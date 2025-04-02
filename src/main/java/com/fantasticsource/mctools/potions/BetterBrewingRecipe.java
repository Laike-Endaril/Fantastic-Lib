package com.fantasticsource.mctools.potions;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraftforge.common.brewing.BrewingRecipe;

import javax.annotation.Nonnull;

public class BetterBrewingRecipe extends BrewingRecipe
{
    protected PotionType basePotion, result;

    public BetterBrewingRecipe(BetterPotionType potionType)
    {
        this(potionType.recipeBasePotion, potionType.recipeAddedItem, potionType);
    }

    public BetterBrewingRecipe(PotionType basePotion, ItemStack addedItem, PotionType result)
    {
        super(PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), basePotion), addedItem, PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), result));

        this.basePotion = basePotion;
        this.result = result;
    }

    @Override
    public boolean isInput(@Nonnull ItemStack stack)
    {
        return PotionUtils.getPotionFromItem(stack) == basePotion;
    }

    @Nonnull
    @Override
    public ItemStack getOutput(@Nonnull ItemStack input, @Nonnull ItemStack ingredient)
    {
        if (isInput(input) && isIngredient(ingredient)) return PotionUtils.addPotionToItemStack(new ItemStack(input.getItem()), result);
        return ItemStack.EMPTY;
    }
}
