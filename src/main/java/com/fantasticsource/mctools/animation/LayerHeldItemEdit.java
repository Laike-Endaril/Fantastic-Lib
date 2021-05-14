package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.path.CPath;
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

            CPath.CPathData handItemSwap = CBipedAnimation.getCurrent(entitylivingbaseIn).handItemSwap;
            long millis = System.currentTimeMillis();
            if (handItemSwap.path != null && handItemSwap.getRelativePosition(millis).values[0] < 0)
            {
                renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, millis);
                renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, millis);
            }
            else
            {
                renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, millis);
                renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, millis);
            }


            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(EntityLivingBase entity, ItemStack stack, ItemCameraTransforms.TransformType transformType, EnumHandSide handSide, long millis)
    {
        if (stack.isEmpty()) return;


        GlStateManager.pushMatrix();

        if (entity.isSneaking()) GlStateManager.translate(0.0F, 0.2F, 0.0F);
        translateToHand(handSide);
        GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        boolean flag = handSide == EnumHandSide.LEFT;
        GlStateManager.translate((float) (flag ? -1 : 1) / 16.0F, 0.125F, -0.625F);


        CBipedAnimation animation = CBipedAnimation.getCurrent(entity);
        if (handSide == EnumHandSide.LEFT)
        {
            if (animation.leftItem.xScalePath.path != null)
            {
                leftItemScale = new float[]{(float) animation.leftItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
            }
            if (animation.leftItem.yScalePath.path != null)
            {
                if (leftItemScale == null) leftItemScale = new float[]{1, (float) animation.leftItem.yScalePath.getRelativePosition(millis).values[0], 1};
                else leftItemScale[1] = (float) animation.leftItem.yScalePath.getRelativePosition(millis).values[0];
            }
            if (animation.leftItem.zScalePath.path != null)
            {
                if (leftItemScale == null) leftItemScale = new float[]{1, 1, (float) animation.leftItem.zScalePath.getRelativePosition(millis).values[0]};
                else leftItemScale[2] = (float) animation.leftItem.zScalePath.getRelativePosition(millis).values[0];
            }
            if (leftItemScale != null) GlStateManager.scale(leftItemScale[0], leftItemScale[1], leftItemScale[2]);

            GL11.glTranslatef(animation.leftItem.xPath.path == null ? 0 : (float) animation.leftItem.xPath.getRelativePosition(millis).values[0], animation.leftItem.yPath.path == null ? 0 : (float) animation.leftItem.yPath.getRelativePosition(millis).values[0], animation.leftItem.zPath.path == null ? 0 : (float) animation.leftItem.zPath.getRelativePosition(millis).values[0]);

            if (animation.leftItem.zRotPath.path != null) GL11.glRotated(Math.toDegrees(Tools.posMod(animation.leftItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
            if (animation.leftItem.yRotPath.path != null) GL11.glRotated(Math.toDegrees(Tools.posMod(animation.leftItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
            if (animation.leftItem.xRotPath.path != null) GL11.glRotated(Math.toDegrees(Tools.posMod(animation.leftItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
        }
        else
        {
            if (animation.rightItem.xScalePath.path != null)
            {
                rightItemScale = new float[]{(float) animation.rightItem.xScalePath.getRelativePosition(millis).values[0], 1, 1};
            }
            if (animation.rightItem.yScalePath.path != null)
            {
                if (rightItemScale == null) rightItemScale = new float[]{1, (float) animation.rightItem.yScalePath.getRelativePosition(millis).values[0], 1};
                else rightItemScale[1] = (float) animation.rightItem.yScalePath.getRelativePosition(millis).values[0];
            }
            if (animation.rightItem.zScalePath.path != null)
            {
                if (rightItemScale == null) rightItemScale = new float[]{1, 1, (float) animation.rightItem.zScalePath.getRelativePosition(millis).values[0]};
                else rightItemScale[2] = (float) animation.rightItem.zScalePath.getRelativePosition(millis).values[0];
            }
            if (rightItemScale != null) GlStateManager.scale(rightItemScale[0], rightItemScale[1], rightItemScale[2]);

            GL11.glTranslatef(animation.rightItem.xPath.path == null ? 0 : (float) animation.rightItem.xPath.getRelativePosition(millis).values[0], animation.rightItem.yPath.path == null ? 0 : (float) animation.rightItem.yPath.getRelativePosition(millis).values[0], animation.rightItem.zPath.path == null ? 0 : (float) animation.rightItem.zPath.getRelativePosition(millis).values[0]);

            if (animation.rightItem.zRotPath.path != null) GL11.glRotated(Math.toDegrees(Tools.posMod(animation.rightItem.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 0, 1);
            if (animation.rightItem.yRotPath.path != null) GL11.glRotated(Math.toDegrees(Tools.posMod(animation.rightItem.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 0, 1, 0);
            if (animation.rightItem.xRotPath.path != null) GL11.glRotated(Math.toDegrees(Tools.posMod(animation.rightItem.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2)), 1, 0, 0);
        }


        Minecraft.getMinecraft().getItemRenderer().renderItemSide(entity, stack, transformType, flag);
        GlStateManager.popMatrix();
    }
}
