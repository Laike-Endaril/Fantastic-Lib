package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.datastructures.Color;

public class GUILabeledBoolean extends GUIAutocroppedView
{
    public final GUIText label;
    public final GUIText input;

    public GUILabeledBoolean(GUIScreen screen, String label, boolean defaultInput)
    {
        this(screen, label, defaultInput, 1);
    }

    public GUILabeledBoolean(GUIScreen screen, String label, boolean defaultInput, double scale)
    {
        super(screen);

        input = new GUIText(screen, "" + defaultInput, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, scale);

        this.label = new GUIText(screen, label, scale);
        add(this.label.addClickActions(() -> input.setText(input.getText().equals("true") ? "false" : "true")));
        add(input.addClickActions(() -> input.setText(input.getText().equals("true") ? "false" : "true")));
    }


    public GUILabeledBoolean(GUIScreen screen, double x, double y, String label, boolean defaultInput)
    {
        this(screen, x, y, label, defaultInput, 1);
    }

    public GUILabeledBoolean(GUIScreen screen, double x, double y, String label, boolean defaultInput, double scale)
    {
        super(screen, x, y);

        input = new GUIText(screen, "" + defaultInput, GUIScreen.getIdleColor(Color.WHITE), GUIScreen.getHoverColor(Color.WHITE), Color.WHITE, scale);

        this.label = new GUIText(screen, label, scale);
        add(this.label.addClickActions(() -> input.setText(input.getText().equals("true") ? "false" : "true")));
        add(input.addClickActions(() -> input.setText(input.getText().equals("true") ? "false" : "true")));
    }


    public GUILabeledBoolean setInput(boolean value)
    {
        input.setText("" + value);
        return this;
    }


    @Override
    public void recalcAndRepositionSubElements(int startIndex)
    {
        super.recalcAndRepositionSubElements(startIndex);

        if (label != null && input != null)
        {
            label.recalc(0);
            input.x = label.width;
            input.y = 0;
        }
    }

    @Override
    public String toString()
    {
        return input.toString();
    }


    public boolean getValue()
    {
        return Boolean.parseBoolean(input.getText());
    }

    public void setValue(boolean value)
    {
        input.setText("" + value);
    }

    @Override
    public GUILabeledBoolean addEditActions(Runnable... actions)
    {
        input.addEditActions(actions);

        return this;
    }
}
