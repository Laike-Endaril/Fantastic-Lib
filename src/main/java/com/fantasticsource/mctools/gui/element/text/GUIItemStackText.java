package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraft.item.ItemStack;

public class GUIItemStackText extends GUIText
{
    private ItemStack stack;

    public GUIItemStackText(GUIScreen screen, ItemStack stack)
    {
        this(screen, stack, 1);
    }

    public GUIItemStackText(GUIScreen screen, ItemStack stack, double scale)
    {
        super(screen, "", scale);
        setStack(stack);
    }


    public GUIItemStackText(GUIScreen screen, double x, double y, ItemStack stack)
    {
        this(screen, x, y, stack, 1);
    }

    public GUIItemStackText(GUIScreen screen, double x, double y, ItemStack stack, double scale)
    {
        super(screen, x, y, "", scale);
        setStack(stack);
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public GUIItemStackText setStack(ItemStack stack)
    {
        this.stack = stack;
        text = stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + stack.getDisplayName();
        tooltip = stack;
        return this;
    }
}
