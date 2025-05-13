package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUINavbar extends GUITextButton
{
    public int maxParentsDisplayed = Integer.MAX_VALUE;

    public GUINavbar(GUIScreen screen)
    {
        this(screen, 1);
    }

    public GUINavbar(GUIScreen screen, double scale)
    {
        this(screen, Color.AQUA, scale);
    }

    public GUINavbar(GUIScreen screen, Color color)
    {
        this(screen, color, 1);
    }

    public GUINavbar(GUIScreen screen, Color color, double scale)
    {
        this(screen, color, Color.BLANK, scale);
    }

    public GUINavbar(GUIScreen screen, Color border, Color center)
    {
        this(screen, border, center, 1);
    }

    public GUINavbar(GUIScreen screen, Color border, Color center, double scale)
    {
        super(screen, "", border, center, scale);
        setSubElementAutoplaceMethod(AP_CENTERED_H_TOP_TO_BOTTOM);
        width = 1;

        GUIGradientBorder back = (GUIGradientBorder) background;
        back.border = back.activeBorder;
        back.hoverBorder = back.activeBorder;

        GUIText fore = (GUIText) children.get(1);
        fore.setText(genText(screen));
        fore.setColor(back.border);
    }

    protected String genText(GUIScreen screen)
    {
        StringBuilder result = null;
        int i = Tools.min(GUIScreen.SCREEN_STACK.size(), maxParentsDisplayed) - GUIScreen.SCREEN_STACK.size() - 1;
        for (GUIScreen.ScreenEntry entry : GUIScreen.SCREEN_STACK)
        {
            if (i++ < 0) continue;

            if (result == null) result = new StringBuilder(entry.screen instanceof GUIScreen ? ((GUIScreen) entry.screen).title() : entry.screen.getClass().getSimpleName());
            else result.append(" > ").append(entry.screen instanceof GUIScreen ? ((GUIScreen) entry.screen).title() : entry.screen.getClass().getSimpleName());
        }

        if (result == null) return screen.title();
        return result.append(" > ").append(screen.title()).toString();
    }

    @Override
    public GUINavbar recalc(int subIndexChanged)
    {
        if (size() > 1)
        {
            GUIText fore = (GUIText) children.get(1);
            fore.text = genText(screen);
        }


        width = 1;
        height = 1;
        if (parent == null) return this;


        recalcAndRepositionSubElements(0);

        height = 0;
        for (GUIElement element : children)
        {
            if (element != background)
            {
                height = Tools.max(height, element.y + element.height);
            }
        }

        recalcAndRepositionSubElements(0);

        double paddingPx = Tools.min(absolutePxWidth(), absolutePxHeight()) * padding;
        double xPad = paddingPx / parent.absolutePxWidth(), yPad = paddingPx / parent.absolutePxHeight();

        height += yPad * 2;

        xPad = xPad / width;
        yPad = yPad / height;
        for (GUIElement element : children)
        {
            if (element != background)
            {
                element.x += (0.5 - element.x) * 2 * xPad;
                element.y += (0.5 - element.y) * 2 * yPad;
                element.recalc(0);
            }
        }

        postRecalc();

        return this;
    }

    @Override
    public void draw()
    {
        if (screen.isVisible()) super.draw();
    }
}
