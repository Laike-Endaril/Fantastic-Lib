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
        boolean reshow = !hidden;
        if (reshow) hide();
        this.hideableElements.add(hideableElement);
        if (reshow) show();
        return this;
    }

    @Override
    public GUIElement add(int index, GUIElement hideableElement)
    {
        boolean reshow = !hidden;
        if (reshow) hide();
        this.hideableElements.add(index, hideableElement);
        if (reshow) show();
        return this;
    }


    public void show()
    {
        if (!hidden) return;

        if (parent == null)
        {
            screen.guiElements.addAll(screen.guiElements.indexOf(this) + 1, hideableElements);
        }
        else
        {
            parent.children.addAll(parent.indexOf(this) + 1, hideableElements);
        }
    }

    public void hide()
    {
        if (hidden) return;

        if (parent == null)
        {
            for (GUIElement element : screen.guiElements.toArray(new GUIElement[0]))
            {
                if (hideableElements.contains(element)) screen.guiElements.remove(element);
            }
        }
        else
        {
            for (GUIElement element : parent.children.toArray(new GUIElement[0]))
            {
                if (hideableElements.contains(element)) parent.children.remove(element);
            }
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
