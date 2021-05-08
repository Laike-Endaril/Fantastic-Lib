package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.*;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;

import java.lang.reflect.Field;
import java.util.Map;

public class ModelPlayerEdit extends ModelPlayer
{
    public static final Field
            RENDER_LIVING_BASE_MAIN_MODEL_FIELD = ReflectionTool.getField(RenderLivingBase.class, "field_77045_g", "mainModel"),
            MODEL_PLAYER_BIPED_CAPE_FIELD = ReflectionTool.getField(ModelPlayer.class, "field_178729_w", "bipedCape"),
            MODEL_BOX_VERTEX_POSITIONS_FIELD = ReflectionTool.getField(ModelBox.class, "field_78253_h", "vertexPositions");


    public ModelPlayerEdit(ModelPlayer oldModel)
    {
        this(getModelSize(oldModel), isSmallArms(oldModel));
    }

    public ModelPlayerEdit(float modelSize, boolean smallArmsIn)
    {
        super(modelSize, smallArmsIn);
    }


    public static float getModelSize(ModelPlayer modelPlayer)
    {
        ModelBox box = modelPlayer.bipedLeftArm.cubeList.get(0);
        PositionTextureVertex[] vertexPositions = (PositionTextureVertex[]) ReflectionTool.get(MODEL_BOX_VERTEX_POSITIONS_FIELD, box);
        return (float) (box.posX1 - vertexPositions[0].vector3D.x);
    }

    public static boolean isSmallArms(ModelPlayer modelPlayer)
    {
        ModelBox box = modelPlayer.bipedLeftArm.cubeList.get(0);
        return box.posX2 - box.posX1 == 3;
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

        if (rightArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
        {
            bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY;
            bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY + 0.4F;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
        }
        else if (leftArmPose == ModelBiped.ArmPose.BOW_AND_ARROW)
        {
            bipedRightArm.rotateAngleY = -0.1F + bipedHead.rotateAngleY - 0.4F;
            bipedLeftArm.rotateAngleY = 0.1F + bipedHead.rotateAngleY;
            bipedRightArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
            bipedLeftArm.rotateAngleX = -((float) Math.PI / 2F) + bipedHead.rotateAngleX;
        }

        copyModelAngles(bipedHead, bipedHeadwear);


        //From ModelPlayer
        copyModelAngles(bipedLeftLeg, bipedLeftLegwear);
        copyModelAngles(bipedRightLeg, bipedRightLegwear);
        copyModelAngles(bipedLeftArm, bipedLeftArmwear);
        copyModelAngles(bipedRightArm, bipedRightArmwear);
        copyModelAngles(bipedBody, bipedBodyWear);

        ModelRenderer bipedCape = (ModelRenderer) ReflectionTool.get(MODEL_PLAYER_BIPED_CAPE_FIELD, this);
        if (entityIn.isSneaking()) bipedCape.rotationPointY = 2.0F;
        else bipedCape.rotationPointY = 0.0F;


        //Custom via paths
    }


    public static void init(FMLPostInitializationEvent event)
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
        }
    }
}
