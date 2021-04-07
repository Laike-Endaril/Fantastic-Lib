package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.ItemStack;

public class ItemstackSelectionGUI extends GUIScreen
{
    public static final int
            MODE_DROPDOWN = 0,
            MODE_FULLSCREEN = 1;

    public final String title;

    public ItemstackSelectionGUI(GUIItemStack clickedElement, String title, ItemStack... options)
    {
        this(clickedElement, title, 1, options);
    }

    public ItemstackSelectionGUI(GUIItemStack clickedElement, int dropdownW, int dropdownH, String title, ItemStack... options)
    {
        this(clickedElement, dropdownW, dropdownH, title, 1, options);
    }

    public ItemstackSelectionGUI(GUIItemStack clickedElement, String title, double textScale, ItemStack... options)
    {
        this(clickedElement, 9, 5, title, textScale, options);
    }

    public ItemstackSelectionGUI(GUIItemStack clickedElement, int dropdownW, int dropdownH, String title, double textScale, ItemStack... options)
    {
        super(textScale);
        this.title = title;

        show();


        //Background
        root.add(new GUIDarkenedBackground(this));


        //Navbar
        GUINavbar navbar = new GUINavbar(this);
        root.add(navbar);


        //Scrollview
        double x = 0, y = navbar.height, w = 0.98, h = 1 - navbar.height;
        boolean scrollbar = true;
        if (dropdownW > 0 && dropdownH != 0)
        {
            w = clickedElement.absoluteWidth() * dropdownW;
            h = clickedElement.absoluteHeight() * dropdownH;
            x = clickedElement.absoluteX();
            double clickedBottom = clickedElement.absoluteY() + clickedElement.absoluteHeight();
            if (dropdownH > 0)
            {
                //Prefer downwards
                y = clickedBottom + h <= 1 ? clickedBottom : clickedElement.absoluteY() - h;
            }
            else
            {
                //Prefer upwards
                y = clickedElement.absoluteY() - h >= 0 ? clickedElement.absoluteY() - h : clickedBottom;
            }
            scrollbar = options.length > Math.abs(dropdownW * dropdownH) + 1 || (options.length > Math.abs(dropdownW * dropdownH) && Tools.contains(options, clickedElement.getItemStack()));
        }
        GUIScrollView view = new GUIScrollView(this, x, y, w, h);
        root.add(view);
        if (scrollbar) root.add(new GUIVerticalScrollbar(this, view.x + view.width, view.y, clickedElement.absoluteWidth() / 4, view.height, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, view));


        //Add options
        if (Tools.contains(options, clickedElement.getItemStack()))
        {
            GUIItemStack guiItemStack = new GUIItemStack(this, clickedElement.absoluteX(), clickedElement.absoluteY(), 16, 16, clickedElement.getItemStack());
            guiItemStack.add(new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.PURPLE), Color.BLANK, getHoverColor(Color.PURPLE), Color.BLANK, Color.PURPLE, Color.BLANK));
            root.add(guiItemStack.addClickActions(this::close));
        }
        for (ItemStack option : options)
        {
            if (option.equals(clickedElement.getItemStack())) continue;
            GUIItemStack guiItemStack = new GUIItemStack(this, 16, 16, option);
            guiItemStack.add(new GUIGradientBorder(this, 1, 1, 0.1, getIdleColor(Color.WHITE), Color.BLANK, getHoverColor(Color.WHITE), Color.BLANK, Color.WHITE, Color.BLANK));
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
