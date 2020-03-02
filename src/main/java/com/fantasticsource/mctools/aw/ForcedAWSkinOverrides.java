package com.fantasticsource.mctools.aw;

import com.fantasticsource.tools.ReflectionTool;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
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
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class ForcedAWSkinOverrides
{
    public static Class
            awModAddonManagerClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.addons.ModAddonManager"),
            awSkinLayerRendererHeldItemClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererHeldItem");

    public static Field
            awItemOverridesField = ReflectionTool.getField(awModAddonManagerClass, "ITEM_OVERRIDES"),
            renderLivingBaseLayerRenderersField = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers");

    public static Method awSkinLayerRendererHeldItemRenderHeldItemMethod = ReflectionTool.getMethod(awSkinLayerRendererHeldItemClass, "renderHeldItem");

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

        compound.setString("awType", skinType);
    }

    public static String getForcedAWSkinType(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) return null;
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("awType")) return null;
        return compound.getString("awType");
    }

    public static void removeForcedAWSkinType(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound mainTag = stack.getTagCompound();

        if (!mainTag.hasKey(DOMAIN)) return;
        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);

        compound.removeTag("awType");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    //These two methods are the core part of making AW recognize items as being skinnable via NBT
    private static void tryEnableAWSkinOverrideHack(ItemStack stack)
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
        compound.setString("awOverride", key);
    }

    private static void tryDisableAWSkinOverrideHack(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("awOverride")) return;


        String key = compound.getString("awOverride");
        awItemOverrides.remove(key);

        compound.removeTag("awOverride");
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
        tryEnableAWSkinOverrideHack(event.getItemStack());
    }

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void renderSpecificHandAfterAW(RenderSpecificHandEvent event)
    {
        tryDisableAWSkinOverrideHack(event.getItemStack());
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
                    IEntitySkinCapability skinCapability = ArmourersWorkshopApi.getEntitySkinCapability(entitylivingbaseIn);
                    GlStateManager.pushMatrix();

                    if (this.livingEntityRenderer.getMainModel().isChild)
                    {
                        GlStateManager.translate(0.0F, 0.75F, 0.0F);
                        GlStateManager.scale(0.5F, 0.5F, 0.5F);
                    }

                    tryEnableAWSkinOverrideHack(rightStack);
                    awSkinLayerRendererHeldItemRenderHeldItemMethod.invoke(awLayer, entitylivingbaseIn, rightStack, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT, skinCapability);
                    tryDisableAWSkinOverrideHack(rightStack);

                    tryEnableAWSkinOverrideHack(leftStack);
                    awSkinLayerRendererHeldItemRenderHeldItemMethod.invoke(awLayer, entitylivingbaseIn, leftStack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT, skinCapability);
                    tryDisableAWSkinOverrideHack(leftStack);

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
