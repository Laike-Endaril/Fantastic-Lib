package com.fantasticsource.mctools.animation;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinOutfit;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class ModelSkinOutfitEdit extends ModelSkinOutfit
{
    public float[] headScale = null, chestScale = null, leftArmScale = null, rightArmScale = null, leftLegScale = null, rightLegScale = null;

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading)
    {
        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
    }

    @Override
    public void render(Entity entity, Skin skin, SkinRenderData renderData)
    {
        if (skin == null)
        {
            return;
        }

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            this.isSneak = player.isSneaking();
            this.isRiding = player.isRiding();
        }

        GlStateManager.pushAttrib();
        RenderHelper.enableGUIStandardItemLighting();

        if (skin.hasPaintData() & renderData.isShowSkinPaint() & ClientProxy.getTexturePaintType() == ClientProxy.TexturePaintType.TEXTURE_REPLACE)
        {
            SkinModelTexture st = ClientSkinPaintCache.INSTANCE.getTextureForSkin(skin, renderData.getSkinDye(), renderData.getExtraColours());
            st.bindTexture();
            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            if (!renderData.isItemRender())
            {
                GlStateManager.enablePolygonOffset();
                GlStateManager.doPolygonOffset(-2, 1);
            }
            bipedHead.render(SCALE);
            bipedBody.render(SCALE);
            bipedLeftArm.render(SCALE);
            bipedRightArm.render(SCALE);

            GL11.glTranslated(0, 0, 0.005F);
            GL11.glTranslated(0.02F, 0, 0);
            bipedLeftLeg.render(SCALE);
            GL11.glTranslated(-0.02F, 0, 0);
            bipedRightLeg.render(SCALE);
            GL11.glTranslated(0, 0, -0.005F);
            if (!renderData.isItemRender())
            {
                GlStateManager.doPolygonOffset(0F, 0F);
                GlStateManager.disablePolygonOffset();
            }
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        boolean overrideChest = SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skin.getProperties());

        double angle;

        int i = 0;
        for (SkinPart skinPart : skin.getParts())
        {
            //FLib compat start
            CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entity);
            long millis = System.currentTimeMillis();
            //FLib compat end


            GL11.glPushMatrix();

            if (skinPart.getPartType().getRegistryName().equals("armourers:head.base"))
            {
                boolean doHead = true;
                // Fix to stop head skins rendering when using the Real First-Person Render mod.
                if (entity != null && entity.equals(Minecraft.getMinecraft().player))
                {
                    if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
                    {
                        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
                        for (StackTraceElement traceElement : traceElements)
                        {
                            if (traceElement.toString().contains("realrender") | traceElement.toString().contains("rfpf"))
                            {
                                doHead = false;
                                break;
                            }
                        }
                    }
                }
                if (doHead)
                {
                    if (isChild)
                    {
                        GL11.glScalef(0.75f, 0.75f, 0.75f);
                        GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
                    }

                    if (isSneak)
                    {
                        GlStateManager.translate(0.0F, 0.2F, 0.0F);
                        GlStateManager.translate(0.0F, SCALE, 0.0F);
                    }

                    //FLib compat start
                    if (playerAnimation != null)
                    {
                        if (playerAnimation.head.xPath != null) bipedHead.offsetX = (float) playerAnimation.head.xPath.getRelativePosition(millis).values[0];
                        if (playerAnimation.head.yPath != null) bipedHead.offsetY = (float) playerAnimation.head.yPath.getRelativePosition(millis).values[0];
                        if (playerAnimation.head.zPath != null) bipedHead.offsetZ = (float) playerAnimation.head.zPath.getRelativePosition(millis).values[0];
                        if (playerAnimation.head.xRotPath != null) bipedHead.rotateAngleX = (float) playerAnimation.head.xRotPath.getRelativePosition(millis).values[0];
                        if (playerAnimation.head.yRotPath != null) bipedHead.rotateAngleY = (float) playerAnimation.head.yRotPath.getRelativePosition(millis).values[0];
                        if (playerAnimation.head.zRotPath != null) bipedHead.rotateAngleZ = (float) playerAnimation.head.zRotPath.getRelativePosition(millis).values[0];

                        if (playerAnimation.head.xScalePath != null)
                        {
                            headScale = new float[]{(float) playerAnimation.head.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                        }
                        if (playerAnimation.head.yScalePath != null)
                        {
                            if (headScale == null) headScale = new float[]{1, (float) playerAnimation.head.yScalePath.getRelativePosition(millis).values[0], 1};
                            else headScale[1] = (float) playerAnimation.head.yScalePath.getRelativePosition(millis).values[0];
                        }
                        if (playerAnimation.head.zScalePath != null)
                        {
                            if (headScale == null) headScale = new float[]{1, 1, (float) playerAnimation.head.zScalePath.getRelativePosition(millis).values[0]};
                            else headScale[2] = (float) playerAnimation.head.zScalePath.getRelativePosition(millis).values[0];
                        }
                    }
                    if (headScale != null) GlStateManager.scale(headScale[0], headScale[1], headScale[2]);
                    GL11.glTranslatef(bipedHead.offsetX, bipedHead.offsetY, bipedHead.offsetZ);
                    //FLib compat end

                    renderHead(new SkinPartRenderData(skinPart, renderData));
                }
            }
            else
            {
                if (isChild)
                {
                    GL11.glScalef(0.5f, 0.5f, 0.5f);
                    GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
                }

                if (isSneak) GlStateManager.translate(0.0F, 0.2F, 0.0F);

                switch (skinPart.getPartType().getRegistryName())
                {
                    case "armourers:chest.base":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.chest.xPath != null) bipedBody.offsetX = (float) playerAnimation.chest.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.yPath != null) bipedBody.offsetY = (float) playerAnimation.chest.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.zPath != null) bipedBody.offsetZ = (float) playerAnimation.chest.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.xRotPath != null) bipedBody.rotateAngleX = (float) playerAnimation.chest.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.yRotPath != null) bipedBody.rotateAngleY = (float) playerAnimation.chest.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.zRotPath != null) bipedBody.rotateAngleZ = (float) playerAnimation.chest.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.chest.xScalePath != null)
                            {
                                chestScale = new float[]{(float) playerAnimation.chest.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.chest.yScalePath != null)
                            {
                                if (chestScale == null) chestScale = new float[]{1, (float) playerAnimation.chest.yScalePath.getRelativePosition(millis).values[0], 1};
                                else chestScale[1] = (float) playerAnimation.chest.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.chest.zScalePath != null)
                            {
                                if (chestScale == null) chestScale = new float[]{1, 1, (float) playerAnimation.chest.zScalePath.getRelativePosition(millis).values[0]};
                                else chestScale[2] = (float) playerAnimation.chest.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (chestScale != null) GlStateManager.scale(chestScale[0], chestScale[1], chestScale[2]);
                        GL11.glTranslatef(bipedBody.offsetX, bipedBody.offsetY, bipedBody.offsetZ);

                        GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleZ), 0, 0, 1);
                        GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleY), 0, 1, 0);
                        GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleX), 1, 0, 0);
                        //FLib compat end

                        renderChest(new SkinPartRenderData(skinPart, renderData));
                        break;

                    case "armourers:chest.leftArm":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.leftArm.xPath != null) bipedLeftArm.offsetX = (float) playerAnimation.leftArm.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftArm.yPath != null) bipedLeftArm.offsetY = (float) playerAnimation.leftArm.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftArm.zPath != null) bipedLeftArm.offsetZ = (float) playerAnimation.leftArm.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftArm.xRotPath != null) bipedLeftArm.rotateAngleX = (float) playerAnimation.leftArm.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftArm.yRotPath != null) bipedLeftArm.rotateAngleY = (float) playerAnimation.leftArm.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftArm.zRotPath != null) bipedLeftArm.rotateAngleZ = (float) playerAnimation.leftArm.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.leftArm.xScalePath != null)
                            {
                                leftArmScale = new float[]{(float) playerAnimation.leftArm.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.leftArm.yScalePath != null)
                            {
                                if (leftArmScale == null) leftArmScale = new float[]{1, (float) playerAnimation.leftArm.yScalePath.getRelativePosition(millis).values[0], 1};
                                else leftArmScale[1] = (float) playerAnimation.leftArm.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.leftArm.zScalePath != null)
                            {
                                if (leftArmScale == null) leftArmScale = new float[]{1, 1, (float) playerAnimation.leftArm.zScalePath.getRelativePosition(millis).values[0]};
                                else leftArmScale[2] = (float) playerAnimation.leftArm.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (leftArmScale != null) GlStateManager.scale(leftArmScale[0], leftArmScale[1], leftArmScale[2]);
                        GL11.glTranslatef(bipedLeftArm.offsetX, bipedLeftArm.offsetY, bipedLeftArm.offsetZ);
                        //FLib compat end

                        renderLeftArm(new SkinPartRenderData(skinPart, renderData), overrideChest);
                        break;

                    case "armourers:chest.rightArm":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.rightArm.xPath != null) bipedRightArm.offsetX = (float) playerAnimation.rightArm.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightArm.yPath != null) bipedRightArm.offsetY = (float) playerAnimation.rightArm.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightArm.zPath != null) bipedRightArm.offsetZ = (float) playerAnimation.rightArm.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightArm.xRotPath != null) bipedRightArm.rotateAngleX = (float) playerAnimation.rightArm.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightArm.yRotPath != null) bipedRightArm.rotateAngleY = (float) playerAnimation.rightArm.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightArm.zRotPath != null) bipedRightArm.rotateAngleZ = (float) playerAnimation.rightArm.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.rightArm.xScalePath != null)
                            {
                                rightArmScale = new float[]{(float) playerAnimation.rightArm.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.rightArm.yScalePath != null)
                            {
                                if (rightArmScale == null) rightArmScale = new float[]{1, (float) playerAnimation.rightArm.yScalePath.getRelativePosition(millis).values[0], 1};
                                else rightArmScale[1] = (float) playerAnimation.rightArm.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.rightArm.zScalePath != null)
                            {
                                if (rightArmScale == null) rightArmScale = new float[]{1, 1, (float) playerAnimation.rightArm.zScalePath.getRelativePosition(millis).values[0]};
                                else rightArmScale[2] = (float) playerAnimation.rightArm.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (rightArmScale != null) GlStateManager.scale(rightArmScale[0], rightArmScale[1], rightArmScale[2]);
                        GL11.glTranslatef(bipedRightArm.offsetX, bipedRightArm.offsetY, bipedRightArm.offsetZ);
                        //FLib compat end

                        renderRightArm(new SkinPartRenderData(skinPart, renderData), overrideChest);
                        break;

                    case "armourers:legs.leftLeg":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.leftLeg.xPath != null) bipedLeftLeg.offsetX = (float) playerAnimation.leftLeg.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.yPath != null) bipedLeftLeg.offsetY = (float) playerAnimation.leftLeg.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.zPath != null) bipedLeftLeg.offsetZ = (float) playerAnimation.leftLeg.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.xRotPath != null) bipedLeftLeg.rotateAngleX = (float) playerAnimation.leftLeg.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.yRotPath != null) bipedLeftLeg.rotateAngleY = (float) playerAnimation.leftLeg.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.zRotPath != null) bipedLeftLeg.rotateAngleZ = (float) playerAnimation.leftLeg.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.leftLeg.xScalePath != null)
                            {
                                leftLegScale = new float[]{(float) playerAnimation.leftLeg.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.leftLeg.yScalePath != null)
                            {
                                if (leftLegScale == null) leftLegScale = new float[]{1, (float) playerAnimation.leftLeg.yScalePath.getRelativePosition(millis).values[0], 1};
                                else leftLegScale[1] = (float) playerAnimation.leftLeg.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.leftLeg.zScalePath != null)
                            {
                                if (leftLegScale == null) leftLegScale = new float[]{1, 1, (float) playerAnimation.leftLeg.zScalePath.getRelativePosition(millis).values[0]};
                                else leftLegScale[2] = (float) playerAnimation.leftLeg.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (leftLegScale != null) GlStateManager.scale(leftLegScale[0], leftLegScale[1], leftLegScale[2]);
                        GL11.glTranslatef(bipedLeftLeg.offsetX, bipedLeftLeg.offsetY, bipedLeftLeg.offsetZ);
                        //FLib compat end

                        renderLeftLeg(new SkinPartRenderData(skinPart, renderData));
                        break;

                    case "armourers:legs.rightLeg":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.rightLeg.xPath != null) bipedRightLeg.offsetX = (float) playerAnimation.rightLeg.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.yPath != null) bipedRightLeg.offsetY = (float) playerAnimation.rightLeg.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.zPath != null) bipedRightLeg.offsetZ = (float) playerAnimation.rightLeg.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.xRotPath != null) bipedRightLeg.rotateAngleX = (float) playerAnimation.rightLeg.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.yRotPath != null) bipedRightLeg.rotateAngleY = (float) playerAnimation.rightLeg.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.zRotPath != null) bipedRightLeg.rotateAngleZ = (float) playerAnimation.rightLeg.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.rightLeg.xScalePath != null)
                            {
                                rightLegScale = new float[]{(float) playerAnimation.rightLeg.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.rightLeg.yScalePath != null)
                            {
                                if (rightLegScale == null) rightLegScale = new float[]{1, (float) playerAnimation.rightLeg.yScalePath.getRelativePosition(millis).values[0], 1};
                                else rightLegScale[1] = (float) playerAnimation.rightLeg.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.rightLeg.zScalePath != null)
                            {
                                if (rightLegScale == null) rightLegScale = new float[]{1, 1, (float) playerAnimation.rightLeg.zScalePath.getRelativePosition(millis).values[0]};
                                else rightLegScale[2] = (float) playerAnimation.rightLeg.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (rightLegScale != null) GlStateManager.scale(rightLegScale[0], rightLegScale[1], rightLegScale[2]);
                        GL11.glTranslatef(bipedRightLeg.offsetX, bipedRightLeg.offsetY, bipedRightLeg.offsetZ);
                        //FLib compat end

                        renderRightLeg(new SkinPartRenderData(skinPart, renderData));
                        break;

                    case "armourers:legs.skirt":
                        renderSkirt(new SkinPartRenderData(skinPart, renderData));
                        break;

                    case "armourers:feet.leftFoot":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.leftLeg.xPath != null) bipedLeftLeg.offsetX = (float) playerAnimation.leftLeg.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.yPath != null) bipedLeftLeg.offsetY = (float) playerAnimation.leftLeg.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.zPath != null) bipedLeftLeg.offsetZ = (float) playerAnimation.leftLeg.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.xRotPath != null) bipedLeftLeg.rotateAngleX = (float) playerAnimation.leftLeg.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.yRotPath != null) bipedLeftLeg.rotateAngleY = (float) playerAnimation.leftLeg.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.leftLeg.zRotPath != null) bipedLeftLeg.rotateAngleZ = (float) playerAnimation.leftLeg.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.leftLeg.xScalePath != null)
                            {
                                leftLegScale = new float[]{(float) playerAnimation.leftLeg.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.leftLeg.yScalePath != null)
                            {
                                if (leftLegScale == null) leftLegScale = new float[]{1, (float) playerAnimation.leftLeg.yScalePath.getRelativePosition(millis).values[0], 1};
                                else leftLegScale[1] = (float) playerAnimation.leftLeg.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.leftLeg.zScalePath != null)
                            {
                                if (leftLegScale == null) leftLegScale = new float[]{1, 1, (float) playerAnimation.leftLeg.zScalePath.getRelativePosition(millis).values[0]};
                                else leftLegScale[2] = (float) playerAnimation.leftLeg.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (leftLegScale != null) GlStateManager.scale(leftLegScale[0], leftLegScale[1], leftLegScale[2]);
                        GL11.glTranslatef(bipedLeftLeg.offsetX, bipedLeftLeg.offsetY, bipedLeftLeg.offsetZ);
                        //FLib compat end

                        renderLeftFoot(new SkinPartRenderData(skinPart, renderData));
                        break;

                    case "armourers:feet.rightFoot":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.rightLeg.xPath != null) bipedRightLeg.offsetX = (float) playerAnimation.rightLeg.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.yPath != null) bipedRightLeg.offsetY = (float) playerAnimation.rightLeg.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.zPath != null) bipedRightLeg.offsetZ = (float) playerAnimation.rightLeg.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.xRotPath != null) bipedRightLeg.rotateAngleX = (float) playerAnimation.rightLeg.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.yRotPath != null) bipedRightLeg.rotateAngleY = (float) playerAnimation.rightLeg.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.rightLeg.zRotPath != null) bipedRightLeg.rotateAngleZ = (float) playerAnimation.rightLeg.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.rightLeg.xScalePath != null)
                            {
                                rightLegScale = new float[]{(float) playerAnimation.rightLeg.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.rightLeg.yScalePath != null)
                            {
                                if (rightLegScale == null) rightLegScale = new float[]{1, (float) playerAnimation.rightLeg.yScalePath.getRelativePosition(millis).values[0], 1};
                                else rightLegScale[1] = (float) playerAnimation.rightLeg.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.rightLeg.zScalePath != null)
                            {
                                if (rightLegScale == null) rightLegScale = new float[]{1, 1, (float) playerAnimation.rightLeg.zScalePath.getRelativePosition(millis).values[0]};
                                else rightLegScale[2] = (float) playerAnimation.rightLeg.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (rightLegScale != null) GlStateManager.scale(rightLegScale[0], rightLegScale[1], rightLegScale[2]);
                        GL11.glTranslatef(bipedRightLeg.offsetX, bipedRightLeg.offsetY, bipedRightLeg.offsetZ);
                        //FLib compat end

                        renderRightFoot(new SkinPartRenderData(skinPart, renderData));
                        break;

                    case "armourers:wings.leftWing":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.chest.xPath != null) bipedBody.offsetX = (float) playerAnimation.chest.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.yPath != null) bipedBody.offsetY = (float) playerAnimation.chest.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.zPath != null) bipedBody.offsetZ = (float) playerAnimation.chest.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.xRotPath != null) bipedBody.rotateAngleX = (float) playerAnimation.chest.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.yRotPath != null) bipedBody.rotateAngleY = (float) playerAnimation.chest.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.zRotPath != null) bipedBody.rotateAngleZ = (float) playerAnimation.chest.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.chest.xScalePath != null)
                            {
                                chestScale = new float[]{(float) playerAnimation.chest.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.chest.yScalePath != null)
                            {
                                if (chestScale == null) chestScale = new float[]{1, (float) playerAnimation.chest.yScalePath.getRelativePosition(millis).values[0], 1};
                                else chestScale[1] = (float) playerAnimation.chest.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.chest.zScalePath != null)
                            {
                                if (chestScale == null) chestScale = new float[]{1, 1, (float) playerAnimation.chest.zScalePath.getRelativePosition(millis).values[0]};
                                else chestScale[2] = (float) playerAnimation.chest.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (chestScale != null) GlStateManager.scale(chestScale[0], chestScale[1], chestScale[2]);
                        GL11.glTranslatef(bipedBody.offsetX, bipedBody.offsetY, bipedBody.offsetZ);
                        //FLib compat end

                        angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
                        renderLeftWing(new SkinPartRenderData(skinPart, renderData), angle);
                        break;

                    case "armourers:wings.rightWing":
                        //FLib compat start
                        if (playerAnimation != null)
                        {
                            if (playerAnimation.chest.xPath != null) bipedBody.offsetX = (float) playerAnimation.chest.xPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.yPath != null) bipedBody.offsetY = (float) playerAnimation.chest.yPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.zPath != null) bipedBody.offsetZ = (float) playerAnimation.chest.zPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.xRotPath != null) bipedBody.rotateAngleX = (float) playerAnimation.chest.xRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.yRotPath != null) bipedBody.rotateAngleY = (float) playerAnimation.chest.yRotPath.getRelativePosition(millis).values[0];
                            if (playerAnimation.chest.zRotPath != null) bipedBody.rotateAngleZ = (float) playerAnimation.chest.zRotPath.getRelativePosition(millis).values[0];

                            if (playerAnimation.chest.xScalePath != null)
                            {
                                chestScale = new float[]{(float) playerAnimation.chest.xScalePath.getRelativePosition(millis).values[0], 1, 1};
                            }
                            if (playerAnimation.chest.yScalePath != null)
                            {
                                if (chestScale == null) chestScale = new float[]{1, (float) playerAnimation.chest.yScalePath.getRelativePosition(millis).values[0], 1};
                                else chestScale[1] = (float) playerAnimation.chest.yScalePath.getRelativePosition(millis).values[0];
                            }
                            if (playerAnimation.chest.zScalePath != null)
                            {
                                if (chestScale == null) chestScale = new float[]{1, 1, (float) playerAnimation.chest.zScalePath.getRelativePosition(millis).values[0]};
                                else chestScale[2] = (float) playerAnimation.chest.zScalePath.getRelativePosition(millis).values[0];
                            }
                        }
                        if (chestScale != null) GlStateManager.scale(chestScale[0], chestScale[1], chestScale[2]);
                        GL11.glTranslatef(bipedBody.offsetX, bipedBody.offsetY, bipedBody.offsetZ);
                        //FLib compat end

                        angle = SkinUtils.getFlapAngleForWings(entity, skin, i);
                        renderRightWing(new SkinPartRenderData(skinPart, renderData), -angle);
                        break;
                }
            }


            GL11.glPopMatrix();

            i++;
        }
        GlStateManager.popAttrib();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void renderHead(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleZ), 0, 0, 1);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleY), 0, 1, 0);
        GL11.glRotated(Math.toDegrees(bipedHead.rotateAngleX), 1, 0, 0);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderChest(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftArm(SkinPartRenderData partRenderData, boolean override)
    {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);

        GL11.glTranslatef(5.0F * partRenderData.getScale(), 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * partRenderData.getScale(), 0F);
        if (slim & !override)
        {
            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
        }

        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftArm.rotateAngleX), 1, 0, 0);

        if (slim & !override)
        {
            GL11.glTranslatef(-0.25F * partRenderData.getScale(), 0F, 0F);
            GL11.glScalef(0.75F, 1F, 1F);
        }
        renderPart(partRenderData);

        GL11.glPopMatrix();
    }

    private void renderRightArm(SkinPartRenderData partRenderData, boolean override)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);

        GL11.glTranslatef(-5.0F * partRenderData.getScale(), 0F, 0F);
        GL11.glTranslatef(0, 2.0F * partRenderData.getScale(), 0F);
        if (slim & !override)
        {
            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
        }

        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightArm.rotateAngleX), 1, 0, 0);

        if (slim & !override)
        {
            GL11.glTranslatef(0.25F * partRenderData.getScale(), 0F, 0F);
            GL11.glScalef(0.75F, 1F, 1F);
        }

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftLeg(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        GL11.glTranslated(2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightLeg(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        GL11.glTranslated(-2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);
        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftFoot(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        GL11.glTranslated(2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedLeftLeg.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightFoot(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        GL11.glTranslated(-2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedRightLeg.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderSkirt(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        if (isRiding)
        {
            GL11.glRotated(-70, 1F, 0F, 0F);
        }

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftWing(SkinPartRenderData partRenderData, double angle)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, partRenderData.getScale() * 2);

        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (partRenderData.getSkinPart().getMarkerCount() > 0)
        {
            point = partRenderData.getSkinPart().getMarker(0);
            axis = partRenderData.getSkinPart().getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F);
        GL11.glTranslated(partRenderData.getScale() * point.getX(), partRenderData.getScale() * point.getY(), partRenderData.getScale() * point.getZ());

        switch (axis)
        {
            case UP:
                GL11.glRotated(angle, 0, 1, 0);
                break;
            case DOWN:
                GL11.glRotated(angle, 0, -1, 0);
                break;
            case SOUTH:
                GL11.glRotated(angle, 0, 0, -1);
                break;
            case NORTH:
                GL11.glRotated(angle, 0, 0, 1);
                break;
            case EAST:
                GL11.glRotated(angle, 1, 0, 0);
                break;
            case WEST:
                GL11.glRotated(angle, -1, 0, 0);
                break;
        }

        GL11.glTranslated(partRenderData.getScale() * -point.getX(), partRenderData.getScale() * -point.getY(), partRenderData.getScale() * -point.getZ());
        GL11.glTranslated(partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightWing(SkinPartRenderData partRenderData, double angle)
    {
        GL11.glPushMatrix();
        GL11.glTranslated(0, 0, partRenderData.getScale() * 2);
        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (partRenderData.getSkinPart().getMarkerCount() > 0)
        {
            point = partRenderData.getSkinPart().getMarker(0);
            axis = partRenderData.getSkinPart().getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(this.bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F, partRenderData.getScale() * 0.5F);
        GL11.glTranslated(partRenderData.getScale() * point.getX(), partRenderData.getScale() * point.getY(), partRenderData.getScale() * point.getZ());

        switch (axis)
        {
            case UP:
                GL11.glRotated(angle, 0, 1, 0);
                break;
            case DOWN:
                GL11.glRotated(angle, 0, -1, 0);
                break;
            case SOUTH:
                GL11.glRotated(angle, 0, 0, -1);
                break;
            case NORTH:
                GL11.glRotated(angle, 0, 0, 1);
                break;
            case EAST:
                GL11.glRotated(angle, 1, 0, 0);
                break;
            case WEST:
                GL11.glRotated(angle, -1, 0, 0);
                break;
        }

        GL11.glTranslated(partRenderData.getScale() * -point.getX(), partRenderData.getScale() * -point.getY(), partRenderData.getScale() * -point.getZ());
        GL11.glTranslated(partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F, partRenderData.getScale() * -0.5F);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }
}
