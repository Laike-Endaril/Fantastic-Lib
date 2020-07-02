package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUITooltipView extends GUIAutocroppedView
{
    private double offset = 0;

    public GUITooltipView(GUIScreen screen)
    {
        super(screen, 0, 0, 0.1, new GUIDarkenedBackground(screen));
        setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
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

        drawChildren();
    }

    @Override
    public GUITooltipView recalc(int subIndexChanged)
    {
        width = 1;
        height = 1;
        if (parent == null) return this;


        recalcAndRepositionSubElements(0);

        width = 0;
        height = 0;
        for (GUIElement element : children)
        {
            if (element != background)
            {
                width = Tools.max(width, element.x + element.width);
                height = Tools.max(height, element.y + element.height);
            }
        }

        recalcAndRepositionSubElements(0);

        double paddingPx = Tools.min(absolutePxWidth(), absolutePxHeight()) * padding;
        double xPad = paddingPx / parent.absolutePxWidth(), yPad = paddingPx / parent.absolutePxHeight();

        width += xPad * 2;
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

        offset = 12d / screen.width;

        postRecalc();

        return this;
    }

    @Override
    public GUIElement setTooltip(String tooltip)
    {
        clear();
        add(background);

        for (String line : Tools.fixedSplit(tooltip, "\n"))
        {
            add(new GUIText(screen, line, Color.YELLOW));
        }
        return this;
    }
}
