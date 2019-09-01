package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.datastructures.Color;

public class MultilineTextInput extends GUIRectScrollView
{
    public final double lineSpacing;
    public Color color, hoverColor, activeColor, cursorColor, highlightColor;

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

        this.lineSpacing = (double) GUIScreen.FONT_RENDERER.FONT_HEIGHT / screen.height;
        if (lines.length == 0) add("");
        else for (String line : lines) add(line);
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
        if (children.size() == 0) add(new GUITextInputRect(screen, lineSpacing / 4, lineSpacing / 2, s, color, hoverColor, activeColor, cursorColor, highlightColor));
        else add(new GUITextInputRect(screen, lineSpacing / 4, children.get(children.size() - 1).y + lineSpacing, s, color, hoverColor, activeColor, cursorColor, highlightColor));
    }

    public void add(int index, String s)
    {
        add(index, new GUITextInputRect(screen, lineSpacing / 4, children.get(children.size() - 1).y + lineSpacing, s, color, hoverColor, activeColor, cursorColor, highlightColor));
    }
}
