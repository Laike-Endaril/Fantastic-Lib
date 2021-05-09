package com.fantasticsource.mctools.animation;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinFeet;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ModelSkinFeetEdit extends ModelSkinFeet
{
    public float[] leftLegScale = null, rightLegScale = null;

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading)
    {
        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
    }

    @Override
    public void render(Entity entity, Skin skin, SkinRenderData renderData)
    {
        if (skin == null) return;
        ArrayList<SkinPart> parts = skin.getParts();

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            isSneak = player.isSneaking();
            isRiding = player.isRiding();
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

        for (SkinPart skinPart : parts)
        {
            GL11.glPushMatrix();
            if (isChild)
            {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }
            if (isSneak)
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
                GL11.glTranslated(0, -3 * SCALE, 4 * SCALE);
            }


            //FLib compat start
            CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entity);
            //FLib compat end


            if (skinPart.getPartType().getPartName().equals("leftFoot"))
            {
                //FLib compat start
                if (playerAnimation != null)
                {
                    long millis = System.currentTimeMillis();

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
            }
            else if (skinPart.getPartType().getPartName().equals("rightFoot"))
            {
                //FLib compat start
                if (playerAnimation != null)
                {
                    long millis = System.currentTimeMillis();

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
            }

            GL11.glPopMatrix();
        }
        GlStateManager.popAttrib();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void renderLeftFoot(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        GL11.glTranslated(2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedLeftLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(bipedLeftLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedLeftLeg.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightFoot(SkinPartRenderData partRenderData)
    {
        GL11.glPushMatrix();
        GL11.glColor3f(1F, 1F, 1F);
        GL11.glTranslated(0, 12 * partRenderData.getScale(), 0);
        GL11.glTranslated(-2 * partRenderData.getScale(), 0, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedRightLeg.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(bipedRightLeg.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedRightLeg.rotateAngleX), 1, 0, 0);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }
}
