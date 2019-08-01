package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;

public class GUITextButton extends GUIGradientBorder
{
    public GUITextButton(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, color, color.copy().setColor(color.r(), color.g(), color.b(), 0));
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, Color border, Color center)
    {
        this(screen, x, y, text, 0.01, border, center);
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
        super(screen, x, y, (double) GUITextRect.fontRenderer.getStringWidth(text) / screen.width + padding * 2, (double) GUITextRect.fontRenderer.FONT_HEIGHT / screen.height + padding * 2, borderThickness, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

        GUITextRect textRect = new GUITextRect(screen, padding, padding, text, border, hoverBorder, activeBorder);
        children.add(textRect);
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
}
