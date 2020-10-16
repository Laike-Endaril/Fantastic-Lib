package com.fantasticsource.mctools.inventory.slot;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;

import java.util.function.Predicate;

public class FilteredSlot extends BetterSlot
{
    public final Predicate<ItemStack> filter;
    public final boolean bindingCursable;
    public final int slotStackLimit;

    public FilteredSlot(IInventory inventoryIn, int index, int x, int y, int u, int v, boolean bindingCursable, int slotStackLimit, Predicate<ItemStack> filter)
    {
        super(inventoryIn, index, x, y, u, v);

        this.filter = filter;
        this.bindingCursable = bindingCursable;
        this.slotStackLimit = slotStackLimit;
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        return filter.test(stack);
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        if (!bindingCursable || player.isCreative()) return true;

        ItemStack itemstack = getStack();
        return (itemstack.isEmpty() || !EnchantmentHelper.hasBindingCurse(itemstack));
    }

    @Override
    public int getSlotStackLimit()
    {
        return slotStackLimit;
    }
}
