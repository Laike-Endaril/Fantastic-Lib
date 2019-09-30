package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStack;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemSelectionGUI extends GUIScreen
{
    public ItemStack selection;

    public ItemSelectionGUI(GUIItemStack clickedElement)
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.getStack();


        guiElements.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.7f)));

        GUIScrollView scrollView = new GUIScrollView(this, 0.02, 0, 0.94, 1);
        guiElements.add(scrollView);
        guiElements.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));

        scrollView.add(new GUIText(this, "\n"));

        //Current
        GUIItemStack stackElement = new GUIItemStack(this, clickedElement.getStack().copy());
        stackElement.text += "" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + " (currently selected)";
        scrollView.add(stackElement);
        scrollView.add(new GUIText(this, "\n\n"));

        //Remove
        stackElement = new GUIItemStack(this, ItemStack.EMPTY);
        stackElement.text = TextFormatting.DARK_PURPLE + "(Remove item)";
        scrollView.add(stackElement);
        scrollView.add(new GUIText(this, "\n\n\n"));

        //Player inventory
        EntityPlayer player = Minecraft.getMinecraft().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                scrollView.add(new GUIItemStack(this, stack.copy()));
                scrollView.add(new GUIText(this, "\n"));
            }
        }

        for (int i = scrollView.size() - 1; i >= 0; i--)
        {
            GUIElement element = scrollView.get(i);
            if (element instanceof GUIItemStack)
            {
                element.addClickActions(() ->
                {
                    selection = ((GUIItemStack) element).getStack();
                    close();
                });
            }
        }
    }

    @Override
    protected void init()
    {
    }
}
