package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.mctools.gui.GUIScreen.getHoverColor;
import static com.fantasticsource.mctools.gui.GUIScreen.getIdleColor;
import static com.fantasticsource.tools.datastructures.Color.WHITE;

public class GUITextButton extends GUIAutocroppedView
{
    public static final double DEFAULT_PADDING = 0.5;

    private GUIText text;

    public GUITextButton(GUIScreen screen, String text)
    {
        this(screen, text, WHITE, 1);
    }

    public GUITextButton(GUIScreen screen, String text, double scale)
    {
        this(screen, text, WHITE, scale);
    }

    public GUITextButton(GUIScreen screen, String text, Color color)
    {
        this(screen, text, color, getIdleColor(color).setAF(color.af() * 0.4f), 1);
    }

    public GUITextButton(GUIScreen screen, String text, Color color, double scale)
    {
        this(screen, text, color, getIdleColor(color).setAF(color.af() * 0.4f), scale);
    }

    public GUITextButton(GUIScreen screen, String text, Color border, Color center)
    {
        this(screen, text, border, center, 1);
    }

    public GUITextButton(GUIScreen screen, String text, Color border, Color center, double scale)
    {
        super(screen, DEFAULT_PADDING, new GUIGradientBorder(screen, 1, 1, DEFAULT_PADDING / (1 + DEFAULT_PADDING) / 2, getIdleColor(border), getIdleColor(center), getHoverColor(border), getHoverColor(center), border, center));

        this.text = new GUIText(screen, text, getIdleColor(border), getHoverColor(border), border, scale);
        add(this.text);
        linkMouseActivity(this.text);
        linkMouseActivity(this.background);

        recalc();
    }


    public GUITextButton(GUIScreen screen, double x, double y, String text)
    {
        this(screen, x, y, text, WHITE, 1);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double scale)
    {
        this(screen, x, y, text, WHITE, scale);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, color, getIdleColor(color).setAF(color.af() * 0.4f), 1);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color color, double scale)
    {
        this(screen, x, y, text, color, getIdleColor(color).setAF(color.af() * 0.4f), scale);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center)
    {
        this(screen, x, y, text, border, center, 1);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center, double scale)
    {
        super(screen, x, y, DEFAULT_PADDING, new GUIGradientBorder(screen, 1, 1, DEFAULT_PADDING / (1 + DEFAULT_PADDING) / 2, getIdleColor(border), getIdleColor(center), getHoverColor(border), getHoverColor(center), border, center));

        this.text = new GUIText(screen, text, getIdleColor(border), getHoverColor(border), border, scale);
        add(this.text);
        linkMouseActivity(this.text);
        linkMouseActivity(this.background);

        recalc();
    }

    @Override
    public String toString()
    {
        return text.text;
    }
}
