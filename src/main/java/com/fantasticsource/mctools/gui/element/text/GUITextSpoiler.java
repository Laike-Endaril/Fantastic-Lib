package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

public class GUITextSpoiler extends GUIText
{
    private boolean hidden = true;

    private ArrayList<GUIElement> hideableElements = new ArrayList<>();

    public GUITextSpoiler(GUIScreen screen, String text, GUIElement... hideableElements)
    {
        this(screen, text, Color.WHITE, 1, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, double scale, GUIElement... hideableElements)
    {
        this(screen, text, Color.WHITE, scale, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, Color color, GUIElement... hideableElements)
    {
        this(screen, text, color, color, color, 1, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, Color color, double scale, GUIElement... hideableElements)
    {
        this(screen, text, color, color, color, scale, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, GUIElement... hideableElements)
    {
        this(screen, text, color, hoverColor, activeColor, 1, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, double scale, GUIElement... hideableElements)
    {
        super(screen, text.charAt(text.length() - 1) == '\n' ? text : text + '\n', color, hoverColor, activeColor, scale);
        for (GUIElement element : hideableElements) add(element);
    }


    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, GUIElement... hideableElements)
    {
        this(screen, x, y, text, Color.WHITE, 1, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, double scale, GUIElement... hideableElements)
    {
        this(screen, x, y, text, Color.WHITE, scale, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, GUIElement... hideableElements)
    {
        this(screen, x, y, text, color, color, color, 1, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, double scale, GUIElement... hideableElements)
    {
        this(screen, x, y, text, color, color, color, scale, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, GUIElement... hideableElements)
    {
        this(screen, x, y, text, color, hoverColor, activeColor, 1, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, double scale, GUIElement... hideableElements)
    {
        super(screen, x, y, text.charAt(text.length() - 1) == '\n' ? text : text + '\n', color, hoverColor, activeColor, scale);
        for (GUIElement element : hideableElements) add(element);
    }


    @Override
    public GUITextSpoiler add(GUIElement hideableElement)
    {
        if (hideableElements == null) return null;

        boolean reshow = !hidden;
        if (reshow) hide();
        this.hideableElements.add(hideableElement);
        if (reshow) show();
        return this;
    }

    @Override
    public GUIElement add(int index, GUIElement hideableElement)
    {
        if (hideableElements == null) return null;

        boolean reshow = !hidden;
        if (reshow) hide();
        this.hideableElements.add(index, hideableElement);
        if (reshow) show();
        return this;
    }

    @Override
    public void remove(GUIElement element)
    {
        if (hideableElements == null) return;
        hideableElements.remove(element);
    }

    @Override
    public void remove(int index)
    {
        if (hideableElements == null) return;
        hideableElements.remove(index);
    }

    @Override
    public int indexOf(GUIElement hideableElement)
    {
        if (hideableElements == null) return -1;
        return hideableElements.indexOf(hideableElement);
    }

    @Override
    public void clear()
    {
        if (hideableElements == null) return;
        hideableElements.clear();
    }

    @Override
    public int size()
    {
        if (hideableElements == null) return 0;
        return hideableElements.size();
    }

    @Override
    public GUIElement get(int index)
    {
        if (hideableElements == null) return null;
        return hideableElements.get(index);
    }


    public void show()
    {
        if (!hidden) return;

        hidden = false;

        int index = parent.indexOf(this);
        for (GUIElement element : hideableElements) parent.add(++index, element);
    }

    public void hide()
    {
        if (hidden) return;

        hidden = true;

        for (GUIElement element : hideableElements) parent.remove(element);
    }

    public void toggle()
    {
        if (hidden) show();
        else hide();
    }

    @Override
    public void click()
    {
        toggle();
        super.click();
    }
}
