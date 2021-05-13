package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.tools.ReflectionTool;
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
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CBipedAnimation extends Component
{
    public static final Field
            RENDER_LIVING_BASE_MAIN_MODEL_FIELD = ReflectionTool.getField(RenderLivingBase.class, "field_77045_g", "mainModel"),
            RENDER_LIVING_BASE_LAYER_RENDERERS_FIELD = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers"),
            LAYER_ARMOR_BASE_MODEL_LEGGINGS_FIELD = ReflectionTool.getField(LayerArmorBase.class, "field_177189_c", "modelLeggings"),
            LAYER_ARMOR_BASE_MODEL_ARMOR_FIELD = ReflectionTool.getField(LayerArmorBase.class, "field_177186_d", "modelArmor"),
            LAYER_HELD_ITEM_LIVING_ENTITY_RENDERER_FIELD = ReflectionTool.getField(LayerHeldItem.class, "field_177206_a", "livingEntityRenderer");


    public static final HashMap<Entity, ArrayList<CBipedAnimation>> ANIMATION_DATA = new HashMap<>();


    public CModelRendererAnimation head, chest, leftArm, rightArm, leftLeg, rightLeg, leftItem, rightItem;
    public CPath handItemSwap = null; //Renders items in hands swapped when the current value < 0


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
        for (CBipedAnimation animation : ANIMATION_DATA.getOrDefault(entity, new ArrayList<>()))
        {
            if (animation.handItemSwap != null) result.handItemSwap = animation.handItemSwap;

            if (animation.head.xPath != null) result.head.xPath = animation.head.xPath;
            if (animation.head.yPath != null) result.head.yPath = animation.head.yPath;
            if (animation.head.zPath != null) result.head.zPath = animation.head.zPath;
            if (animation.head.xRotPath != null) result.head.xRotPath = animation.head.xRotPath;
            if (animation.head.yRotPath != null) result.head.yRotPath = animation.head.yRotPath;
            if (animation.head.zRotPath != null) result.head.zRotPath = animation.head.zRotPath;
            if (animation.head.xScalePath != null) result.head.xScalePath = animation.head.xScalePath;
            if (animation.head.yScalePath != null) result.head.yScalePath = animation.head.yScalePath;
            if (animation.head.zScalePath != null) result.head.zScalePath = animation.head.zScalePath;

            if (animation.chest.xPath != null) result.chest.xPath = animation.chest.xPath;
            if (animation.chest.yPath != null) result.chest.yPath = animation.chest.yPath;
            if (animation.chest.zPath != null) result.chest.zPath = animation.chest.zPath;
            if (animation.chest.xRotPath != null) result.chest.xRotPath = animation.chest.xRotPath;
            if (animation.chest.yRotPath != null) result.chest.yRotPath = animation.chest.yRotPath;
            if (animation.chest.zRotPath != null) result.chest.zRotPath = animation.chest.zRotPath;
            if (animation.chest.xScalePath != null) result.chest.xScalePath = animation.chest.xScalePath;
            if (animation.chest.yScalePath != null) result.chest.yScalePath = animation.chest.yScalePath;
            if (animation.chest.zScalePath != null) result.chest.zScalePath = animation.chest.zScalePath;

            if (animation.leftArm.xPath != null) result.leftArm.xPath = animation.leftArm.xPath;
            if (animation.leftArm.yPath != null) result.leftArm.yPath = animation.leftArm.yPath;
            if (animation.leftArm.zPath != null) result.leftArm.zPath = animation.leftArm.zPath;
            if (animation.leftArm.xRotPath != null) result.leftArm.xRotPath = animation.leftArm.xRotPath;
            if (animation.leftArm.yRotPath != null) result.leftArm.yRotPath = animation.leftArm.yRotPath;
            if (animation.leftArm.zRotPath != null) result.leftArm.zRotPath = animation.leftArm.zRotPath;
            if (animation.leftArm.xScalePath != null) result.leftArm.xScalePath = animation.leftArm.xScalePath;
            if (animation.leftArm.yScalePath != null) result.leftArm.yScalePath = animation.leftArm.yScalePath;
            if (animation.leftArm.zScalePath != null) result.leftArm.zScalePath = animation.leftArm.zScalePath;

            if (animation.rightArm.xPath != null) result.rightArm.xPath = animation.rightArm.xPath;
            if (animation.rightArm.yPath != null) result.rightArm.yPath = animation.rightArm.yPath;
            if (animation.rightArm.zPath != null) result.rightArm.zPath = animation.rightArm.zPath;
            if (animation.rightArm.xRotPath != null) result.rightArm.xRotPath = animation.rightArm.xRotPath;
            if (animation.rightArm.yRotPath != null) result.rightArm.yRotPath = animation.rightArm.yRotPath;
            if (animation.rightArm.zRotPath != null) result.rightArm.zRotPath = animation.rightArm.zRotPath;
            if (animation.rightArm.xScalePath != null) result.rightArm.xScalePath = animation.rightArm.xScalePath;
            if (animation.rightArm.yScalePath != null) result.rightArm.yScalePath = animation.rightArm.yScalePath;
            if (animation.rightArm.zScalePath != null) result.rightArm.zScalePath = animation.rightArm.zScalePath;

            if (animation.leftLeg.xPath != null) result.leftLeg.xPath = animation.leftLeg.xPath;
            if (animation.leftLeg.yPath != null) result.leftLeg.yPath = animation.leftLeg.yPath;
            if (animation.leftLeg.zPath != null) result.leftLeg.zPath = animation.leftLeg.zPath;
            if (animation.leftLeg.xRotPath != null) result.leftLeg.xRotPath = animation.leftLeg.xRotPath;
            if (animation.leftLeg.yRotPath != null) result.leftLeg.yRotPath = animation.leftLeg.yRotPath;
            if (animation.leftLeg.zRotPath != null) result.leftLeg.zRotPath = animation.leftLeg.zRotPath;
            if (animation.leftLeg.xScalePath != null) result.leftLeg.xScalePath = animation.leftLeg.xScalePath;
            if (animation.leftLeg.yScalePath != null) result.leftLeg.yScalePath = animation.leftLeg.yScalePath;
            if (animation.leftLeg.zScalePath != null) result.leftLeg.zScalePath = animation.leftLeg.zScalePath;

            if (animation.rightLeg.xPath != null) result.rightLeg.xPath = animation.rightLeg.xPath;
            if (animation.rightLeg.yPath != null) result.rightLeg.yPath = animation.rightLeg.yPath;
            if (animation.rightLeg.zPath != null) result.rightLeg.zPath = animation.rightLeg.zPath;
            if (animation.rightLeg.xRotPath != null) result.rightLeg.xRotPath = animation.rightLeg.xRotPath;
            if (animation.rightLeg.yRotPath != null) result.rightLeg.yRotPath = animation.rightLeg.yRotPath;
            if (animation.rightLeg.zRotPath != null) result.rightLeg.zRotPath = animation.rightLeg.zRotPath;
            if (animation.rightLeg.xScalePath != null) result.rightLeg.xScalePath = animation.rightLeg.xScalePath;
            if (animation.rightLeg.yScalePath != null) result.rightLeg.yScalePath = animation.rightLeg.yScalePath;
            if (animation.rightLeg.zScalePath != null) result.rightLeg.zScalePath = animation.rightLeg.zScalePath;

            if (animation.leftItem.xPath != null) result.leftItem.xPath = animation.leftItem.xPath;
            if (animation.leftItem.yPath != null) result.leftItem.yPath = animation.leftItem.yPath;
            if (animation.leftItem.zPath != null) result.leftItem.zPath = animation.leftItem.zPath;
            if (animation.leftItem.xRotPath != null) result.leftItem.xRotPath = animation.leftItem.xRotPath;
            if (animation.leftItem.yRotPath != null) result.leftItem.yRotPath = animation.leftItem.yRotPath;
            if (animation.leftItem.zRotPath != null) result.leftItem.zRotPath = animation.leftItem.zRotPath;
            if (animation.leftItem.xScalePath != null) result.leftItem.xScalePath = animation.leftItem.xScalePath;
            if (animation.leftItem.yScalePath != null) result.leftItem.yScalePath = animation.leftItem.yScalePath;
            if (animation.leftItem.zScalePath != null) result.leftItem.zScalePath = animation.leftItem.zScalePath;

            if (animation.rightItem.xPath != null) result.rightItem.xPath = animation.rightItem.xPath;
            if (animation.rightItem.yPath != null) result.rightItem.yPath = animation.rightItem.yPath;
            if (animation.rightItem.zPath != null) result.rightItem.zPath = animation.rightItem.zPath;
            if (animation.rightItem.xRotPath != null) result.rightItem.xRotPath = animation.rightItem.xRotPath;
            if (animation.rightItem.yRotPath != null) result.rightItem.yRotPath = animation.rightItem.yRotPath;
            if (animation.rightItem.zRotPath != null) result.rightItem.zRotPath = animation.rightItem.zRotPath;
            if (animation.rightItem.xScalePath != null) result.rightItem.xScalePath = animation.rightItem.xScalePath;
            if (animation.rightItem.yScalePath != null) result.rightItem.yScalePath = animation.rightItem.yScalePath;
            if (animation.rightItem.zScalePath != null) result.rightItem.zScalePath = animation.rightItem.zScalePath;
        }

        return result;
    }


    public static void addAnimation(Entity entity, CBipedAnimation animation)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new ArrayList<>()).add(animation);
    }

    public static void removeAnimation(Entity entity, CBipedAnimation animation)
    {
        ArrayList<CBipedAnimation> list = ANIMATION_DATA.get(entity);
        if (list != null) list.remove(animation);
    }


    @Override
    public CBipedAnimation write(ByteBuf buf)
    {
        head.write(buf);
        chest.write(buf);
        leftArm.write(buf);
        rightArm.write(buf);
        leftLeg.write(buf);
        rightLeg.write(buf);
        leftItem.write(buf);
        rightItem.write(buf);

        writeMarkedOrNull(buf, handItemSwap);

        return this;
    }

    @Override
    public CBipedAnimation read(ByteBuf buf)
    {
        head.read(buf);
        chest.read(buf);
        leftArm.read(buf);
        rightArm.read(buf);
        leftLeg.read(buf);
        rightLeg.read(buf);
        leftItem.read(buf);
        rightItem.read(buf);

        handItemSwap = (CPath) readMarkedOrNull(buf);

        return this;
    }

    @Override
    public CBipedAnimation save(OutputStream stream)
    {
        head.save(stream);
        chest.save(stream);
        leftArm.save(stream);
        rightArm.save(stream);
        leftLeg.save(stream);
        rightLeg.save(stream);
        leftItem.save(stream);
        rightItem.save(stream);

        saveMarkedOrNull(stream, handItemSwap);

        return this;
    }

    @Override
    public CBipedAnimation load(InputStream stream)
    {
        head.load(stream);
        chest.load(stream);
        leftArm.load(stream);
        rightArm.load(stream);
        leftLeg.load(stream);
        rightLeg.load(stream);
        leftItem.load(stream);
        rightItem.load(stream);

        handItemSwap = (CPath) loadMarkedOrNull(stream);

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
}
