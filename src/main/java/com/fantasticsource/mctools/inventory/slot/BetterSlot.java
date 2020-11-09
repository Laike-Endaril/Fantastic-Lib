package com.fantasticsource.mctools.inventory.slot;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.util.ResourceLocation;

public class BetterSlot extends Slot
{
    public final ResourceLocation texture;
    public final int u, v, texWidth16x, texHeight16x;
    public final double uPixel, vPixel;
    public boolean enabled = true;

    public BetterSlot(IInventory inventoryIn, int index, int x, int y, ResourceLocation texture, int texWidth16x, int texHeight16x, int u, int v)
    {
        super(inventoryIn, index, x, y);
        this.texture = texture;
        this.u = u;
        this.v = v;
        this.texWidth16x = texWidth16x;
        this.texHeight16x = texHeight16x;
        uPixel = 1d / texWidth16x;
        vPixel = 1d / texHeight16x;
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
