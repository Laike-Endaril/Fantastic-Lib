package com.fantasticsource.mctools;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;

public class MultiIngredient extends Ingredient
{
    public Ingredient[] ingredients;

    public MultiIngredient(Ingredient... ingredients)
    {
        super(0);
        this.ingredients = ingredients;
    }

    @Override
    public ItemStack[] getMatchingStacks()
    {
        ArrayList<ItemStack> itemStacks = new ArrayList<>();
        for (Ingredient ingredient : ingredients)
        {
            itemStacks.addAll(Arrays.asList(ingredient.getMatchingStacks()));
        }
        return itemStacks.toArray(new ItemStack[0]);
    }

    @Override
    public IntList getValidItemStacksPacked()
    {
        IntList result = new IntArrayList();
        for (Ingredient ingredient : ingredients) result.addAll(ingredient.getValidItemStacksPacked());
        return result;
    }

    @Override
    public boolean apply(@Nullable ItemStack stack)
    {
        for (Ingredient ingredient : ingredients)
        {
            if (ingredient.apply(stack)) return true;
        }
        return false;
    }

    @Override
    protected void invalidate()
    {
    }

    @Override
    public boolean isSimple()
    {
        for (Ingredient ingredient : ingredients)
        {
            if (!ingredient.isSimple()) return false;
        }
        return true;
    }
}
