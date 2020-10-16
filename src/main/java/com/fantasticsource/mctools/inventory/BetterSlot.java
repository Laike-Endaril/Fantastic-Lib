package com.fantasticsource.mctools.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class BetterSlot extends Slot
{
    public final int u, v;
    public boolean enabled = true;

    public BetterSlot(IInventory inventoryIn, int index, int x, int y, int u, int v)
    {
        super(inventoryIn, index, x, y);
        this.u = u;
        this.v = v;
    }

    public BetterSlot enable()
    {
        enabled = true;
        return this;
    }

    public BetterSlot disable()
    {
        enabled = false;
        return this;
    }

    @Override
    public boolean isEnabled()
    {
        return enabled;
    }

    public BetterSlot setEnabled(boolean enabled)
    {
        if (enabled) enable();
        else disable();
        return this;
    }
}
