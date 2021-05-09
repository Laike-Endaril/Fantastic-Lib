package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.opengl.GL11;

public class LayerHeldItemEdit extends LayerHeldItem
{
    public float[] leftItemScale = null, rightItemScale = null;

    public LayerHeldItemEdit(RenderLivingBase<?> livingEntityRendererIn)
    {
        super(livingEntityRendererIn);
    }

    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();

        if (!itemstack.isEmpty() || !itemstack1.isEmpty())
        {
            GlStateManager.pushMatrix();

            if (this.livingEntityRenderer.getMainModel().isChild)
            {
                GlStateManager.translate(0.0F, 0.75F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide)
    {
        if (!stack.isEmpty())
        {
            GlStateManager.pushMatrix();

            if (entity.isSneaking()) GlStateManager.translate(0.0F, 0.2F, 0.0F);
            translateToHand(handSide);
            GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            boolean flag = handSide == EnumHandSide.LEFT;
            GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);


            CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entity);
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

                    if (playerAnimation.leftItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                    if (playerAnimation.leftItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                    if (playerAnimation.leftItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.leftItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
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

                    if (playerAnimation.rightItem.zRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
                    if (playerAnimation.rightItem.yRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
                    if (playerAnimation.rightItem.xRotPath != null) GL11.glRotated(Math.toDegrees(Tools.posMod(playerAnimation.rightItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
                }
            }


            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transformType, flag);
            GlStateManager.popMatrix();
        }
    }
}
