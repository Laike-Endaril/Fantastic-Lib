package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.component.*;
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
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;

public class CBipedAnimation extends Component
{
    public static Field RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD;
    public static Field LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD;
    public static Field LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD;
    public static Field LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD;


    public static final HashMap<Entity, ArrayList<CBipedAnimation>> ANIMATION_DATA = new HashMap<>();
    protected static boolean initialized = false;


    public UUID id = UUID.randomUUID();
    public long startTime = 0, pauseTime = -1, pauseAt = Long.MIN_VALUE, removeAt = Long.MIN_VALUE; //Long.MIN_VALUE means never
    double rate = 1;
    public CModelRendererAnimation head, chest, leftArm, rightArm, leftLeg, rightLeg, leftItem, rightItem;
    public CPath.CPathData handItemSwap = new CPath.CPathData(); //Renders items in hands swapped when the current value < 0
    public boolean bodyFacesLookDirection = false;


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
        //For the "current" (combined) animation, fields and methods related to times, rates, and pausing should not be used, because they may differ between parts of combined animation
        //Instead, they can be used in individual animations gotten from the main static map-of-lists (before getting the combined animation if changing data)
        CBipedAnimation result = new CBipedAnimation();
        CPath.CPathData[] resultData = result.getAllData();
        ArrayList<CBipedAnimation> toRemove = new ArrayList<>();
        ArrayList<CBipedAnimation> animations = ANIMATION_DATA.getOrDefault(entity, new ArrayList<>());
        for (CBipedAnimation animation : animations)
        {
            //Auto-pause
            if (animation.pauseAt != Long.MIN_VALUE && animation.pauseTime == -1)
            {
                long runTime = (long) ((System.currentTimeMillis() - animation.startTime) * animation.rate);
                if (runTime >= animation.pauseAt) animation.pauseAllInternal(System.currentTimeMillis() - (runTime - animation.pauseAt));
            }

            //Auto-end
            if (animation.removeAt != Long.MIN_VALUE)
            {
                if (animation.pauseTime > -1)
                {
                    if ((animation.pauseTime - animation.startTime) * animation.rate > animation.removeAt)
                    {
                        toRemove.add(animation);
                        continue;
                    }
                }
                else
                {
                    if ((System.currentTimeMillis() - animation.startTime) * animation.rate > animation.removeAt)
                    {
                        toRemove.add(animation);
                        continue;
                    }
                }
            }

            //Combine
            int i = 0;
            for (CPath.CPathData data : animation.getAllData())
            {
                if (data.path != null)
                {
                    CPath.CPathData data2 = resultData[i];
                    data2.path = data.path;
                    data2.startTime = data.startTime;
                    data2.pauseTime = data.pauseTime;
                    data2.rate = data.rate;
                }
                i++;
            }
        }

        //Remove ended animations
        animations.removeAll(toRemove);

