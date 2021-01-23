package com.fantasticsource.mctools.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.Collection;
import java.util.Iterator;

public class ItemMatcher
{
    public static boolean stacksMatch(ItemStack stack1, ItemStack stack2)
    {
        return stacksMatch(stack1, stack2, true);
    }

    public static boolean stacksMatch(ItemStack stack1, ItemStack stack2, boolean testCount)
    {
        if (stack1 == stack2) return true;

        if (testCount && stack1.getCount() != stack2.getCount()) return false;

        Item item = stack1.getItem();
        if (item != stack2.getItem()) return false;

        if (!item.isDamageable() && item.getMetadata(stack1) != item.getMetadata(stack2)) return false;

        NBTTagCompound compound1 = stack1.getTagCompound(), compound2 = stack2.getTagCompound();
        return compound1 == null ? compound2 == null : compound1.equals(compound2);
    }

    public static boolean stacksMatch(Collection<ItemStack> stacks1, Collection<ItemStack> stacks2)
    {
        return stacksMatch(stacks1, stacks2, true);
    }

    public static boolean stacksMatch(Collection<ItemStack> stacks1, Collection<ItemStack> stacks2, boolean testCount)
    {
        ItemStack stack1;
        Iterator iterator1 = stacks1.iterator();
        for (ItemStack stack2 : stacks2)
        {
            stack1 = (ItemStack) iterator1.next();


            if (stack1 == stack2) continue;

            if (testCount && stack1.getCount() != stack2.getCount()) return false;

            Item item = stack1.getItem();
            if (item != stack2.getItem()) return false;

            if (!item.isDamageable() && item.getMetadata(stack1) != item.getMetadata(stack2)) return false;
        }

        iterator1 = stacks1.iterator();
        for (ItemStack stack2 : stacks2)
        {
            stack1 = (ItemStack) iterator1.next();


            NBTTagCompound compound1 = stack1.getTagCompound(), compound2 = stack2.getTagCompound();
            if (compound1 == null ? compound2 != null : !compound1.equals(compound2)) return false;
        }

        return true;
    }

    public static boolean stacksMatch(ItemStack[] stacks1, ItemStack[] stacks2)
    {
        return stacksMatch(stacks1, stacks2, true);
    }

    public static boolean stacksMatch(ItemStack[] stacks1, ItemStack[] stacks2, boolean testCount)
    {
        ItemStack stack1, stack2;
        int size = stacks1.length;

        for (int i = 0; i < size; i++)
        {
            stack1 = stacks1[i];
            stack2 = stacks2[i];


            if (stack1 == stack2) continue;

            if (testCount && stack1.getCount() != stack2.getCount()) return false;

            Item item = stack1.getItem();
            if (item != stack2.getItem()) return false;

            if (!item.isDamageable() && item.getMetadata(stack1) != item.getMetadata(stack2)) return false;
        }

        for (int i = 0; i < size; i++)
        {
            stack1 = stacks1[i];
            stack2 = stacks2[i];


            NBTTagCompound compound1 = stack1.getTagCompound(), compound2 = stack2.getTagCompound();
            if (compound1 == null ? compound2 != null : !compound1.equals(compound2)) return false;
        }

        return true;
    }
}
