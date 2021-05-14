package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

public class CBipedAnimation extends Component
{
    public static final Field
            RENDER_LIVING_BASE_MAIN_MODEL_FIELD = ReflectionTool.getField(RenderLivingBase.class, "field_77045_g", "mainModel"),
            RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers"),
            LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD = ReflectionTool.getField(LayerArmorBase.class, "field_177189_c", "modelLeggings"),
            LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD = ReflectionTool.getField(LayerArmorBase.class, "field_177186_d", "modelArmor"),
            LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD = ReflectionTool.getField(LayerHeldItem.class, "field_177206_a", "livingEntityRenderer");


    public static final HashMap<Entity, ArrayList<CBipedAnimation>> ANIMATION_DATA = new HashMap<>();


    public UUID id = UUID.randomUUID();
    public long startTime = 0, duration = Long.MIN_VALUE; //Means infinite duration
    public CModelRendererAnimation head, chest, leftArm, rightArm, leftLeg, rightLeg, leftItem, rightItem;
    public CPath.CPathData handItemSwap = new CPath.CPathData(); //Renders items in hands swapped when the current value < 0


    public CBipedAnimation()
    {
        this(new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation());
    }

    public CBipedAnimation(CModelRendererAnimation head, CModelRendererAnimation chest, CModelRendererAnimation leftArm, CModelRendererAnimation rightArm, CModelRendererAnimation leftLeg, CModelRendererAnimation rightLeg, CModelRendererAnimation leftItem, CModelRendererAnimation rightItem)
    {
        this.head = head;
        this.chest = chest;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
        this.leftItem = leftItem;
        this.rightItem = rightItem;
    }


    public static CBipedAnimation getCurrent(Entity entity)
    {
        CBipedAnimation result = new CBipedAnimation();
        CPath.CPathData[] resultData = result.getAllData();
        ArrayList<CBipedAnimation> toRemove = new ArrayList<>();
        ArrayList<CBipedAnimation> animations = ANIMATION_DATA.getOrDefault(entity, new ArrayList<>());
        for (CBipedAnimation animation : animations)
        {
            if (animation.duration != Long.MIN_VALUE && System.currentTimeMillis() - animation.startTime > animation.duration)
            {
                toRemove.add(animation);
                continue;
            }

            int i = 0;
            for (CPath.CPathData data : animation.getAllData())
            {
                if (data.path != null) resultData[i].path = data.path;
                i++;
            }
        }

        animations.removeAll(toRemove);

        return result;
    }


