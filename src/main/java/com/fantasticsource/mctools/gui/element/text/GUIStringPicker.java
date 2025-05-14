package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.screen.TextSelectionGUI;
import com.fantasticsource.tools.datastructures.Color;

public class GUIStringPicker extends GUITextButton
{
    public String label;
    public String[] possibleValues;
    public String value = "";

    public GUIStringPicker(GUIScreen screen, String text, String... possibleValues)
    {
        this(screen, text, 1, possibleValues);
    }

    public GUIStringPicker(GUIScreen screen, String text, double scale, String... possibleValues)
    {
        super(screen, text, Color.AQUA, scale);
        setColor(Color.AQUA);
        this.label = text;
        this.possibleValues = possibleValues;
        if (possibleValues.length > 0) value = possibleValues[0];
        set(value);
    }

    public GUIStringPicker(GUIScreen screen, double x, double y, String text, String... possibleValues)
    {
        this(screen, x, y, text, 1, possibleValues);
    }

    public GUIStringPicker(GUIScreen screen, double x, double y, String text, double scale, String... possibleValues)
    {
        super(screen, x, y, text, Color.AQUA, scale);
        setColor(Color.AQUA);
        this.label = text;
        this.possibleValues = possibleValues;
        if (possibleValues.length > 0) value = possibleValues[0];
        set(value);
    }

    public GUIStringPicker set(String value)
    {
        this.value = value;
        this.internalText.setText(this.label + ": " + value);
        runEditActions();
        return this;
    }

    public void click()
    {
        GUIText dummy = new GUIText(screen, value);
        TextSelectionGUI selectionGUI = new TextSelectionGUI(dummy, label, possibleValues);
        selectionGUI.addOnClosedActions(() -> set(dummy.getText()));
        super.click();
    }
}
