package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;

public class MultilineTextInput extends GUIRectScrollView
{
    public MultilineTextInput(GUIScreen screen, double x, double y, double width, double height, GUIRectElement... subElements)
    {
        super(screen, x, y, width, height, subElements);
    }

    @Override
    public void add(GUIElement element)
    {
        if (!(element instanceof GUITextInputRect)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        super.add(element);
    }
}
