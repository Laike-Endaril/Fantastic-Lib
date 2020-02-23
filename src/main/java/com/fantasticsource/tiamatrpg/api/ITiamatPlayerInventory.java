package com.fantasticsource.tiamatrpg.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ITiamatPlayerInventory extends IInventory
{
    ItemStack getInactiveWeaponsetMainhand();

    ItemStack getInactiveWeaponsetOffhand();

    ArrayList<ItemStack> getTiamatArmor();

    ItemStack getPet();

    ArrayList<ItemStack> getPlayerClasses();

    ArrayList<ItemStack> getSkills();

    ArrayList<ItemStack> getGatheringProfessions();

    ArrayList<ItemStack> getCraftingProfessions();

    ArrayList<ItemStack> getCraftingRecipes();

    ArrayList<ItemStack> getReadySkills();

    ArrayList<ItemStack> getAllItems();

    ArrayList<ItemStack> getAllEquippedItems();
}
