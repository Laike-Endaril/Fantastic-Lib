package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.screen.ColorSelectionGUI;
import com.fantasticsource.tools.datastructures.Color;

public class GUIColor extends GUIText
{
    private Color value;

    public GUIColor(GUIScreen screen)
    {
        this(screen, Color.WHITE, 1);
    }

    public GUIColor(GUIScreen screen, Color value)
    {
        this(screen, value, 1);
    }

    public GUIColor(GUIScreen screen, Color value, double scale)
    {
        super(screen, value == null ? "00000000" : value.hex8(), scale);
        this.value = value == null ? new Color(0) : value.copy();

        color = GUIScreen.getIdleColor(this.value).setA(255);
        hoverColor = GUIScreen.getHoverColor(this.value).setA(255);
        activeColor = this.value.copy().setA(255);
    }


    public GUIColor(GUIScreen screen, double x, double y)
    {
        this(screen, x, y, Color.WHITE, 1);
    }

    public GUIColor(GUIScreen screen, double x, double y, Color value)
    {
        this(screen, x, y, value, 1);
    }

    public GUIColor(GUIScreen screen, double x, double y, Color value, double scale)
    {
        super(screen, x, y, value == null ? "00000000" : value.hex8(), scale);
        this.value = value == null ? new Color(0) : value.copy();

        color = GUIScreen.getIdleColor(this.value).setA(255);
        hoverColor = GUIScreen.getHoverColor(this.value).setA(255);
        activeColor = this.value.copy().setA(255);
    }


    public Color getValue()
    {
        return value.copy();
    }

    public GUIColor setValue(Color value)
    {
        this.value = value == null ? new Color(0) : value;
        text = this.value.hex8();

        color = GUIScreen.getIdleColor(this.value).setA(255);
        hoverColor = GUIScreen.getHoverColor(this.value).setA(255);
        activeColor = this.value.copy().setA(255);

        return this;
    }

    @Override
    public void click()
    {
        new ColorSelectionGUI(this, screen.textScale);
        super.click();
    }
}
