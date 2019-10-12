package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

public class GUIMultilineTextInput extends GUITextInput
{
    public ArrayList<String> lines = new ArrayList<>();

    public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter)
    {
        super(screen, text, filter);
    }

    public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor)
    {
        super(screen, text, filter, activeColor);
    }

    public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter)
    {
        super(screen, x, y, text, filter);
    }

    public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor)
    {
        super(screen, x, y, text, filter, activeColor);
    }
}
