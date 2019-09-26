package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.mctools.gui.GUIScreen.getHoverColor;
import static com.fantasticsource.mctools.gui.GUIScreen.getIdleColor;
import static com.fantasticsource.tools.datastructures.Color.WHITE;

public class GUITextButton extends GUIGradientBorder
{
    public static final double DEFAULT_PADDING = 0.5;

    private GUIText text;
    private double padding;
    private boolean autoThickness = true;

    public GUITextButton(GUIScreen screen, String text)
    {
        this(screen, text, WHITE);
    }

    public GUITextButton(GUIScreen screen, String text, Color color)
    {
        this(screen, text, color, getIdleColor(color).setAF(color.af() * 0.4f));
    }

    public GUITextButton(GUIScreen screen, String text, Color border, Color center)
    {
        this(screen, text, DEFAULT_PADDING, border, center);
    }

    public GUITextButton(GUIScreen screen, String text, double padding, Color border, Color center)
    {
        this(screen, text, padding, getIdleColor(border), getIdleColor(center), getHoverColor(border), getHoverColor(center), border, center);
    }

    public GUITextButton(GUIScreen screen, String text, double padding, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, 1, 1, 0, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

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
        this(screen, x, y, text, color, getIdleColor(color).setAF(color.af() * 0.4f));
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center)
    {
        this(screen, x, y, text, DEFAULT_PADDING, border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, Color border, Color center)
    {
        this(screen, x, y, text, padding, getIdleColor(border), getIdleColor(center), getHoverColor(border), getHoverColor(center), border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, x, y, 1, 1, 0, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

        this.padding = padding;

        this.text = new GUIText(screen, 0, 0, text, border, hoverBorder, activeBorder);
        add(this.text);
        linkMouseActivity(this.text);

        recalc();
    }

    @Override
    public GUITextButton setThickness(double thickness)
    {
        autoThickness = false;
        this.thickness = thickness;
        return this;
    }

    @Override
    public GUITextButton recalc()
    {
        width = 1;
        height = 1;
        super.recalc(0);

        double vPadding = text.height / Tools.max(1, text.lines.size()) * padding;

        double absH = text.absoluteHeight();

        height = text.height + vPadding * 2;

        width = (text.absoluteWidth() + (absoluteHeight() - absH) * screen.height / screen.width) / absoluteWidth();

        double hPadding = (width - text.width) / 2;

        if (autoThickness) thickness = vPadding / height;

        text.x = hPadding / width;
        text.y = vPadding / height;

        return this;
    }

    @Override
    public GUIElement recalc(int subIndexChanged)
    {
        return recalc();
    }

    @Override
    public String toString()
    {
        return text.text;
    }
}
