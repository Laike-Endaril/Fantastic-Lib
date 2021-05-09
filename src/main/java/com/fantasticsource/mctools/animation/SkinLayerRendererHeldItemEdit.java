package com.fantasticsource.mctools.animation;

import com.fantasticsource.mctools.aw.ForcedAWSkinOverrides;
import com.fantasticsource.tools.ReflectionTool;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkin;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SkinLayerRendererHeldItemEdit extends LayerHeldItem
{
    public static final Class AW_MOD_ADDON_MANAGER_ITEM_OVERRIDE_TYPE_ENUM = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.addons.ModAddonManager$ItemOverrideType"),
            AW_MOD_ADDON_MANAGER_CLASS = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.addons.ModAddonManager"),
            AW_MODEL_SKIN_BOW_CLASS = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.client.model.skin.ModelSkinBow"),
            AW_I_EQUIPMENT_MODEL_INTERFACE = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.client.model.skin.IEquipmentModel"),
            AW_CLIENT_SKIN_CACHE_CLASS = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache");

    public static final Field
            SKIN_MODEL_RENDER_HELPER_MODEL_BOW_FIELD = ReflectionTool.getField(CBipedAnimation.AW_SKIN_MODEL_RENDER_HELPER_CLASS, "modelBow"),
            MODEL_SKIN_BOW_FRAME_FIELD = ReflectionTool.getField(AW_MODEL_SKIN_BOW_CLASS, "frame"),
            CLIENT_SKIN_CACHE_INSTANCE_FIELD = ReflectionTool.getField(AW_CLIENT_SKIN_CACHE_CLASS, "INSTANCE");

    public static final Method
            MOD_ADDON_MANAGER_IS_OVERRIDE_ITEM_METHOD = ReflectionTool.getMethod(AW_MOD_ADDON_MANAGER_CLASS, "isOverrideItem"),
            SKIN_MODEL_RENDER_HELPER_GET_TYPE_HELPER_FOR_MODEL_METHOD = ReflectionTool.getMethod(CBipedAnimation.AW_SKIN_MODEL_RENDER_HELPER_CLASS, "getTypeHelperForModel"),
            MODEL_SKIN_BOW_RENDER_METHOD = ReflectionTool.getMethod(AW_MODEL_SKIN_BOW_CLASS, 8, "render"),
            I_EQUIPMENT_MODEL_RENDER_METHOD = ReflectionTool.getMethod(AW_I_EQUIPMENT_MODEL_INTERFACE, 9, "render"),
            CLIENT_SKIN_CACHE_GET_SKIN_METHOD = ReflectionTool.getMethod(AW_CLIENT_SKIN_CACHE_CLASS, new Class[]{ISkinDescriptor.class}, "getSkin");


    public float[] leftItemScale = null, rightItemScale = null;

    public LayerHeldItem awLayer;

    public SkinLayerRendererHeldItemEdit(RenderLivingBase<?> livingEntityRendererIn, LayerHeldItem awLayer)
    {
        super(livingEntityRendererIn);
        this.awLayer = awLayer;
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack leftStack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack rightStack = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

        if (!leftStack.isEmpty() || !rightStack.isEmpty())
        {
            IEntitySkinCapability skinCapability = ArmourersWorkshopApi.getEntitySkinCapability(entitylivingbaseIn);
            GlStateManager.pushMatrix();

            if (livingEntityRenderer.getMainModel().isChild)
            {
                GlStateManager.translate(0.0F, 0.75F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            ForcedAWSkinOverrides.tryEnableAWSkinOverrideHack(rightStack);
            renderHeldItem(entitylivingbaseIn, rightStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability);
            ForcedAWSkinOverrides.tryDisableAWSkinOverrideHack(rightStack);

            ForcedAWSkinOverrides.tryEnableAWSkinOverrideHack(leftStack);
            renderHeldItem(entitylivingbaseIn, leftStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability);
            ForcedAWSkinOverrides.tryDisableAWSkinOverrideHack(leftStack);

            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack itemStack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, IEntitySkinCapability skinCapability)
    {
        if (!itemStack.isEmpty())
        {
            GlStateManager.pushMatrix();

            if (entityLivingBase.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }
            translateToHand(handSide);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);

            ISkinTypeRegistry skinTypeRegistry = ArmourersWorkshopApi.skinTypeRegistry;
            ISkinType[] skinTypes = new ISkinType[]
                    {
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:sword"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:shield"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:bow"),

                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:pickaxe"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:axe"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:shovel"),
                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:hoe"),

                            skinTypeRegistry.getSkinTypeFromRegistryName("armourers:item"),
                    };

            boolean slim = false;
            if (entityLivingBase instanceof EntityPlayer)
            {
                slim = ModelPlayerEdit.isSmallArms((ModelPlayer) livingEntityRenderer.getMainModel());
            }

            boolean didRender = false;
            Object overriteTypeBow = AW_MOD_ADDON_MANAGER_ITEM_OVERRIDE_TYPE_ENUM.getEnumConstants()[2];
            for (int i = 0; i < AW_MOD_ADDON_MANAGER_ITEM_OVERRIDE_TYPE_ENUM.getEnumConstants().length; i++)
            {
                Object overrideType = AW_MOD_ADDON_MANAGER_ITEM_OVERRIDE_TYPE_ENUM.getEnumConstants()[i];
                if ((boolean) ReflectionTool.invoke(MOD_ADDON_MANAGER_IS_OVERRIDE_ITEM_METHOD, null, overrideType, itemStack.getItem()))
                {
                    ISkinDescriptor descriptor = ArmourersWorkshopApi.getSkinNBTUtils().getSkinDescriptor(itemStack);
                    if (descriptor == null) descriptor = skinCapability.getSkinDescriptor(skinTypes[i], 0);
                    if (descriptor != null)
                    {
                        GlStateManager.pushMatrix();
                        GlStateManager.enableCull();
                        GlStateManager.scale(-1, -1, 1);
                        GlStateManager.translate(0, 0.0625F * 2, 0.0625F * 2);
                        if (flag)
                        {
                            GlStateManager.scale(-1, 1, 1);
                            GlStateManager.cullFace(GlStateManager.CullFace.FRONT);
                        }
                        Object skinModelRenderHelper = ReflectionTool.get(CBipedAnimation.SKIN_MODEL_RENDER_HELPER_INSTANCE_FIELD, null);
                        Object clientSkinCache = ReflectionTool.get(CLIENT_SKIN_CACHE_INSTANCE_FIELD, null);
                        ISkin skin = (ISkin) ReflectionTool.invoke(CLIENT_SKIN_CACHE_GET_SKIN_METHOD, clientSkinCache, descriptor);
                        if (skin != null)
                        {
                            if (overrideType != overriteTypeBow)
                            {
                                if (slim) GL11.glScaled(0.75F, 1F, 1F);
                                Object model = ReflectionTool.invoke(SKIN_MODEL_RENDER_HELPER_GET_TYPE_HELPER_FOR_MODEL_METHOD, skinModelRenderHelper, CBipedAnimation.SKIN_MODEL_RENDER_HELPER_MODEL_TYPE_ENUM.getEnumConstants()[0], descriptor.getIdentifier().getSkinType());


                                //FLib compat start
                                CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityLivingBase);
                                if (playerAnimation != null)
                                {
                                    long millis = System.currentTimeMillis();

                                    if (handSide == EnumHandSide.LEFT)
                                    {
                                        if (playerAnimation.leftItem.xScalePath != null)
                                        {
                                            leftItemScale = new float[]{(float) playerAnimation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.leftItem.yScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else leftItemScale[1] = (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.leftItem.zScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else leftItemScale[2] = (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        GL11.glTranslatef(playerAnimation.leftItem.xPath == null ? 0 : (float) playerAnimation.leftItem.xPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.yPath == null ? 0 : -(float) playerAnimation.leftItem.yPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.zPath == null ? 0 : (float) playerAnimation.leftItem.zPath.getRelativePosition(millis).values[0]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0]), 0, 0, -1);
                                        if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0]), 0, 1, 0);
                                        if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0]), -1, 0, 0);
                                    }
                                    else
                                    {
                                        if (playerAnimation.rightItem.xScalePath != null)
                                        {
                                            rightItemScale = new float[]{(float) playerAnimation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.rightItem.yScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else rightItemScale[1] = (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.rightItem.zScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else rightItemScale[2] = (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        GL11.glTranslatef(playerAnimation.rightItem.xPath == null ? 0 : -(float) playerAnimation.rightItem.xPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.yPath == null ? 0 : -(float) playerAnimation.rightItem.yPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.zPath == null ? 0 : (float) playerAnimation.rightItem.zPath.getRelativePosition(millis).values[0]);

                                        //CAREFUL, THIS NEEDED CERTAIN NEGATIONS SPECIFICALLY FOR AW SKINS
                                        if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0]), 0, 0, 1);
                                        if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0]), 0, -1, 0);
                                        if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0]), -1, 0, 0);
                                    }
                                }
                                //FLib compat end


                                ReflectionTool.invoke(I_EQUIPMENT_MODEL_RENDER_METHOD, model, entityLivingBase, skin, livingEntityRenderer.getMainModel(), false, descriptor.getSkinDye(), null, true, 0, true);
                            }
                            else
                            {
                                Object model = ReflectionTool.get(SKIN_MODEL_RENDER_HELPER_MODEL_BOW_FIELD, skinModelRenderHelper);
                                ReflectionTool.set(MODEL_SKIN_BOW_FRAME_FIELD, model, getAnimationFrame(entityLivingBase.getItemInUseMaxCount()));


                                //FLib compat start
                                CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityLivingBase);
                                if (playerAnimation != null)
                                {
                                    long millis = System.currentTimeMillis();

                                    if (handSide == EnumHandSide.LEFT)
                                    {
                                        if (playerAnimation.leftItem.xScalePath != null)
                                        {
                                            leftItemScale = new float[]{(float) playerAnimation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.leftItem.yScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else leftItemScale[1] = (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.leftItem.zScalePath != null)
                                        {
                                            if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else leftItemScale[2] = (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

                                        GL11.glTranslatef(playerAnimation.leftItem.xPath == null ? 0 : (float) playerAnimation.leftItem.xPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.yPath == null ? 0 : (float) playerAnimation.leftItem.yPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.zPath == null ? 0 : (float) playerAnimation.leftItem.zPath.getRelativePosition(millis).values[0]);

                                        if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0]), 0, 0, 1);
                                        if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0]), 0, 1, 0);
                                        if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0]), 1, 0, 0);
                                    }
                                    else
                                    {
                                        if (playerAnimation.rightItem.xScalePath != null)
                                        {
                                            rightItemScale = new float[]{(float) playerAnimation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                                        }
                                        if (playerAnimation.rightItem.yScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                                            else rightItemScale[1] = (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (playerAnimation.rightItem.zScalePath != null)
                                        {
                                            if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                                            else rightItemScale[2] = (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0];
                                        }
                                        if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

                                        GL11.glTranslatef(playerAnimation.rightItem.xPath == null ? 0 : (float) playerAnimation.rightItem.xPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.yPath == null ? 0 : (float) playerAnimation.rightItem.yPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.zPath == null ? 0 : (float) playerAnimation.rightItem.zPath.getRelativePosition(millis).values[0]);

                                        if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0]), 0, 0, 1);
                                        if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0]), 0, 1, 0);
                                        if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0]), 1, 0, 0);
                                    }
                                }
                                //FLib compat end


                                ReflectionTool.invoke(MODEL_SKIN_BOW_RENDER_METHOD, model, entityLivingBase, skin, false, descriptor.getSkinDye(), null, false, 0, false);
                            }
                        }

                        if (flag)
                        {
                            GlStateManager.cullFace(GlStateManager.CullFace.BACK);
                        }
                        GlStateManager.disableCull();
                        GlStateManager.popMatrix();
                        didRender = true;
                        break;
                    }
                }
            }
            if (!didRender)
            {
                //FLib compat start
                CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityLivingBase);
                if (playerAnimation != null)
                {
                    long millis = System.currentTimeMillis();

                    if (handSide == EnumHandSide.LEFT)
                    {
                        if (playerAnimation.leftItem.xScalePath != null)
                        {
                            leftItemScale = new float[]{(float) playerAnimation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                        }
                        if (playerAnimation.leftItem.yScalePath != null)
                        {
                            if (leftItemScale == null) leftItemScale = new float[]{1, (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                            else leftItemScale[1] = (float) playerAnimation.leftItem.yScalePath.getRelativePosition(millis).values[0];
                        }
                        if (playerAnimation.leftItem.zScalePath != null)
                        {
                            if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                            else leftItemScale[2] = (float) playerAnimation.leftItem.zScalePath.getRelativePosition(millis).values[0];
                        }
                        if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

                        GL11.glTranslatef(playerAnimation.leftItem.xPath == null ? 0 : (float) playerAnimation.leftItem.xPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.yPath == null ? 0 : (float) playerAnimation.leftItem.yPath.getRelativePosition(millis).values[0], playerAnimation.leftItem.zPath == null ? 0 : (float) playerAnimation.leftItem.zPath.getRelativePosition(millis).values[0]);

                        if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0]), 0, 0, 1);
                        if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0]), 0, 1, 0);
                        if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0]), 1, 0, 0);
                    }
                    else
                    {
                        if (playerAnimation.rightItem.xScalePath != null)
                        {
                            rightItemScale = new float[]{(float) playerAnimation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                        }
                        if (playerAnimation.rightItem.yScalePath != null)
                        {
                            if (rightItemScale == null) rightItemScale = new float[]{1, (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                            else rightItemScale[1] = (float) playerAnimation.rightItem.yScalePath.getRelativePosition(millis).values[0];
                        }
                        if (playerAnimation.rightItem.zScalePath != null)
                        {
                            if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                            else rightItemScale[2] = (float) playerAnimation.rightItem.zScalePath.getRelativePosition(millis).values[0];
                        }
                        if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

                        GL11.glTranslatef(playerAnimation.rightItem.xPath == null ? 0 : (float) playerAnimation.rightItem.xPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.yPath == null ? 0 : (float) playerAnimation.rightItem.yPath.getRelativePosition(millis).values[0], playerAnimation.rightItem.zPath == null ? 0 : (float) playerAnimation.rightItem.zPath.getRelativePosition(millis).values[0]);

                        if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0]), 0, 0, 1);
                        if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0]), 0, 1, 0);
                        if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0]), 1, 0, 0);
                    }
                }
                //FLib compat end


                Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, itemStack, transformType, flag);
            }
            GlStateManager.popMatrix();
        }
    }

    private int getAnimationFrame(int useCount)
    {
        if (useCount >= 18) return 2;
        if (useCount > 13) return 1;
        return 0;
    }
}
