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
        this(screen, text, WHITE);
    }

    public GUITextButton(GUIScreen screen, String text, Color color)
    {
        this(screen, text, color, getIdleColor(color).setAF(color.af() * 0.4f));
    }

    public GUITextButton(GUIScreen screen, String text, Color border, Color center)
    {
        super(screen, DEFAULT_PADDING, new GUIGradientBorder(screen, 1, 1, DEFAULT_PADDING / (1 + DEFAULT_PADDING) / 2, getIdleColor(border), getIdleColor(center), getHoverColor(border), getHoverColor(center), border, center));

        this.text = new GUIText(screen, text, getIdleColor(border), getHoverColor(border), border);
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
        super(screen, x, y, DEFAULT_PADDING, new GUIGradientBorder(screen, 1, 1, DEFAULT_PADDING / (1 + DEFAULT_PADDING) / 2, getIdleColor(border), getIdleColor(center), getHoverColor(border), getHoverColor(center), border, center));

        this.text = new GUIText(screen, text, getIdleColor(border), getHoverColor(border), border);
        add(this.text);
        linkMouseActivity(this.text);

        recalc();
    }

    @Override
    public String toString()
    {
        return text.text;
    }
}
