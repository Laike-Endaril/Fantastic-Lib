package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.lang.reflect.Field;
import java.util.HashSet;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class CustomAWSkinHandler
{
    public static Class awModAddonManagerClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.addons.ModAddonManager");
    public static Field awItemOverridesField = ReflectionTool.getField(awModAddonManagerClass, "ITEM_OVERRIDES");
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


    //The event for these two methods only applies to the local player's own held items, rendered in 1st person view
    //This is part of making AW recognize items as being skinnable via NBT
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void renderSpecificHandBeforeAW(RenderSpecificHandEvent event)
    {
        ItemStack stack = event.getItemStack();
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

    @SubscribeEvent(priority = EventPriority.LOW, receiveCanceled = true)
    public static void renderSpecificHandAfterAW(RenderSpecificHandEvent event)
    {
        ItemStack stack = event.getItemStack();
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
}
