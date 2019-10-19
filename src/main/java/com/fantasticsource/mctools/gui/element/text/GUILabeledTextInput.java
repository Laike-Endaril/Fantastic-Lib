package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;

public class GUILabeledTextInput extends GUIAutocroppedView
{
    public final GUIText label;
    public final GUITextInput input;

    public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter)
    {
        this(screen, label, defaultInput, filter, 1);
    }

    public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter, double scale)
    {
        super(screen, scale);

        input = new GUITextInput(screen, defaultInput, filter);

        this.label = new GUIText(screen, label);
        add(this.label.addClickActions(() ->
        {
            int length = input.text.length();
            input.cursorPosition = length;
            input.selectorPosition = length == 0 ? -1 : 0;

            input.setActive(true);
        }));

        add(input);
    }


    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter)
    {
        this(screen, x, y, label, defaultInput, filter, 1);
    }

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter, double scale)
    {
        super(screen, x, y, scale);

        input = new GUITextInput(screen, defaultInput, filter);

        this.label = new GUIText(screen, label);
        add(this.label.addClickActions(() ->
        {
            int length = input.text.length();
            input.cursorPosition = length;
            input.selectorPosition = length == 0 ? -1 : 0;

            input.setActive(true);
        }));

        add(input);
    }


    @Override
    public void recalcAndRepositionSubElements(int startIndex)
    {
        super.recalcAndRepositionSubElements(startIndex);

        if (label != null && input != null)
        {
            label.recalc();
            input.x = label.width;
            input.y = 0;
        }
    }

    @Override
    public String toString()
    {
        return input.toString();
    }
}
