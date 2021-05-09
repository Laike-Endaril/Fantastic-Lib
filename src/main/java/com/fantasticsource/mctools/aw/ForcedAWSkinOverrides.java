package com.fantasticsource.mctools.aw;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class ForcedAWSkinOverrides
{
    public static Class awModAddonManagerClass = null, awSkinLayerRendererHeldItemClass = null;
    public static Field awItemOverridesField = null, renderLivingBaseLayerRenderersField = null;
    public static Method awSkinLayerRendererHeldItemRenderHeldItemMethod = null;

    public static HashSet<String> awItemOverrides = null;


    @SideOnly(Side.CLIENT)
    public static void clientInit() throws IllegalAccessException
    {
        awModAddonManagerClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.addons.ModAddonManager");
        awSkinLayerRendererHeldItemClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.client.render.entity.SkinLayerRendererHeldItem");

        awItemOverridesField = ReflectionTool.getField(awModAddonManagerClass, "ITEM_OVERRIDES");
        renderLivingBaseLayerRenderersField = ReflectionTool.getField(RenderLivingBase.class, "field_177097_h", "layerRenderers");

        awSkinLayerRendererHeldItemRenderHeldItemMethod = ReflectionTool.getMethod(awSkinLayerRendererHeldItemClass, "renderHeldItem");

        awItemOverrides = (HashSet<String>) awItemOverridesField.get(null);


        MinecraftForge.EVENT_BUS.register(ForcedAWSkinOverrides.class);
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
    public static void tryEnableAWSkinOverrideHack(ItemStack stack)
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

    public static void tryDisableAWSkinOverrideHack(ItemStack stack)
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
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void clientRenderSpecificHandBeforeAW(RenderSpecificHandEvent event)
    {
        tryEnableAWSkinOverrideHack(event.getItemStack());
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void clientRenderSpecificHandAfterAW(RenderSpecificHandEvent event)
    {
        tryDisableAWSkinOverrideHack(event.getItemStack());
    }
}
