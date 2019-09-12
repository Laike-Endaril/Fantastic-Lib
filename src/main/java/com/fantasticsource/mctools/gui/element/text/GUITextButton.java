package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.mctools.gui.GUIScreen.getColor;
import static com.fantasticsource.mctools.gui.GUIScreen.getHover;
import static com.fantasticsource.tools.datastructures.Color.WHITE;

public class GUITextButton extends GUIGradientBorder
{
    public static final double DEFAULT_PADDING = 0.5;

    private GUIText text;
    private double padding;

    public GUITextButton(GUIScreen screen, String text)
    {
        this(screen, text, WHITE);
    }

    public GUITextButton(GUIScreen screen, String text, Color color)
    {
        this(screen, text, color, getColor(color).setAF(color.af() * 0.4f));
    }

    public GUITextButton(GUIScreen screen, String text, Color border, Color center)
    {
        this(screen, text, DEFAULT_PADDING, border, center);
    }

    public GUITextButton(GUIScreen screen, String text, double padding, Color border, Color center)
    {
        this(screen, text, padding, getColor(border), getColor(center), getHover(border), getHover(center), border, center);
    }

    public GUITextButton(GUIScreen screen, String text, double padding, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, 0, 0, 0, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

        this.padding = padding;

        this.text = new GUIText(screen, 0, 0, text, border, hoverBorder, activeBorder);
        add(this.text);
        linkMouseActivity(this.text);

        recalc();
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text)
    {
        this(screen, x, y, text, WHITE);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, color, getColor(color).setAF(color.af() * 0.4f));
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center)
    {
        this(screen, x, y, text, DEFAULT_PADDING, border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, Color border, Color center)
    {
        this(screen, x, y, text, padding, getColor(border), getColor(center), getHover(border), getHover(center), border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, x, y, 0, 0, 0, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

        this.padding = padding;

        this.text = new GUIText(screen, 0, 0, text, border, hoverBorder, activeBorder);
        add(this.text);
        linkMouseActivity(this.text);

        recalc();
    }

    @Override
    public GUIElement recalc()
    {
        width = 1;
        super.recalc();

        double scaledPadding = text.height * padding;

        width = text.width + scaledPadding * 2;
        height = text.height + scaledPadding * 2;

        thickness = scaledPadding / height;

        text.x = scaledPadding / width;
        text.y = scaledPadding / height;

        return this;
    }

    @Override
    public double getScreenWidth()
    {
        return width;
    }

    @Override
    public double getScreenHeight()
    {
        return height;
    }
}
