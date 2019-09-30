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
        super(screen);

        input = new GUITextInput(screen, 0, 0, defaultInput, filter);

        this.label = new GUIText(screen, label);
        add(this.label.addClickActions(() ->
        {
            input.cursorPosition = 0;
            input.selectorPosition = -1;
            input.setActive(true);
        }));

        add(input);
    }

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter)
    {
        super(screen, x, y);

        input = new GUITextInput(screen, 0, 0, defaultInput, filter);

        this.label = new GUIText(screen, label);
        add(this.label.addClickActions(() ->
        {
            input.cursorPosition = 0;
            input.selectorPosition = -1;
            input.setActive(true);
        }));

        add(input);
    }

    @Override
    public void recalcAndRepositionSubElements(int startIndex)
    {
        if (label != null && input != null)
        {
            label.recalc();
            input.x = label.width / absoluteWidth();
        }

        super.recalcAndRepositionSubElements(startIndex);
    }

    @Override
    public String toString()
    {
        return input.toString();
    }
}
