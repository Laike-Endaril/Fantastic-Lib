package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;

public class GUIBooleanToggle extends GUITextButton
{
    public String label;
    public boolean value = true;

    public GUIBooleanToggle(GUIScreen screen, String text)
    {
        this(screen, text, 1);
    }

    public GUIBooleanToggle(GUIScreen screen, String text, double scale)
    {
        super(screen, text, Color.GREEN, scale);
        label = text;
    }


    public GUIBooleanToggle(GUIScreen screen, double x, double y, String text)
    {
        this(screen, x, y, text, 1);
    }

    public GUIBooleanToggle(GUIScreen screen, double x, double y, String text, double scale)
    {
        super(screen, x, y, text, Color.GREEN, scale);
        label = text;
    }


    public GUIBooleanToggle set(boolean value)
    {
        this.value = value;
        if (value) setColor(Color.GREEN);
        else setColor(Color.RED);
        internalText.setText(label + ": " + Boolean.toString(value).toUpperCase());
        return this;
    }


    @Override
    public void click()
    {
        set(!value);
        super.click();
    }
}
