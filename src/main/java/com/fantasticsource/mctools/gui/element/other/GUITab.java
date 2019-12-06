package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;

public class GUITab extends GUIElement
{
    private double tabX, tabY;

    public GUITab(GUIScreen screen, double tabX, double tabY)
    {
        super(screen, 0, 0);

        this.tabX = tabX;
        this.tabY = tabY;
    }

    public GUITab(GUIScreen screen, double tabX, double tabY, byte subElementAutoplaceMethod)
    {
        super(screen, 0, 0, subElementAutoplaceMethod);

        this.tabX = tabX;
        this.tabY = tabY;
    }

    @Override
    public GUITab recalc(int subIndexChanged)
    {
        width = tabX - x;
        height = tabY - y;

        recalcAndRepositionSubElements(subIndexChanged);

        postRecalc();

        return this;
    }
}
