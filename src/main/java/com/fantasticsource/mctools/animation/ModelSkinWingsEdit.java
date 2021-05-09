package com.fantasticsource.mctools.animation;

import moe.plushie.armourers_workshop.api.common.IExtraColours;
import moe.plushie.armourers_workshop.api.common.skin.Point3D;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDye;
import moe.plushie.armourers_workshop.client.model.skin.ModelSkinWings;
import moe.plushie.armourers_workshop.client.render.SkinPartRenderData;
import moe.plushie.armourers_workshop.client.render.SkinRenderData;
import moe.plushie.armourers_workshop.common.skin.data.Skin;
import moe.plushie.armourers_workshop.common.skin.data.SkinPart;
import moe.plushie.armourers_workshop.utils.SkinUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import org.lwjgl.opengl.GL11;

public class ModelSkinWingsEdit extends ModelSkinWings
{
    public float[] chestScale = null;

    @Override
    public void render(Entity entity, Skin skin, boolean showSkinPaint, ISkinDye skinDye, IExtraColours extraColours, boolean itemRender, double distance, boolean doLodLoading)
    {
        render(entity, skin, new SkinRenderData(SCALE, skinDye, extraColours, distance, doLodLoading, showSkinPaint, itemRender, null));
    }

    @Override
    public void render(Entity entity, Skin skin, SkinRenderData renderData)
    {
        if (skin == null) return;

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            isSneak = player.isSneaking();
            isRiding = player.isRiding();
        }

        GlStateManager.pushAttrib();
        RenderHelper.enableGUIStandardItemLighting();

        int i = 0;
        for (SkinPart skinPart : skin.getParts())
        {
            GL11.glPushMatrix();
            if (isSneak)
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
                GlStateManager.rotate((float) Math.toDegrees(bipedBody.rotateAngleX), 1F, 0, 0);
            }
            GL11.glTranslated(0, 0, SCALE * 2);
            if (isChild)
            {
                GL11.glScalef(0.5f, 0.5f, 0.5f);
                GL11.glTranslatef(0.0F, 24.0F * SCALE, 0.0F);
            }

            double angle = SkinUtils.getFlapAngleForWings(entity, skin, i);


            //FLib compat start
            CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entity);
            if (playerAnimation != null)
            {
                long millis = System.currentTimeMillis();

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


            if (skinPart.getPartType().getPartName().equals("leftWing"))
            {
                renderLeftWing(new SkinPartRenderData(skinPart, renderData), angle);
            }
            else
            {
                renderRightWing(new SkinPartRenderData(skinPart, renderData), -angle);
            }
            GL11.glPopMatrix();

            i++;
        }
        GlStateManager.popAttrib();
        GlStateManager.color(1F, 1F, 1F, 1F);
    }

    private void renderLeftWing(SkinPartRenderData partRenderData, double angle)
    {
        GL11.glPushMatrix();

        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (partRenderData.getSkinPart().getMarkerCount() > 0)
        {
            point = partRenderData.getSkinPart().getMarker(0);
            axis = partRenderData.getSkinPart().getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleX), 1, 0, 0);


        GL11.glTranslated(SCALE * 0.5F, SCALE * 0.5F, SCALE * 0.5F);
        GL11.glTranslated(SCALE * point.getX(), SCALE * point.getY(), SCALE * point.getZ());

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

        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }

    private void renderRightWing(SkinPartRenderData partRenderData, double angle)
    {
        GL11.glPushMatrix();
        Point3D point = new Point3D(0, 0, 0);
        EnumFacing axis = EnumFacing.DOWN;

        if (partRenderData.getSkinPart().getMarkerCount() > 0)
        {
            point = partRenderData.getSkinPart().getMarker(0);
            axis = partRenderData.getSkinPart().getMarkerSide(0);
        }
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleZ), 0, 0, 1);
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleY), 0, 1, 0);
        GL11.glRotatef((float) Math.toDegrees(bipedBody.rotateAngleX), 1, 0, 0);

        GL11.glTranslated(SCALE * 0.5F, SCALE * 0.5F, SCALE * 0.5F);
        GL11.glTranslated(SCALE * point.getX(), SCALE * point.getY(), SCALE * point.getZ());

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

        GL11.glTranslated(SCALE * -point.getX(), SCALE * -point.getY(), SCALE * -point.getZ());
        GL11.glTranslated(SCALE * -0.5F, SCALE * -0.5F, SCALE * -0.5F);

        renderPart(partRenderData);
        GL11.glPopMatrix();
    }
}
