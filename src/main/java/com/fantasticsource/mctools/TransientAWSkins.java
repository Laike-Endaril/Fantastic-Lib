package com.fantasticsource.mctools;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class TransientAWSkins
{
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
}
