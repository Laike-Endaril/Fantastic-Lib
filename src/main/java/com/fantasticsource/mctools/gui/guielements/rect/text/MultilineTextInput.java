package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;

public class MultilineTextInput extends GUIRectScrollView
{
    public final double lineSpacing;
    public Color color, hoverColor, activeColor, cursorColor, highlightColor;

    public MultilineTextInput(GUIScreen screen, double x, double y, double width, double height, double lineSpacing, GUIRectElement... subElements)
    {
        super(screen, x, y, width, height, subElements);

        this.lineSpacing = lineSpacing;
        add(new GUITextInputRect(screen, 0, 0, "", color, hoverColor, activeColor, cursorColor, highlightColor));
    }

    @Override
    public void add(GUIElement element)
    {
        if (!(element instanceof GUITextInputRect)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        super.add(element);
    }

    @Override
    public void add(int index, GUIElement element)
    {
        if (!(element instanceof GUITextInputRect)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        super.add(index, element);
    }

    public void add(String s)
    {
        add(new GUITextInputRect(screen, 0, 0, s, color, hoverColor, activeColor, cursorColor, highlightColor));
    }

    public void add(int index, String s)
    {
        add(index, new GUITextInputRect(screen, 0, 0, s, color, hoverColor, activeColor, cursorColor, highlightColor));
    }
}
