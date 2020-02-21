package com.fantasticsource.mctools.items;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemMatcher
{
    public static boolean stacksMatch(ItemStack stack1, ItemStack stack2)
    {
        if (stack1 == stack2) return true;

        Item item1 = stack1.getItem();
        if (!item1.equals(stack2.getItem())) return false;
        if (!item1.isDamageable() && stack1.getMetadata() != stack2.getMetadata()) return false;
        if (stack1.hasTagCompound() != stack2.hasTagCompound()) return false;
        return !stack1.hasTagCompound() || stack1.getTagCompound().equals(stack2.getTagCompound());
    }
}
