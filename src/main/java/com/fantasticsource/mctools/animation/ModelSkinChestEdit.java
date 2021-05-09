package com.fantasticsource.mctools.animation;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinChest;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.client.skin.SkinModelTexture;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinPaintCache;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.common.skin.data.SkinProperties;
import moe.plushie.armourers_workshop.proxies.ClientProxy;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ModelSkinChestEdit extends ModelSkinChest
{
    public float[] chestScale = null, leftArmScale = null, rightArmScale = null;

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
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            bipedBody.render(SCALE);
            bipedLeftArm.render(SCALE);
            bipedRightArm.render(SCALE);
            GL11.glPopAttrib();
        }

        boolean override = SkinProperties.PROP_MODEL_OVERRIDE_CHEST.getValue(skin.getProperties());

        for (SkinPart part : parts)
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
            }


            //FLib compat start
            CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entity);
            long millis = System.currentTimeMillis();
            //FLib compat end


            if (part.getPartType().getPartName().equals("base"))
            {
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

                renderChest(new SkinPartRenderData(part, renderData));
            }
            else if (part.getPartType().getPartName().equals("leftArm"))
            {
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

                renderLeftArm(new SkinPartRenderData(part, renderData), override);
            }
            else if (part.getPartType().getPartName().equals("rightArm"))
            {
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

                renderRightArm(new SkinPartRenderData(part, renderData), override);
            }

            GL11.glPopMatrix();
        }
        GlStateManager.popAttrib();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void renderChest(SkinPartRenderData skinPartRenderData)
    {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleY), 0, 1, 0);
        if (isSneak)
        {
            GL11.glRotated(Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
        }
        GL11.glColor3f(1F, 1F, 1F);
        renderPart(skinPartRenderData);
        GL11.glPopMatrix();
    }

    private void renderLeftArm(SkinPartRenderData partRenderData, boolean override)
    {
        GL11.glPushMatrix();
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleY), 0, 1, 0);

        GL11.glTranslatef(5.0F * partRenderData.getScale(), 0F, 0F);
        GL11.glTranslatef(0F, 2.0F * partRenderData.getScale(), 0F);
        if (slim & !override)
        {
            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
        }

        GL11.glRotatef((float) Math.toDegrees(bipedLeftArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(bipedLeftArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedLeftArm.rotateAngleX), 1, 0, 0);

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
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleY), 0, 1, 0);

        GL11.glTranslatef(-5.0F * partRenderData.getScale(), 0F, 0F);
        GL11.glTranslatef(0, 2.0F * partRenderData.getScale(), 0F);
        if (slim & !override)
        {
            GlStateManager.translate(0, partRenderData.getScale() * 0.5F, 0);
        }

        GL11.glRotatef((float) Math.toDegrees(bipedRightArm.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(bipedRightArm.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedRightArm.rotateAngleX), 1, 0, 0);

        if (slim & !override)
        {
            GL11.glTranslatef(0.25F * partRenderData.getScale(), 0F, 0F);
            GL11.glScalef(0.75F, 1F, 1F);
        }

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }
}
