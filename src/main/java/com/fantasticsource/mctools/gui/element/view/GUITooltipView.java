package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.tools.datastructures.Color;

public class GUITooltipView extends GUIAutocroppedView
{
    private double offset = 0;

    public GUITooltipView(GUIScreen screen)
    {
        super(screen, 0, 0, 5, new GUIGradient(screen, 1, 1, Color.BLACK.copy().setAF(0.85f)));
    }

    @Override
    public void draw()
    {
        x = mouseX() + offset;
        y = mouseY() - height / 2;

        if (y + height > 1) y = 1 - height;
        if (y < 0) y = 0;

        if (x + width > 1) x = mouseX() - width - offset;
        if (x < 0) x = 1 - width;
        if (x < 0) x = 0;

        super.draw();
    }

    @Override
    public GUITooltipView recalc(int subIndexChanged)
    {
        super.recalc(subIndexChanged);

        offset = 12d / screen.width;

        return this;
    }
}
