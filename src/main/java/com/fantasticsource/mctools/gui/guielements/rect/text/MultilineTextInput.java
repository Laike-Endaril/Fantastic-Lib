package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class MultilineTextInput extends GUIRectScrollView
{
    public final double lineSpacing, margin;
    public Color color, hoverColor, activeColor, cursorColor, highlightColor;
    protected int cursorX = 0, selectionStartY = -1;

    public MultilineTextInput(GUIScreen screen, double x, double y, double width, double height, String... lines)
    {
        this(screen, x, y, width, height, GUIScreen.getColor(Color.GRAY), GUIScreen.getHover(Color.GRAY), Color.GRAY, Color.WHITE, Color.WHITE.copy().setAF(0.4f), lines);
    }

    public MultilineTextInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color cursorColor, Color hightlightColor, String... lines)
    {
        super(screen, x, y, width, height);

        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        this.cursorColor = cursorColor;
        this.highlightColor = hightlightColor;

        lineSpacing = 0;
        margin = (double) FONT_RENDERER.FONT_HEIGHT / screen.height / 4;
        if (lines.length == 0) add("");
        else for (String line : lines) add(line);

        cursorX = ((GUITextInputRect) children.get(0)).text.length();
    }

    @Override
    public GUIElement recalc()
    {
        return super.recalc();
    }

    @Override
    public GUIElement add(GUIElement element)
    {
        if (!(element instanceof GUITextInputRect)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        return add(((GUITextInputRect) element).text);
    }

    @Override
    public GUIElement add(int index, GUIElement element)
    {
        if (!(element instanceof GUITextInputRect)) throw new IllegalArgumentException("Multiline text inputs can only have text inputs added to them!");
        return add(index, ((GUITextInputRect) element).text);
    }

    public GUIElement add(String s)
    {
        if (children.size() == 0) return super.add(new GUITextInputRect(screen, margin, margin, s, color, hoverColor, activeColor, cursorColor, highlightColor));
        else
        {
            GUIElement element = children.get(children.size() - 1);
            return super.add(new GUITextInputRect(screen, margin, element.y + (element.height + lineSpacing + (1d / screen.height)) / height, s, color, hoverColor, activeColor, cursorColor, highlightColor));
        }
    }

    public GUIElement add(int index, String s)
    {
        if (index == 0) return add(s);
        else
        {
            GUIElement element = children.get(index - 1), newElement = new GUITextInputRect(screen, margin, element.y + (element.height + lineSpacing + (1d / screen.height)) / height, s, color, hoverColor, activeColor, cursorColor, highlightColor);
            for (int i = index; i < children.size(); i++)
            {
                element = children.get(i);
                element.y += lineSpacing + (1d / screen.height) + newElement.height;
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
