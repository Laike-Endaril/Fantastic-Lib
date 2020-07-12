package com.fantasticsource.tiamatinventory.api;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public interface ITiamatPlayerInventory extends IInventory
{
    ItemStack getSheathedMainhand1();

    void setSheathedMainhand1(ItemStack stack);

    ItemStack getSheathedOffhand1();

    void setSheathedOffhand1(ItemStack stack);

    ItemStack getSheathedMainhand2();

    void setSheathedMainhand2(ItemStack stack);

    ItemStack getSheathedOffhand2();

    void setSheathedOffhand2(ItemStack stack);

    ArrayList<ItemStack> getTiamatArmor();

    ItemStack getShoulders();

    void setShoulders(ItemStack stack);

    ItemStack getCape();

    void setCape(ItemStack stack);

    ArrayList<ItemStack> getQuickSlots();

    void setQuickSlot(int index, ItemStack stack);

    ItemStack getBackpack();

    void setBackpack(ItemStack stack);

    ItemStack getPet();

    void setPet(ItemStack stack);

    ItemStack getDeck();

    void setDeck(ItemStack stack);

    ArrayList<ItemStack> getPlayerClasses();

    void setPlayerClass(int index, ItemStack stack);

    ArrayList<ItemStack> getOffensiveSkills();

    void setOffensiveSkill(int index, ItemStack stack);

    ArrayList<ItemStack> getUtilitySkills();

    void setUtilitySkill(int index, ItemStack stack);

    ItemStack getUltimateSkill();

    void setUltimateSkill(ItemStack stack);

    ArrayList<ItemStack> getPassiveSkills();

    void setPassiveSkill(int index, ItemStack stack);

    ArrayList<ItemStack> getGatheringProfessions();

    void setGatheringProfession(int index, ItemStack stack);

    ArrayList<ItemStack> getCraftingProfessions();

    void setCraftingProfession(int index, ItemStack stack);

    ArrayList<ItemStack> getCraftingRecipes();

    void setCraftingRecipe(int index, ItemStack stack);

    ArrayList<ItemStack> getAllItems();

    ArrayList<ItemStack> getAllEquippedItems();
}
