package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;

public class ItemstackSelectionGUI extends GUIScreen
{
    public final String title;

    public ItemstackSelectionGUI(GUIItemStack clickedElement, String title, ItemStack... options)
    {
        this(clickedElement, title, 1, options);
    }

    public ItemstackSelectionGUI(GUIItemStack clickedElement, String title, double textScale, ItemStack... options)
    {
        super(textScale);
        this.title = title;

        show();
        drawStack = false;


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Navigation bar
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Scrollview
        GUIScrollView view = new GUIScrollView(this, 0.98, 1 - navbar.height);
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1 - navbar.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, view);
        navbar.addRecalcActions(() ->
        {
            view.height = 1 - navbar.height;
            scrollbar.height = 1 - navbar.height;
        });
        root.addAll(view, scrollbar);


        //Add options
        for (ItemStack option : options)
        {
            GUIItemStack guiItemStack = new GUIItemStack(this, 16, 16, option);
            if (option.equals(clickedElement.getItemStack())) guiItemStack.add(new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.PURPLE), Color.BLANK, getHoverColor(Color.PURPLE), Color.BLANK, Color.PURPLE, Color.BLANK));
            else guiItemStack.add(new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK));
            view.add(guiItemStack.addClickActions(() ->
            {
                clickedElement.setItemStack(option);
                close();
            }));
        }
    }

    @Override
    public String title()
    {
        return title;
    }
}
