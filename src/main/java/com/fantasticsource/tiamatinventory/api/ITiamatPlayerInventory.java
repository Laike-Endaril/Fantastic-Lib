package com.fantasticsource.tiamatinventory.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ITiamatPlayerInventory extends IInventory
{
    ItemStack getSheathedMainhand1();

    ItemStack getSheathedOffhand1();

    ItemStack getSheathedMainhand2();

    ItemStack getSheathedOffhand2();

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
}
