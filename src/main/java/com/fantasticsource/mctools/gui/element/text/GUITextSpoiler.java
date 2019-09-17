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
        this(screen, text, Color.WHITE, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, Color color, GUIElement... hideableElements)
    {
        this(screen, text, color, color, color, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, GUIElement... hideableElements)
    {
        super(screen, text.charAt(text.length() - 1) == '\n' ? text : text + '\n', color, hoverColor, activeColor);
        for (GUIElement element : hideableElements) add(element);
    }


    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, GUIElement... hideableElements)
    {
        this(screen, x, y, text, Color.WHITE, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, GUIElement... hideableElements)
    {
        this(screen, x, y, text, color, color, color, hideableElements);
    }

    public GUITextSpoiler(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, GUIElement... hideableElements)
    {
        super(screen, x, y, text.charAt(text.length() - 1) == '\n' ? text : text + '\n', color, hoverColor, activeColor);
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

        if (parent == null)
        {
            screen.guiElements.addAll(screen.guiElements.indexOf(this) + 1, hideableElements);
        }
        else
        {
            int index = parent.indexOf(this);
            for (GUIElement element : hideableElements) parent.add(++index, element);
        }
    }

    public void hide()
    {
        if (hidden) return;

        hidden = true;

        if (parent == null)
        {
            for (GUIElement element : screen.guiElements.toArray(new GUIElement[0]))
            {
                if (hideableElements.contains(element)) screen.guiElements.remove(element);
            }
        }
        else
        {
            for (GUIElement element : hideableElements) parent.remove(element);
        }
    }

    public void toggle()
    {
        if (hidden) show();
        else hide();
    }

    @Override
    public boolean mouseReleased(double x, double y, int button)
    {
        boolean result = super.mouseReleased(x, y, button);

        if (result) toggle();

        return result;
    }
}
