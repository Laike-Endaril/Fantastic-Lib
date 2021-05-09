package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;

public class ModelBipedEdit extends ModelBiped
{
    public float[] headScale = null, chestScale = null, leftArmScale = null, rightArmScale = null, leftLegScale = null, rightLegScale = null;

    public ModelBipedEdit(float modelSize)
    {
        super(modelSize);
    }

    public ModelBipedEdit(ModelBiped oldModel)
    {
        leftArmPose = oldModel.leftArmPose;
        rightArmPose = oldModel.rightArmPose;
        textureWidth = oldModel.textureWidth;
        textureHeight = oldModel.textureHeight;

        bipedHead = oldModel.bipedHead;
        bipedBody = oldModel.bipedBody;
        bipedLeftArm = oldModel.bipedLeftArm;
        bipedRightArm = oldModel.bipedRightArm;
        bipedLeftLeg = oldModel.bipedLeftLeg;
        bipedRightLeg = oldModel.bipedRightLeg;

        bipedHeadwear = oldModel.bipedHeadwear;
    }


    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scaleFactor, Entity entityIn)
    {
        //From ModelBiped
        boolean flag = entityIn instanceof EntityLivingBase && ((EntityLivingBase) entityIn).getTicksElytraFlying() > 4;
        bipedHead.rotateAngleY = netHeadYaw * 0.017453292F;

        if (flag)
        {
            bipedHead.rotateAngleX = -((float) Math.PI / 4F);
        }
        else
        {
            bipedHead.rotateAngleX = headPitch * 0.017453292F;
        }

        bipedBody.rotateAngleY = 0.0F;
        bipedRightArm.rotationPointZ = 0.0F;
        bipedRightArm.rotationPointX = -5.0F;
        bipedLeftArm.rotationPointZ = 0.0F;
        bipedLeftArm.rotationPointX = 5.0F;
        float f = 1.0F;

        if (flag)
        {
            f = (float) (entityIn.motionX * entityIn.motionX + entityIn.motionY * entityIn.motionY + entityIn.motionZ * entityIn.motionZ);
            f = f / 0.2F;
            f = f * f * f;
        }

        if (f < 1.0F)
        {
            f = 1.0F;
        }

        bipedRightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 2.0F * limbSwingAmount * 0.5F / f;
        bipedLeftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F / f;
        bipedRightArm.rotateAngleZ = 0.0F;
        bipedLeftArm.rotateAngleZ = 0.0F;
        bipedRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount / f;
        bipedLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float) Math.PI) * 1.4F * limbSwingAmount / f;
        bipedRightLeg.rotateAngleY = 0.0F;
        bipedLeftLeg.rotateAngleY = 0.0F;
        bipedRightLeg.rotateAngleZ = 0.0F;
        bipedLeftLeg.rotateAngleZ = 0.0F;

        if (isRiding)
        {
            bipedRightArm.rotateAngleX += -((float) Math.PI / 5F);
            bipedLeftArm.rotateAngleX += -((float) Math.PI / 5F);
            bipedRightLeg.rotateAngleX = -1.4137167F;
            bipedRightLeg.rotateAngleY = ((float) Math.PI / 10F);
            bipedRightLeg.rotateAngleZ = 0.07853982F;
            bipedLeftLeg.rotateAngleX = -1.4137167F;
            bipedLeftLeg.rotateAngleY = -((float) Math.PI / 10F);
            bipedLeftLeg.rotateAngleZ = -0.07853982F;
        }

        bipedRightArm.rotateAngleY = 0.0F;
        bipedRightArm.rotateAngleZ = 0.0F;

        switch (leftArmPose)
        {
            case EMPTY:
                bipedLeftArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - 0.9424779F;
                bipedLeftArm.rotateAngleY = 0.5235988F;
                break;
            case ITEM:
                bipedLeftArm.rotateAngleX = bipedLeftArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                bipedLeftArm.rotateAngleY = 0.0F;
        }

        switch (rightArmPose)
        {
            case EMPTY:
                bipedRightArm.rotateAngleY = 0.0F;
                break;
            case BLOCK:
                bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - 0.9424779F;
                bipedRightArm.rotateAngleY = -0.5235988F;
                break;
            case ITEM:
                bipedRightArm.rotateAngleX = bipedRightArm.rotateAngleX * 0.5F - ((float) Math.PI / 10F);
                bipedRightArm.rotateAngleY = 0.0F;
        }

        if (swingProgress > 0.0F)
        {
            EnumHandSide enumhandside = getMainHand(entityIn);
            ModelRenderer modelrenderer = getArmForSide(enumhandside);
            float f1 = swingProgress;
            bipedBody.rotateAngleY = MathHelper.sin(MathHelper.sqrt(f1) * ((float) Math.PI * 2F)) * 0.2F;

            if (enumhandside == EnumHandSide.LEFT)
            {
                bipedBody.rotateAngleY *= -1.0F;
            }

            bipedRightArm.rotationPointZ = MathHelper.sin(bipedBody.rotateAngleY) * 5.0F;
            bipedRightArm.rotationPointX = -MathHelper.cos(bipedBody.rotateAngleY) * 5.0F;
            bipedLeftArm.rotationPointZ = -MathHelper.sin(bipedBody.rotateAngleY) * 5.0F;
            bipedLeftArm.rotationPointX = MathHelper.cos(bipedBody.rotateAngleY) * 5.0F;
            bipedRightArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleY += bipedBody.rotateAngleY;
            bipedLeftArm.rotateAngleX += bipedBody.rotateAngleY;
            f1 = 1.0F - swingProgress;
            f1 = f1 * f1;
            f1 = f1 * f1;
            f1 = 1.0F - f1;
            float f2 = MathHelper.sin(f1 * (float) Math.PI);
            float f3 = MathHelper.sin(swingProgress * (float) Math.PI) * -(bipedHead.rotateAngleX - 0.7F) * 0.75F;
            modelrenderer.rotateAngleX = (float) ((double) modelrenderer.rotateAngleX - ((double) f2 * 1.2D + (double) f3));
            modelrenderer.rotateAngleY += bipedBody.rotateAngleY * 2.0F;
            modelrenderer.rotateAngleZ += MathHelper.sin(swingProgress * (float) Math.PI) * -0.4F;
        }

        if (isSneak)
        {
            bipedBody.rotateAngleX = 0.5F;
            bipedRightArm.rotateAngleX += 0.4F;
            bipedLeftArm.rotateAngleX += 0.4F;
            bipedRightLeg.rotationPointZ = 4.0F;
            bipedLeftLeg.rotationPointZ = 4.0F;
            bipedRightLeg.rotationPointY = 9.0F;
            bipedLeftLeg.rotationPointY = 9.0F;
            bipedHead.rotationPointY = 1.0F;
        }
        else
        {
            bipedBody.rotateAngleX = 0.0F;
            bipedRightLeg.rotationPointZ = 0.1F;
            bipedLeftLeg.rotationPointZ = 0.1F;
            bipedRightLeg.rotationPointY = 12.0F;
            bipedLeftLeg.rotationPointY = 12.0F;
            bipedHead.rotationPointY = 0.0F;
        }

        bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
        bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
        bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;

        if (rightArmPose == ArmPose.BOW_AND_ARROW)
        {
            bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY;
            bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
        }
        else if (leftArmPose == ArmPose.BOW_AND_ARROW)
        {
            bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - 0.4F;
            bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
        }


        //Custom via paths
        CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityIn);
        if (playerAnimation != null)
        {
            long millis = System.currentTimeMillis();

            if (playerAnimation.head.xPath != null) bipedHead.offsetX = (float) playerAnimation.head.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.head.yPath != null) bipedHead.offsetY = (float) playerAnimation.head.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.head.zPath != null) bipedHead.offsetZ = (float) playerAnimation.head.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.head.xRotPath != null) bipedHead.rotateAngleX = (float) Tools.posMod(playerAnimation.head.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.head.yRotPath != null) bipedHead.rotateAngleY = (float) Tools.posMod(playerAnimation.head.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.head.zRotPath != null) bipedHead.rotateAngleZ = (float) Tools.posMod(playerAnimation.head.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);

            if (playerAnimation.chest.xPath != null) bipedBody.offsetX = (float) playerAnimation.chest.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.chest.yPath != null) bipedBody.offsetY = (float) playerAnimation.chest.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.chest.zPath != null) bipedBody.offsetZ = (float) playerAnimation.chest.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.chest.xRotPath != null) bipedBody.rotateAngleX = (float) Tools.posMod(playerAnimation.chest.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.chest.yRotPath != null) bipedBody.rotateAngleY = (float) Tools.posMod(playerAnimation.chest.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.chest.zRotPath != null) bipedBody.rotateAngleZ = (float) Tools.posMod(playerAnimation.chest.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);

            if (playerAnimation.leftArm.xPath != null) bipedLeftArm.offsetX = (float) playerAnimation.leftArm.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.leftArm.yPath != null) bipedLeftArm.offsetY = (float) playerAnimation.leftArm.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.leftArm.zPath != null) bipedLeftArm.offsetZ = (float) playerAnimation.leftArm.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.leftArm.xRotPath != null) bipedLeftArm.rotateAngleX = (float) Tools.posMod(playerAnimation.leftArm.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.leftArm.yRotPath != null) bipedLeftArm.rotateAngleY = (float) Tools.posMod(playerAnimation.leftArm.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.leftArm.zRotPath != null) bipedLeftArm.rotateAngleZ = (float) Tools.posMod(playerAnimation.leftArm.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);

            if (playerAnimation.rightArm.xPath != null) bipedRightArm.offsetX = (float) playerAnimation.rightArm.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.rightArm.yPath != null) bipedRightArm.offsetY = (float) playerAnimation.rightArm.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.rightArm.zPath != null) bipedRightArm.offsetZ = (float) playerAnimation.rightArm.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.rightArm.xRotPath != null) bipedRightArm.rotateAngleX = (float) Tools.posMod(playerAnimation.rightArm.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.rightArm.yRotPath != null) bipedRightArm.rotateAngleY = (float) Tools.posMod(playerAnimation.rightArm.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.rightArm.zRotPath != null) bipedRightArm.rotateAngleZ = (float) Tools.posMod(playerAnimation.rightArm.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);

            if (playerAnimation.leftLeg.xPath != null) bipedLeftLeg.offsetX = (float) playerAnimation.leftLeg.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.leftLeg.yPath != null) bipedLeftLeg.offsetY = (float) playerAnimation.leftLeg.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.leftLeg.zPath != null) bipedLeftLeg.offsetZ = (float) playerAnimation.leftLeg.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.leftLeg.xRotPath != null) bipedLeftLeg.rotateAngleX = (float) Tools.posMod(playerAnimation.leftLeg.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.leftLeg.yRotPath != null) bipedLeftLeg.rotateAngleY = (float) Tools.posMod(playerAnimation.leftLeg.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.leftLeg.zRotPath != null) bipedLeftLeg.rotateAngleZ = (float) Tools.posMod(playerAnimation.leftLeg.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);

            if (playerAnimation.rightLeg.xPath != null) bipedRightLeg.offsetX = (float) playerAnimation.rightLeg.xPath.getRelativePosition(millis).values[0];
            if (playerAnimation.rightLeg.yPath != null) bipedRightLeg.offsetY = (float) playerAnimation.rightLeg.yPath.getRelativePosition(millis).values[0];
            if (playerAnimation.rightLeg.zPath != null) bipedRightLeg.offsetZ = (float) playerAnimation.rightLeg.zPath.getRelativePosition(millis).values[0];
            if (playerAnimation.rightLeg.xRotPath != null) bipedRightLeg.rotateAngleX = (float) Tools.posMod(playerAnimation.rightLeg.xRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.rightLeg.yRotPath != null) bipedRightLeg.rotateAngleY = (float) Tools.posMod(playerAnimation.rightLeg.yRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
            if (playerAnimation.rightLeg.zRotPath != null) bipedRightLeg.rotateAngleZ = (float) Tools.posMod(playerAnimation.rightLeg.zRotPath.getRelativePosition(millis).values[0], Math.PI * 2);
        }


        //Lastly, copy values from body parts to correlating worn armor parts
        copyModelData(bipedHead, bipedHeadwear);
    }

    public static void copyModelData(ModelRenderer from, ModelRenderer to)
    {
        to.offsetX = from.offsetX;
        to.offsetY = from.offsetY;
        to.offsetZ = from.offsetZ;
        copyModelAngles(from, to);
    }

    @Override
    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);

        CBipedAnimation playerAnimation = CBipedAnimation.ANIMATION_DATA.get(entityIn);
        if (playerAnimation != null)
        {
            long millis = System.currentTimeMillis();

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

        GlStateManager.pushMatrix();

        if (isChild)
        {
            GlStateManager.scale(0.75F, 0.75F, 0.75F);
            GlStateManager.translate(0.0F, 16.0F * scale, 0.0F);
            if (headScale != null) GlStateManager.scale(headScale[0], headScale[1], headScale[2]);
            bipedHead.render(scale);
            bipedHeadwear.render(scale);
            GlStateManager.popMatrix();

            GlStateManager.pushMatrix();
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            GlStateManager.translate(0.0F, 24.0F * scale, 0.0F);

            if (chestScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(chestScale[0], chestScale[1], chestScale[2]);
            }
            bipedBody.render(scale);
            if (chestScale != null) GlStateManager.popMatrix();

            if (leftArmScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(leftArmScale[0], leftArmScale[1], leftArmScale[2]);
            }
            bipedLeftArm.render(scale);
            if (leftArmScale != null) GlStateManager.popMatrix();

            if (rightArmScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(rightArmScale[0], rightArmScale[1], rightArmScale[2]);
            }
            bipedRightArm.render(scale);
            if (rightArmScale != null) GlStateManager.popMatrix();

            if (leftLegScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(leftLegScale[0], leftLegScale[1], leftLegScale[2]);
            }
            bipedLeftLeg.render(scale);
            if (leftLegScale != null) GlStateManager.popMatrix();

            if (rightLegScale != null) GlStateManager.scale(rightLegScale[0], rightLegScale[1], rightLegScale[2]);
            bipedRightLeg.render(scale);
        }
        else
        {
            if (entityIn.isSneaking())
            {
                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            if (headScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(headScale[0], headScale[1], headScale[2]);
            }
            bipedHead.render(scale);
            bipedHeadwear.render(scale);
            if (headScale != null) GlStateManager.popMatrix();

            if (chestScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(chestScale[0], chestScale[1], chestScale[2]);
            }
            bipedBody.render(scale);
            if (chestScale != null) GlStateManager.popMatrix();

            if (leftArmScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(leftArmScale[0], leftArmScale[1], leftArmScale[2]);
            }
            bipedLeftArm.render(scale);
            if (leftArmScale != null) GlStateManager.popMatrix();

            if (rightArmScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(rightArmScale[0], rightArmScale[1], rightArmScale[2]);
            }
            bipedRightArm.render(scale);
            if (rightArmScale != null) GlStateManager.popMatrix();

            if (leftLegScale != null)
            {
                GlStateManager.pushMatrix();
                GlStateManager.scale(leftLegScale[0], leftLegScale[1], leftLegScale[2]);
            }
            bipedLeftLeg.render(scale);
            if (leftLegScale != null) GlStateManager.popMatrix();

            if (rightLegScale != null) GlStateManager.scale(rightLegScale[0], rightLegScale[1], rightLegScale[2]);
            bipedRightLeg.render(scale);
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void postRenderArm(float scale, EnumHandSide side)
    {
        if (side == EnumHandSide.LEFT) GlStateManager.scale(leftArmScale[0], leftArmScale[1], leftArmScale[2]);
        else GlStateManager.scale(rightArmScale[0], rightArmScale[1], rightArmScale[2]);


        ModelRenderer armRenderer = getArmForSide(side);
        GlStateManager.translate(armRenderer.offsetX, armRenderer.offsetY, armRenderer.offsetZ);


        getArmForSide(side).postRender(scale);
    }
}
