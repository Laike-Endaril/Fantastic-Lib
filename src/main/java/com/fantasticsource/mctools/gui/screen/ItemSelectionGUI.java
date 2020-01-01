package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIItemStackText;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class ItemSelectionGUI extends GUIScreen
{
    public ItemStack selection;

    public ItemSelectionGUI(GUIItemStackText clickedElement)
    {
        this(clickedElement, 1);
    }

    public ItemSelectionGUI(GUIItemStackText clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        selection = clickedElement.getStack();


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        GUITextSpacer spacer = new GUITextSpacer(this, true);
        GUIScrollView scrollView = new GUIScrollView(this, 0.98 - spacer.width * 2, 1);
        root.add(spacer.addRecalcActions(() -> scrollView.width = 0.98 - spacer.width * 2));
        root.add(scrollView);

        root.add(new GUITextSpacer(this, true));
        root.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, scrollView));

        scrollView.add(new GUITextSpacer(this));

        //Current
        GUIItemStackText stackElement = new GUIItemStackText(this, clickedElement.getStack().copy());
        stackElement.setText("" + TextFormatting.RESET + TextFormatting.DARK_PURPLE + "(currently selected)");
        scrollView.add(stackElement);
        scrollView.add(new GUITextSpacer(this));
        scrollView.add(new GUITextSpacer(this));

        //Remove
        stackElement = new GUIItemStackText(this, ItemStack.EMPTY);
        stackElement.setText(TextFormatting.DARK_PURPLE + "(Remove item)");
        scrollView.add(stackElement);
        scrollView.add(new GUITextSpacer(this));
        scrollView.add(new GUITextSpacer(this));
        scrollView.add(new GUITextSpacer(this));

        //Player inventory
        EntityPlayer player = Minecraft.getMinecraft().player;
        for (int i = 0; i < player.inventory.getSizeInventory(); i++)
        {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                scrollView.add(new GUIItemStackText(this, stack.copy()));
                scrollView.add(new GUITextSpacer(this));
            }
        }

        for (int i = scrollView.size() - 1; i >= 0; i--)
        {
            GUIElement element = scrollView.get(i);
            if (element instanceof GUIItemStackText)
            {
                element.addClickActions(() ->
                {
                    selection = ((GUIItemStackText) element).getStack();
                    close();
                });
            }
        }
    }

    @Override
    public String title()
    {
        return "Item";
    }
}
