package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class CustomAWSkinHandler
{
    public static Class
            awModAddonManagerClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.addons.ModAddonManager"),
            awIEntitySkinCapabilityClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.capability.entityskin.IEntitySkinCapability"),
            awEntitySkinCapabilityClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability"),
            awSkinLayerRendererHeldItemClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererHeldItem");

    public static Field
            awItemOverridesField = ReflectionTool.getField(awModAddonManagerClass, "ITEM_OVERRIDES"),
            renderLivingBaseLayerRenderersField = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers");

    public static Method
            awEntitySkinCapabilityGetMethod = ReflectionTool.getMethod(awEntitySkinCapabilityClass, "get"),
            awSkinLayerRendererHeldItemRenderHeldItemMethod = ReflectionTool.getMethod(awSkinLayerRendererHeldItemClass, "renderHeldItem");

    public static HashSet<String> awItemOverrides = null;

    static
    {
        try
        {
            awItemOverrides = (HashSet<String>) awItemOverridesField.get(null);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


    public static void setForcedAWSkinType(ItemStack stack, String skinType)
    {
        if (skinType == null || skinType.equals(""))
        {
            removeForcedAWSkinType(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("type", skinType);
    }

    public static String getForcedAWSkinType(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) return null;
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("type")) return null;
        return compound.getString("type");
    }

    public static void removeForcedAWSkinType(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound mainTag = stack.getTagCompound();

        if (!mainTag.hasKey(DOMAIN)) return;
        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);

        compound.removeTag("type");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    public static void addTransientAWSkin(ItemStack stack, String libraryFile, String skinType, Color... dyes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("AWSkins")) compound.setTag("AWSkins", new NBTTagList());
        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        list.appendTag(compound);

        compound.setString("file", libraryFile);
        compound.setString("type", skinType);

        if (dyes.length > 0)
        {
            list = new NBTTagList();
            compound.setTag("dyes", list);

            for (Color dye : dyes)
            {
                list.appendTag(new NBTTagInt(dye.color()));
            }
        }
    }

    public static void clearTransientAWSkins(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound mainTag = stack.getTagCompound();

        if (!mainTag.hasKey(DOMAIN)) return;
        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);

        compound.removeTag("AWSkins");

        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    //These two methods are the core part of making AW recognize items as being skinnable via NBT
    private static void testEnableAWSkinOverrideHack(ItemStack stack)
    {
        String forcedSkinType = getForcedAWSkinType(stack);
        if (forcedSkinType == null) return;


        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        String key = forcedSkinType + ":" + stack.getItem().getRegistryName();


        //If the overrides already contain the key, then it's there from somewhere else already and we don't need a forced skin type tag on this itemstack for it to apply
        if (awItemOverrides.contains(key))
        {
            removeForcedAWSkinType(stack);
            return;
        }


        awItemOverrides.add(key);
        compound.setString("mark1", key);
    }

    private static void testDisableAWSkinOverrideHack(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("mark1")) return;


        String key = compound.getString("mark1");
        awItemOverrides.remove(key);

        compound.removeTag("mark1");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    //The event for these two methods only applies to the local player's own held items, rendered in 1st person view
    //This is part of making AW recognize items as being skinnable via NBT
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderSpecificHandBeforeAW(RenderSpecificHandEvent event)
    {
        testEnableAWSkinOverrideHack(event.getItemStack());
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void renderSpecificHandAfterAW(RenderSpecificHandEvent event)
    {
        testDisableAWSkinOverrideHack(event.getItemStack());
    }


    //This event and the class below apply to all items except those viewed in 1st person, in your own hands
    //This is part of making AW recognize items as being skinnable via NBT
    @SubscribeEvent
    public static void entityJoinWorld(EntityJoinWorldEvent event) throws IllegalAccessException
    {
        if (!event.getWorld().isRemote) return;


        Entity entity = event.getEntity();
        if (!(entity instanceof EntityLivingBase)) return;


        Render render = Minecraft.getMinecraft().getRenderManager().getEntityRenderObject(event.getEntity());
        if (!(render instanceof RenderLivingBase)) return;


        List<LayerRenderer> layerRenderers = (List<LayerRenderer>) renderLivingBaseLayerRenderersField.get(render);
        for (LayerRenderer layer : layerRenderers.toArray(new LayerRenderer[0]))
        {
            if (layer.getClass() == awSkinLayerRendererHeldItemClass)
            {
                layerRenderers.remove(layer);
                layerRenderers.add(new AWSkinRenderLayerWrapper((RenderLivingBase) render, (LayerHeldItem) layer));
            }
        }
    }

    public static class AWSkinRenderLayerWrapper extends LayerHeldItem
    {
        public LayerHeldItem awLayer;

        public AWSkinRenderLayerWrapper(RenderLivingBase<?> livingEntityRendererIn, LayerHeldItem awLayer)
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
                try
                {
                    Object skinCapability = awEntitySkinCapabilityGetMethod.invoke(null, entitylivingbaseIn);
                    GlStateManager.pushMatrix();

                    if (this.livingEntityRenderer.getMainModel().isChild)
                    {
                        GlStateManager.translate(0.0F, 0.75F, 0.0F);
                        GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    }

                    testEnableAWSkinOverrideHack(rightStack);
                    awSkinLayerRendererHeldItemRenderHeldItemMethod.invoke(awLayer, entitylivingbaseIn, rightStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability);
                    testDisableAWSkinOverrideHack(rightStack);

                    testEnableAWSkinOverrideHack(leftStack);
                    awSkinLayerRendererHeldItemRenderHeldItemMethod.invoke(awLayer, entitylivingbaseIn, leftStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability);
                    testDisableAWSkinOverrideHack(leftStack);

                    GlStateManager.popMatrix();
                }
                catch (IllegalAccessException | InvocationTargetException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
