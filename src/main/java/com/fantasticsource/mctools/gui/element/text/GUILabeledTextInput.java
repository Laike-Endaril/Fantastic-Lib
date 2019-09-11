package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.datastructures.Color;

public class GUILabeledTextInput extends GUIElement
{
    public GUITextInput input;
    private GUIText label;

    public GUILabeledTextInput(GUIScreen screen, String labelText, String defaultText, TextFilter filter)
    {
        super(screen, 1, 1);

        label = new GUIText(screen, 0, y, labelText, Color.WHITE);
        add(label);

        input = new GUITextInput(screen, label.width, 0, defaultText, filter);
        add(input);

        recalc();
    }

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String labelText, String defaultText, TextFilter filter)
    {
        super(screen, x, y, 1, 1);

        label = new GUIText(screen, 0, y, labelText, Color.WHITE);
        add(label);

        input = new GUITextInput(screen, label.width, 0, defaultText, filter);
        add(input);

        recalc();
    }
}
