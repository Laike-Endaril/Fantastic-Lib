package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUITextButton extends GUIGradientBorder
{
    public static final double DEFAULT_PADDING = 0.01;
    private static final Color WHITE = new Color(0xFFFFFFFF);

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
        this(screen, x, y, text, padding, padding, border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, double borderThickness, Color border, Color center)
    {
        this(screen, x, y, text, padding, borderThickness, getColor(border), getColor(center), getHover(border), getHover(center), border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        this(screen, x, y, text, padding, padding, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, x, y, (double) (FONT_RENDERER.getStringWidth(text) - 1) / screen.width + padding * 2, (double) (FONT_RENDERER.FONT_HEIGHT - 1) / screen.height + padding * 2, borderThickness, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

        this.padding = padding;

        GUITextRect textRect = new GUITextRect(screen, padding, padding, text, border, hoverBorder, activeBorder);
        add(textRect);
        linkMouseActivity(textRect);
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
    public void recalc()
    {
        super.recalc();

        GUIElement textRect = children.get(0);
        width = textRect.width + padding * 2;
        height = textRect.height + padding * 2;
    }
}
