package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.text.filter.TextFilter;
import com.fantasticsource.tools.datastructures.Color;

public class GUILabeledTextInput extends GUIRectElement
{
    private GUITextRect label;
    public GUITextInputRect input;

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String labelText, String defaultText, TextFilter filter)
    {
        super(screen, x, y, 1, 1);

        label = new GUITextRect(screen, 0, y, labelText, Color.WHITE);
        add(label);

        input = new GUITextInputRect(screen, label.width, 0, defaultText, filter);
        add(input);

        recalc();
    }
}
