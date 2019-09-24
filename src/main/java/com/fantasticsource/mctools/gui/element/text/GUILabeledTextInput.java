package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIView;

public class GUILabeledTextInput extends GUIView
{
    public final GUIText label;
    public final GUITextInput input;

    public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter)
    {
        super(screen, 1, new GUIText(screen, "").height);

        this.label = new GUIText(screen, label);
        add(this.label);

        input = new GUITextInput(screen, this.label.width, 0, defaultInput, filter);
        add(input);
    }

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter)
    {
        super(screen, x, y, 1 - x, new GUIText(screen, "").height);

        this.label = new GUIText(screen, label);
        add(this.label);

        input = new GUITextInput(screen, this.label.width, 0, defaultInput, filter);
        add(input);
    }

    @Override
    public String toString()
    {
        return input.toString();
    }
}
