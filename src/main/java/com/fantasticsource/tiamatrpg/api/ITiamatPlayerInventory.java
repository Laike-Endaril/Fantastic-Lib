package com.fantasticsource.tiamatrpg.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ITiamatPlayerInventory extends IInventory
{
    ItemStack getInactiveMainhand();

    ItemStack getInactiveOffhand();

    ArrayList<ItemStack> getTiamatArmor();

    ArrayList<ItemStack> getQuickSlots();

    ItemStack getBackpack();

    ItemStack getPet();

    ItemStack getDeck();

    ArrayList<ItemStack> getPlayerClasses();

    ArrayList<ItemStack> getOffensiveSkills();

    ArrayList<ItemStack> getUtilitySkills();

    ItemStack getUltimateSkill();

    ArrayList<ItemStack> getPassiveSkills();

    ArrayList<ItemStack> getGatheringProfessions();

    ArrayList<ItemStack> getCraftingProfessions();

    ArrayList<ItemStack> getCraftingRecipes();

    ArrayList<ItemStack> getAllItems();

    ArrayList<ItemStack> getAllEquippedItems();

    boolean unsheathed();

    void setUnsheathed(boolean unsheathed);
}