        return result;
    }


    public static void addAnimation(Entity entity, CBipedAnimation animation)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new ArrayList<>()).add(animation);
        if (!entity.world.isRemote)
        {
            MCTools.sendToAllTracking(Network.WRAPPER, new Network.AddBipedAnimationsPacket(entity, new CBipedAnimation[]{animation}), entity);
        }
    }

    public static void updateAnimation(Entity entity, CBipedAnimation animation)
    {
        if (entity.world.isRemote) throw new IllegalStateException("This method should not be called on the client side!");

        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list != null && list.remove(animation))
        {
            MCTools.sendToAllTracking(Network.WRAPPER, new Network.UpdateBipedAnimationsPacket(entity, animation), entity);
        }
    }

    public static void removeAnimation(Entity entity, CBipedAnimation animation)
    {
        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list != null)
        {
            list.remove(animation);
            if (!entity.world.isRemote) MCTools.sendToAllTracking(Network.WRAPPER, new Network.RemoveBipedAnimationPacket(entity, animation), entity);
        }
    }

    @SideOnly(Side.CLIENT)
    public static boolean removeAnimations(Entity entity, UUID id)
    {
        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list == null) return false;

        int size = list.size();
        list.removeIf(animation -> animation.id.equals(id));
        return list.size() != size;
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

    public CBipedAnimation setAllRates(double rate)
    {
        this.rate = rate;
        for (CPath.CPathData data : getAllData()) data.rate = rate;
        return this;
    }

    public CBipedAnimation setAllStartTimes(long time)
    {
        startTime = time;
        for (CPath.CPathData data : getAllData()) data.startTime = time;
        return this;
    }

    public CBipedAnimation pauseAll(@Nullable Entity entity)
    {
        return pauseAll(entity, System.currentTimeMillis());
    }

    public CBipedAnimation pauseAll(@Nullable Entity entity, long time)
    {
        pauseAllInternal(time);
        if (entity != null && !entity.world.isRemote)
        {
            MCTools.sendToAllTracking(Network.WRAPPER, new Network.UpdateBipedAnimationsPacket(entity, new CBipedAnimation[]{this}), entity);
        }
        return this;
    }

    protected CBipedAnimation pauseAllInternal(long time)
    {
        pauseTime = time;
        for (CPath.CPathData data : getAllData()) data.pause(time);
        return this;
    }

    public CBipedAnimation unpauseAll(@Nullable Entity entity)
    {
        return unpauseAll(entity, System.currentTimeMillis());
    }

    public CBipedAnimation unpauseAll(@Nullable Entity entity, long time)
    {
        if (pauseTime > -1)
        {
            startTime += time - pauseTime;
            pauseTime = -1;
        }
        for (CPath.CPathData data : getAllData()) data.unpause(time);
        return this;
    }


    @Override
    public CBipedAnimation write(ByteBuf buf)
    {
        new CUUID().set(id).write(buf);

        buf.writeDouble(rate);

        buf.writeLong(startTime);
        buf.writeLong(pauseTime);
        buf.writeLong(pauseAt);
        buf.writeLong(removeAt);

        writeMarkedOrNull(buf, handItemSwap);

        head.write(buf);
        chest.write(buf);
        leftArm.write(buf);
        rightArm.write(buf);
        leftLeg.write(buf);
        rightLeg.write(buf);
        leftItem.write(buf);
        rightItem.write(buf);

        buf.writeBoolean(bodyFacesLookDirection);

        return this;
    }

    @Override
    public CBipedAnimation read(ByteBuf buf)
    {
        id = new CUUID().read(buf).value;

        rate = buf.readDouble();

        startTime = buf.readLong();
        pauseTime = buf.readLong();
        pauseAt = buf.readLong();
        removeAt = buf.readLong();

        handItemSwap = (CPath.CPathData) readMarkedOrNull(buf);

        head.read(buf);
        chest.read(buf);
        leftArm.read(buf);
        rightArm.read(buf);
        leftLeg.read(buf);
        rightLeg.read(buf);
        leftItem.read(buf);
        rightItem.read(buf);

        bodyFacesLookDirection = buf.readBoolean();

        return this;
    }

    @Override
    public CBipedAnimation save(OutputStream stream)
    {
        new CUUID().set(id).save(stream);

        new CDouble().set(rate).save(stream);

        new CLong().set(startTime).save(stream).set(pauseTime).save(stream).set(pauseAt).save(stream).set(removeAt).save(stream);

        saveMarkedOrNull(stream, handItemSwap);

        head.save(stream);
        chest.save(stream);
        leftArm.save(stream);
        rightArm.save(stream);
        leftLeg.save(stream);
        rightLeg.save(stream);
        leftItem.save(stream);
        rightItem.save(stream);

        new CBoolean().set(bodyFacesLookDirection).save(stream);

        return this;
    }

    @Override
    public CBipedAnimation load(InputStream stream)
    {
        id = new CUUID().load(stream).value;

        rate = new CDouble().load(stream).value;

        CLong cl = new CLong();
        startTime = cl.load(stream).value;
        pauseTime = cl.load(stream).value;
        pauseAt = cl.load(stream).value;
        removeAt = cl.load(stream).value;

        handItemSwap = (CPath.CPathData) loadMarkedOrNull(stream);

        head.load(stream);
        chest.load(stream);
        leftArm.load(stream);
        rightArm.load(stream);
        leftLeg.load(stream);
        rightLeg.load(stream);
        leftItem.load(stream);
        rightItem.load(stream);

        bodyFacesLookDirection = new CBoolean().load(stream).value;

        return this;
    }


    public static void init(FMLPostInitializationEvent event)
    {
        if (!initialized)
        {
            initialized = true;

            MinecraftForge.EVENT_BUS.register(CBipedAnimation.class);
            MinecraftForge.EVENT_BUS.register(ModelPlayerEdit.class);

            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            {
                ClientTickTimer.schedule(1, () ->
                {
                    RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers");
                    LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD = ReflectionTool.getField(LayerArmorBase.class, "field_177189_c", "modelLeggings");
                    LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD = ReflectionTool.getField(LayerArmorBase.class, "field_177186_d", "modelArmor");
                    LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD = ReflectionTool.getField(LayerHeldItem.class, "field_177206_a", "livingEntityRenderer");

                    Field renderLivingBaseMainModelField = ReflectionTool.getField(RenderLivingBase.class, "field_77045_g", "mainModel");
                    RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();
                    Map<String, RenderPlayer> map = renderManager.getSkinMap();
                    ReflectionTool.set(renderLivingBaseMainModelField, map.get("default"), new ModelPlayerEdit(0, false));
                    ReflectionTool.set(renderLivingBaseMainModelField, map.get("slim"), new ModelPlayerEdit(0, true));

                    for (Render<? extends Entity> render : renderManager.entityRenderMap.values())
                    {
                        if (!(render instanceof RenderLivingBase)) continue;

                        ModelBase oldModel = ((RenderLivingBase) render).getMainModel();
                        if (oldModel == null) continue;

                        if (oldModel.getClass() == ModelPlayer.class)
                        {
                            ReflectionTool.set(renderLivingBaseMainModelField, render, new ModelPlayerEdit((ModelPlayer) oldModel));
                        }
                        else if (oldModel.getClass() == ModelBiped.class)
                        {
                            ReflectionTool.set(renderLivingBaseMainModelField, render, new ModelBipedEdit((ModelBiped) oldModel));
                        }
                    }

                });
            }
        }
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

        EntityLivingBase entity = event.getEntity();
        ArrayList<CBipedAnimation> animations = ANIMATION_DATA.get(entity);
        if (animations != null)
        {
            for (CBipedAnimation animation : animations)
            {
                if (animation.bodyFacesLookDirection)
                {
                    entity.setRenderYawOffset(entity.rotationYawHead);
                    break;
                }
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
