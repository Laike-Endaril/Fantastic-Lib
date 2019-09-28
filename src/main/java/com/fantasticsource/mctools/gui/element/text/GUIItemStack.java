package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUITooltipView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class GUIItemStack extends GUIText
{
    public ItemStack stack;
    GUITooltipView tooltip = new GUITooltipView(screen);

    public GUIItemStack(GUIScreen screen, ItemStack stack)
    {
        super(screen, stack == ItemStack.EMPTY ? TextFormatting.GOLD + "(Empty Slot)" : "" + TextFormatting.GOLD + stack.getCount() + "x " + TextFormatting.RESET + stack.getDisplayName());
        this.stack = stack;

        tooltip.setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
        Minecraft mc = Minecraft.getMinecraft();
        for (String line : stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL))
        {
            tooltip.add(new GUIText(screen, line));
        }
    }

    public GUIItemStack(GUIScreen screen, double x, double y, ItemStack stack)
    {
        super(screen, x, y, stack == ItemStack.EMPTY ? TextFormatting.GOLD + "(Empty Slot)" : "" + TextFormatting.GOLD + stack.getCount() + "x " + TextFormatting.RESET + stack.getDisplayName());
        this.stack = stack;

        tooltip.setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
        Minecraft mc = Minecraft.getMinecraft();
        for (String line : stack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL))
        {
            tooltip.add(new GUIText(screen, line));
        }
    }

    @Override
    public void draw()
    {
        super.draw();

        if (isMouseWithin()) tooltip.draw();
    }
}
