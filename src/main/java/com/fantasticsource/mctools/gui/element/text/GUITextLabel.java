package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

import static com.fantasticsource.mctools.gui.GUIScreen.getIdleColor;

public class GUITextLabel extends GUITextButton
{
    public GUITextLabel(GUIScreen screen, double width)
    {
        this(screen, width, 1);
    }

    public GUITextLabel(GUIScreen screen, double width, double scale)
    {
        this(screen, width, Color.AQUA, scale);
    }

    public GUITextLabel(GUIScreen screen, double width, Color color)
    {
        this(screen, width, color, 1);
    }

    public GUITextLabel(GUIScreen screen, double width, Color color, double scale)
    {
        this(screen, width, color, getIdleColor(color).setAF(color.af() * 0.4f), scale);
    }

    public GUITextLabel(GUIScreen screen, double width, Color border, Color center)
    {
        this(screen, width, border, center, 1);
    }

    public GUITextLabel(GUIScreen screen, double width, Color border, Color center, double scale)
    {
        super(screen, "", border, center, scale);
        setSubElementAutoplaceMethod(AP_CENTERED_H_TOP_TO_BOTTOM);
        this.width = width;

        setColor(border, center);
    }


    public GUITextLabel setText(String text)
    {
        ((GUIText) children.get(1)).setText(text);
        return this;
    }


    @Override
    public GUITextLabel recalc(int subIndexChanged)
    {
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
}
