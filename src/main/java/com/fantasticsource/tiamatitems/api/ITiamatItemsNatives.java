package com.fantasticsource.tiamatitems.api;

import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ITiamatItemsNatives
{
    ArrayList<IPartSlot> getPartSlots(ItemStack stack);
}
