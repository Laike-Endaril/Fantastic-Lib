package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;

public class GUITextButton extends GUIGradientBorder
{
    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, double borderThickness, Color border, Color center)
    {
        this(screen, x, y, text, padding, borderThickness, border, center, border, center, border, center);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        this(screen, x, y, text, 0.01, borderThickness, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);
    }

    public GUITextButton(GUIScreen screen, double x, double y, String text, double padding, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, x, y, (double) GUITextRect.fontRenderer.getStringWidth(text) / screen.width + padding * 2, (double) GUITextRect.fontRenderer.FONT_HEIGHT / screen.height + padding * 2, borderThickness, border, center, hoverBorder, hoverCenter, activeBorder, activeCenter);

        GUITextRect textRect = new GUITextRect(screen, padding, padding, text, border, hoverBorder, activeBorder);
        children.add(textRect);
        linkMouseActivity(textRect);
    }
}
