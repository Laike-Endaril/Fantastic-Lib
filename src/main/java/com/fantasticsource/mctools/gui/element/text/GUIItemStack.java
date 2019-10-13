package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUITooltipView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GUIItemStack extends GUIText
{
    private ItemStack stack;

    public GUIItemStack(GUIScreen screen, ItemStack stack)
    {
        super(screen, stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + TextFormatting.RESET + stack.getDisplayName());
        this.stack = stack;

        tooltip = new GUITooltipView(screen);
        tooltip.setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
        Minecraft mc = Minecraft.getMinecraft();

        for (String line : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.ADVANCED))
        {
            GUIAutocroppedView view = new GUIAutocroppedView(screen, 0.3);
            view.add(new GUIText(screen, line));
            tooltip.add(view);
        }
    }

    public GUIItemStack(GUIScreen screen, double x, double y, ItemStack stack)
    {
        super(screen, x, y, stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + TextFormatting.RESET + stack.getDisplayName());
        this.stack = stack;

        tooltip = new GUITooltipView(screen);
        tooltip.setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
        Minecraft mc = Minecraft.getMinecraft();

        for (String line : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.ADVANCED))
        {
            GUIAutocroppedView view = new GUIAutocroppedView(screen, 0.3);
            view.add(new GUIText(screen, line));
            tooltip.add(view);
        }
    }

    public ItemStack getStack()
    {
        return stack;
    }

    public GUIItemStack setStack(ItemStack stack)
    {
        this.stack = stack;
        text = stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + TextFormatting.RESET + stack.getDisplayName();

        tooltip.clear();
        Minecraft mc = Minecraft.getMinecraft();

        for (String line : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.ADVANCED))
        {
            GUIAutocroppedView view = new GUIAutocroppedView(screen, 0.3);
            view.add(new GUIText(screen, line));
            tooltip.add(view);
        }

        return this;
    }
}