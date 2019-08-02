package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.tools.datastructures.Color;

public class GUITextButton extends GUIGradientBorder
{
    public static final double DEFAULT_PADDING = 0.5;
    private static final Color WHITE = new Color(0xFFFFFFFF);

    private GUITextRect textRect;
    private double padding;

    public GUITextButton(GUIScreen screen, double x, double y, String text)
    {
        this(screen, x, y, text, WHITE);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, color, color.copy().setColor(color.r(), color.g(), color.b(), 0));
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

        textRect = new GUITextRect(screen, 0, 0, text, border, hoverBorder, activeBorder);
        add(textRect);
        linkMouseActivity(textRect);

        recalc();
    }

    private static Color getColor(Color active)
    {
        return new Color(active.r() >> 1, active.g() >> 1, active.b() >> 1, active.a());
    }

    private static Color getHover(Color active)
    {
        return new Color((int) (0.75 * active.r()), (int) (0.75 * active.g()), (int) (0.75 * active.b()), active.a());
    }

    @Override
    public GUIElement recalc()
    {
        super.recalc();

        double scaledPadding = textRect.height * padding;

        width = textRect.width + scaledPadding * 2;
        height = textRect.height + scaledPadding * 2;

        textRect.x = scaledPadding;
        textRect.y = scaledPadding;

        thickness = scaledPadding / height;

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
