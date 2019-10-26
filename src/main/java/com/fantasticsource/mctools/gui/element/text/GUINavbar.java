package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUINavbar extends GUITextButton
{
    public GUINavbar(GUIScreen screen)
    {
        this(screen, 1);
    }

    public GUINavbar(GUIScreen screen, double scale)
    {
        this(screen, Color.WHITE, scale);
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
        super(screen, genText(screen), border, center, scale);
        setSubElementAutoplaceMethod(AP_CENTERED_H_TOP_TO_BOTTOM);
        width = 1;

        GUIGradientBorder back = (GUIGradientBorder) background;
        back.border = back.activeBorder;
        back.hoverBorder = back.activeBorder;

        GUIText fore = (GUIText) children.get(1);
        fore.setColor(back.border);
    }

    private static String genText(GUIScreen screen)
    {
        StringBuilder result = new StringBuilder();
        for (GUIScreen.ScreenEntry entry : GUIScreen.SCREEN_STACK)
        {
            if (result.toString().equals("")) result = new StringBuilder(entry.screen.title());
            else result.append(" > ").append(entry.screen.title());
        }

        if (result.toString().equals("")) return screen.title();
        return result.append(" > ").append(screen.title()).toString();
    }

    @Override
    public GUIAutocroppedView recalc(int subIndexChanged)
    {
        if (children.size() > 1)
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
            }
        }

        postRecalc();

        return this;
    }
}
