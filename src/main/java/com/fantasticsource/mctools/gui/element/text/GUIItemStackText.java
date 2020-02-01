package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUITooltipView;
import com.fantasticsource.mctools.gui.screen.ItemSelectionGUI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
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
        super(screen, stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + stack.getDisplayName(), scale);
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


    public GUIItemStackText(GUIScreen screen, double x, double y, ItemStack stack)
    {
        this(screen, x, y, stack, 1);
    }

    public GUIItemStackText(GUIScreen screen, double x, double y, ItemStack stack, double scale)
    {
        super(screen, x, y, stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + stack.getDisplayName(), scale);
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

    public GUIItemStackText setStack(ItemStack stack)
    {
        this.stack = stack;
        text = stack.isEmpty() ? "(Empty Slot)" : stack.getCount() + " " + stack.getDisplayName();

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

    @Override
    public void click()
    {
        new ItemSelectionGUI(this, screen.textScale);
        super.click();
    }
}
