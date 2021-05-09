package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;
import moe.plushie.armourers_workshop.client.render.SkinModelRenderHelper;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
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
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
            SKIN_MODEL_RENDER_HELPER_MODEL_HEAD_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelHead"),
            SKIN_MODEL_RENDER_HELPER_MODEL_CHEST_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelChest"),
            SKIN_MODEL_RENDER_HELPER_MODEL_LEGS_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelLegs"),
            SKIN_MODEL_RENDER_HELPER_MODEL_FEET_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelFeet"),
            SKIN_MODEL_RENDER_HELPER_MODEL_WINGS_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelWings"),
            SKIN_MODEL_RENDER_HELPER_MODEL_OUTFIT_FIELD = ReflectionTool.getField(SkinModelRenderHelper.class, "modelOutfit");


    public static final HashMap<Entity, CBipedAnimation> ANIMATION_DATA = new HashMap<>();


    public CModelRendererAnimation head, chest, leftArm, rightArm, leftLeg, rightLeg;


    public CBipedAnimation()
    {
        this(new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation());
    }

    public CBipedAnimation(CModelRendererAnimation head, CModelRendererAnimation chest, CModelRendererAnimation leftArm, CModelRendererAnimation rightArm, CModelRendererAnimation leftLeg, CModelRendererAnimation rightLeg)
    {
        this.head = head;
        this.chest = chest;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }


    public static void setHeadXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.xPath = path;
    }

    public static void setHeadYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.yPath = path;
    }

    public static void setHeadZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.zPath = path;
    }

    public static void setHeadXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.xRotPath = path;
    }

    public static void setHeadYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.yRotPath = path;
    }

    public static void setHeadZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.zRotPath = path;
    }

    public static void setHeadXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.xScalePath = path;
    }

    public static void setHeadYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.yScalePath = path;
    }

    public static void setHeadZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).head.zScalePath = path;
    }


    public static void setChestXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.xPath = path;
    }

    public static void setChestYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.yPath = path;
    }

    public static void setChestZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.zPath = path;
    }

    public static void setChestXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.xRotPath = path;
    }

    public static void setChestYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.yRotPath = path;
    }

    public static void setChestZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.zRotPath = path;
    }

    public static void setChestXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.xScalePath = path;
    }

    public static void setChestYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.yScalePath = path;
    }

    public static void setChestZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).chest.zScalePath = path;
    }


    public static void setLeftArmXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.xPath = path;
    }

    public static void setLeftArmYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.yPath = path;
    }

    public static void setLeftArmZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.zPath = path;
    }

    public static void setLeftArmXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.xRotPath = path;
    }

    public static void setLeftArmYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.yRotPath = path;
    }

    public static void setLeftArmZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.zRotPath = path;
    }

    public static void setLeftArmXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.xScalePath = path;
    }

    public static void setLeftArmYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.yScalePath = path;
    }

    public static void setLeftArmZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftArm.zScalePath = path;
    }


    public static void setRightArmXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.xPath = path;
    }

    public static void setRightArmYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.yPath = path;
    }

    public static void setRightArmZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.zPath = path;
    }

    public static void setRightArmXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.xRotPath = path;
    }

    public static void setRightArmYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.yRotPath = path;
    }

    public static void setRightArmZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.zRotPath = path;
    }

    public static void setRightArmXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.xScalePath = path;
    }

    public static void setRightArmYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.yScalePath = path;
    }

    public static void setRightArmZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightArm.zScalePath = path;
    }


    public static void setLeftLegXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.xPath = path;
    }

    public static void setLeftLegYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.yPath = path;
    }

    public static void setLeftLegZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.zPath = path;
    }

    public static void setLeftLegXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.xRotPath = path;
    }

    public static void setLeftLegYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.yRotPath = path;
    }

    public static void setLeftLegZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.zRotPath = path;
    }

    public static void setLeftLegXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.xScalePath = path;
    }

    public static void setLeftLegYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.yScalePath = path;
    }

    public static void setLeftLegZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).leftLeg.zScalePath = path;
    }


    public static void setRightLegXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.xPath = path;
    }

    public static void setRightLegYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.yPath = path;
    }

    public static void setRightLegZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.zPath = path;
    }

    public static void setRightLegXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.xRotPath = path;
    }

    public static void setRightLegYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.yRotPath = path;
    }

    public static void setRightLegZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.zRotPath = path;
    }

    public static void setRightLegXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.xScalePath = path;
    }

    public static void setRightLegYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.yScalePath = path;
    }

    public static void setRightLegZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CBipedAnimation()).rightLeg.zScalePath = path;
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

            if (Loader.isModLoaded("armourers_workshop"))
            {
                ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_HEAD_FIELD, SkinModelRenderHelper.INSTANCE, new ModelSkinHeadEdit());
                SkinModelRenderHelper.INSTANCE.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinHead, SkinModelRenderHelper.INSTANCE.modelHead);

                ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_CHEST_FIELD, SkinModelRenderHelper.INSTANCE, new ModelSkinChestEdit());
                SkinModelRenderHelper.INSTANCE.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinChest, SkinModelRenderHelper.INSTANCE.modelChest);

                ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_LEGS_FIELD, SkinModelRenderHelper.INSTANCE, new ModelSkinLegsEdit());
                SkinModelRenderHelper.INSTANCE.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinLegs, SkinModelRenderHelper.INSTANCE.modelLegs);

                ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_FEET_FIELD, SkinModelRenderHelper.INSTANCE, new ModelSkinFeetEdit());
                SkinModelRenderHelper.INSTANCE.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinFeet, SkinModelRenderHelper.INSTANCE.modelFeet);

                ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_WINGS_FIELD, SkinModelRenderHelper.INSTANCE, new ModelSkinWingsEdit());
                SkinModelRenderHelper.INSTANCE.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinWings, SkinModelRenderHelper.INSTANCE.modelWings);

                ReflectionTool.set(SKIN_MODEL_RENDER_HELPER_MODEL_OUTFIT_FIELD, SkinModelRenderHelper.INSTANCE, new ModelSkinOutfitEdit());
                SkinModelRenderHelper.INSTANCE.registerSkinTypeHelperForModel(SkinModelRenderHelper.ModelType.MODEL_BIPED, SkinTypeRegistry.skinOutfit, SkinModelRenderHelper.INSTANCE.modelOutfit);
            }
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
        }
    }
}