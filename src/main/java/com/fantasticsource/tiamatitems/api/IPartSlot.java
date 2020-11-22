package com.fantasticsource.tiamatitems.api;

import net.minecraft.item.ItemStack;

import java.util.HashSet;

public interface IPartSlot
{
    String getSlotType();

    void setSlotType(String slotType);

    boolean getRequired();

    void setRequired(boolean required);

    ItemStack getPart();

    void setPart(ItemStack part);

    HashSet<String> getValidItemTypes();

    boolean partIsValidForSlot(ItemStack part);
}