    public static void addAnimation(Entity entity, CBipedAnimation animation)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new ArrayList<>()).add(animation);
        ArrayList<CBipedAnimation> list = new ArrayList<>();
        if (!entity.world.isRemote) Network.WRAPPER.sendToAllTracking(new Network.AddBipedAnimationsPacket(entity, new CBipedAnimation[]{animation}), entity);
    }

    public static void removeAnimation(Entity entity, CBipedAnimation animation)
    {
        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list != null)
        {
            list.remove(animation);
            if (!entity.world.isRemote) Network.WRAPPER.sendToAllTracking(new Network.RemoveBipedAnimationPacket(entity, animation), entity);
        }
    }

    @SideOnly(Side.CLIENT)
    public static void removeAnimations(Entity entity, UUID id)
    {
        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list != null) list.removeIf(animation -> animation.id.equals(id));
    }


    public CPath.CPathData[] getAllData()
    {
        CPath.CPathData[] result = new CPath.CPathData[1 + 8 * 9];
        result[0] = handItemSwap;
        int i = 1;
        for (CModelRendererAnimation subAnimation : new CModelRendererAnimation[]{head, chest, rightArm, leftArm, rightLeg, leftLeg, rightItem, leftItem})
        {
            for (CPath.CPathData data : subAnimation.getAllData()) result[i++] = data;
        }
        return result;
    }


    @Override
    public CBipedAnimation write(ByteBuf buf)
    {
        new CUUID().set(id).write(buf);

        buf.writeLong(startTime);
        buf.writeLong(duration);

        writeMarkedOrNull(buf, handItemSwap);

        head.write(buf);
        chest.write(buf);
        leftArm.write(buf);
        rightArm.write(buf);
        leftLeg.write(buf);
        rightLeg.write(buf);
        leftItem.write(buf);
        rightItem.write(buf);

        return this;
    }

    @Override
    public CBipedAnimation read(ByteBuf buf)
    {
        id = new CUUID().read(buf).value;

        startTime = buf.readLong();
        duration = buf.readLong();

        handItemSwap = (CPath.CPathData) readMarkedOrNull(buf);

        head.read(buf);
        chest.read(buf);
        leftArm.read(buf);
        rightArm.read(buf);
        leftLeg.read(buf);
        rightLeg.read(buf);
        leftItem.read(buf);
        rightItem.read(buf);

        return this;
    }

    @Override
    public CBipedAnimation save(OutputStream stream)
    {
        new CUUID().set(id).save(stream);

        new CLong().set(startTime).save(stream).set(duration).save(stream);

        saveMarkedOrNull(stream, handItemSwap);

        head.save(stream);
        chest.save(stream);
        leftArm.save(stream);
        rightArm.save(stream);
        leftLeg.save(stream);
        rightLeg.save(stream);
        leftItem.save(stream);
        rightItem.save(stream);

        return this;
    }

    @Override
    public CBipedAnimation load(InputStream stream)
    {
        id = new CUUID().load(stream).value;

        CLong cl = new CLong();
        startTime = cl.load(stream).value;
        duration = cl.load(stream).value;

        handItemSwap = (CPath.CPathData) loadMarkedOrNull(stream);

        head.load(stream);
        chest.load(stream);
        leftArm.load(stream);
        rightArm.load(stream);
        leftLeg.load(stream);
        rightLeg.load(stream);
        leftItem.load(stream);
        rightItem.load(stream);

        return this;
    }


    public static void init(FMLPostInitializationEvent event)
    {
        ClientTickTimer.schedule(1, () ->
        {
            RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
            Map<String, RenderPlayer> map = renderManager.getSkinMap();
            ReflectionTool.set(RENDER_LIVING_BASE_MAIN_MODEL_FIELD, map.get("default"), new ModelPlayerEdit(0, false));
            ReflectionTool.set(RENDER_LIVING_BASE_MAIN_MODEL_FIELD, map.get("slim"), new ModelPlayerEdit(0, true));

            for (Render<? extends Entity> render : renderManager.entityRenderMap.values())
            {
                if (!(render instanceof RenderLivingBase)) continue;

                ModelBase oldModel = ((RenderLivingBase) render).getMainModel();
                if (oldModel.getClass() == ModelPlayer.class)
                {
                    ReflectionTool.set(RENDER_LIVING_BASE_MAIN_MODEL_FIELD, render, new ModelPlayerEdit((ModelPlayer) oldModel));
                }
                else if (oldModel.getClass() == ModelBiped.class)
                {
                    ReflectionTool.set(RENDER_LIVING_BASE_MAIN_MODEL_FIELD, render, new ModelBipedEdit((ModelBiped) oldModel));
                }
            }

            MinecraftForge.EVENT_BUS.register(CBipedAnimation.class);
        });
    }

    @SubscribeEvent
    public static void renderLivingBase(RenderLivingEvent.Pre event)
    {
        List<LayerRenderer> layers = (List<LayerRenderer>) ReflectionTool.get(CBipedAnimation.RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD, event.getRenderer());
        for (int i = 0; i < layers.size(); i++)
        {
            LayerRenderer layer = layers.get(i);
            if (layer instanceof LayerBipedArmor)
            {
                ModelBase leggingsModel = (ModelBase) ReflectionTool.get(LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD, layer);
                ModelBase armorModel = (ModelBase) ReflectionTool.get(LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD, layer);
                if (leggingsModel.getClass() == ModelBiped.class)
                {
                    ReflectionTool.set(LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD, layer, new ModelBipedEdit((ModelBiped) leggingsModel));
                }
                if (armorModel.getClass() == ModelBiped.class)
                {
                    ReflectionTool.set(LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD, layer, new ModelBipedEdit((ModelBiped) armorModel));
                }
            }
            else if (layer.getClass() == LayerHeldItem.class)
            {
                layers.set(i, new LayerHeldItemEdit((RenderLivingBase<?>) ReflectionTool.get(LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD, layer)));
            }
        }
    }

    @SubscribeEvent
    public static void startTracking(PlayerEvent.StartTracking event)
    {
        Entity entity = event.getTarget();
        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list != null && list.size() > 0) Network.WRAPPER.sendTo(new Network.AddBipedAnimationsPacket(entity, list.toArray(new CBipedAnimation[0])), (EntityPlayerMP) event.getEntityPlayer());
    }
}
