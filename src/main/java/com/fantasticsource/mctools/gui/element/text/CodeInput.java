package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class CodeInput extends GUIScrollView
{
    public final double scale;
    public Color color, hoverColor, activeColor, highlightColor;
    protected int cursorX, selectionStartY = -1;


    public CodeInput(GUIScreen screen, double width, double height, String... lines)
    {
        this(screen, width, height, 1, lines);
    }

    public CodeInput(GUIScreen screen, double width, double height, double scale, String... lines)
    {
        this(screen, width, height, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, Color.WHITE.copy().setAF(0.4f), scale, lines);
    }

    public CodeInput(GUIScreen screen, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String... lines)
    {
        this(screen, width, height, color, hoverColor, activeColor, hightlightColor, 1, lines);
    }

    public CodeInput(GUIScreen screen, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, double scale, String... lines)
    {
        super(screen, width, height);
        this.scale = scale;

        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.highlightColor = hightlightColor;

        if (lines.length == 0) add("");
        else for (String line : lines) add(line);

        cursorX = ((GUITextInput) children.get(0)).text.length();
    }


    public CodeInput(GUIScreen screen, double x, double y, double width, double height, String... lines)
    {
        this(screen, x, y, width, height, 1, lines);
    }

    public CodeInput(GUIScreen screen, double x, double y, double width, double height, double scale, String... lines)
    {
        this(screen, x, y, width, height, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, Color.WHITE.copy().setAF(0.4f), scale, lines);
    }

    public CodeInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String... lines)
    {
        this(screen, x, y, width, height, color, hoverColor, activeColor, hightlightColor, 1, lines);
    }

    public CodeInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, double scale, String... lines)
    {
        super(screen, x, y, width, height);
        this.scale = scale;

        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.highlightColor = hightlightColor;

        if (lines.length == 0) add("");
        else for (String line : lines) add(line);

        cursorX = ((GUITextInput) children.get(0)).text.length();
    }

    @Override
    public GUIElement recalc(int subIndexChanged)
    {
        internalHeight = 0;
        GUIElement prev = null;
        for (GUIElement element : children)
        {
            element.recalc();
            if (prev == null) element.y = 0;
            else element.y = prev.y + prev.height;
            prev = element;
            internalHeight = Tools.max(internalHeight, element.y + element.height);
        }

        recalc2();

        postRecalc();

        return this;
    }

    @Override
    public GUIElement add(GUIElement element)
    {
        if (!(element instanceof GUITextInput)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        return add(((GUITextInput) element).text);
    }

    @Override
    public GUIElement add(int index, GUIElement element)
    {
        if (!(element instanceof GUITextInput)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        return add(index, ((GUITextInput) element).text);
    }

    public GUIElement add(String s)
    {
        if (children.size() == 0) return super.add(new GUITextInput(screen, 0, 0, s, FilterNone.INSTANCE, activeColor, scale));
        else
        {
            GUIElement element = children.get(children.size() - 1);
            return super.add(new GUITextInput(screen, 0, element.y + (element.height + (1d / screen.height)) / height, s, FilterNone.INSTANCE, activeColor, scale));
        }
    }

    public GUIElement add(int index, String s)
    {
        if (index == 0) return add(s);
        else
        {
            GUIElement element = children.get(index - 1), newElement = new GUITextInput(screen, 0, element.y + (element.height + (1d / screen.height)) / height, s, FilterNone.INSTANCE, activeColor, scale);
            for (int i = index; i < children.size(); i++)
            {
                element = children.get(i);
                element.y += ((1d / screen.height) + newElement.height) / height;
            }
            return super.add(index, newElement);
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        for (GUIElement element : children)
        {
            if (element.isActive())
            {
                element.keyTyped(typedChar, keyCode);
                break;
            }
        }
    }
}
