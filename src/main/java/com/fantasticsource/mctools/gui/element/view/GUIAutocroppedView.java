package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;

/**
 * Does not currently support relative-dimension subelements (except in background)
 * Eg. text- and image-based elements are fine
 */
public class GUIAutocroppedView extends GUIView
{
    public GUIElement background;
    protected double padding;

    public GUIAutocroppedView(GUIScreen screen)
    {
        this(screen, 0, null);
    }

    public GUIAutocroppedView(GUIScreen screen, double padding)
    {
        this(screen, padding, null);
    }

    public GUIAutocroppedView(GUIScreen screen, GUIElement background)
    {
        this(screen, 0, background);
    }

    public GUIAutocroppedView(GUIScreen screen, double padding, GUIElement background)
    {
        super(screen, 1, 1);

        this.padding = Math.min(padding, 0.5);

        this.background = background;
        if (background != null)
        {
            background.autoplace = false;
            add(background);
        }
    }

    public GUIAutocroppedView(GUIScreen screen, double x, double y)
    {
        this(screen, x, y, 0, null);
    }

    public GUIAutocroppedView(GUIScreen screen, double x, double y, double padding)
    {
        this(screen, x, y, padding, null);
    }

    public GUIAutocroppedView(GUIScreen screen, double x, double y, GUIElement background)
    {
        this(screen, x, y, 0, background);
    }

    public GUIAutocroppedView(GUIScreen screen, double x, double y, double padding, GUIElement background)
    {
        super(screen, x, y, 1, 1);

        this.padding = Math.min(padding, 0.5);

        this.background = background;
        if (background != null)
        {
            background.autoplace = false;
            add(background);
        }
    }

    @Override
    public GUIAutocroppedView recalc(int subIndexChanged)
    {
        width = 1;
        height = 1;
        if (parent == null) return this;


        recalcAndRepositionSubElements(0);

        width = 0;
        height = 0;
        switch (subElementAutoplaceMethod)
        {
            case AP_CENTER:
                double minY = Double.MAX_VALUE;
                for (GUIElement element : children)
                {
                    if (element != background)
                    {
                        minY = Tools.min(minY, element.y);
                        width = Tools.max(width, element.width);
                        height = Tools.max(height, element.height);
                    }
                }
                height -= minY;
                break;


            case AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM:
                for (GUIElement element : children)
                {
                    if (element != background)
                    {
                        width = Tools.max(width, element.x + element.width);
                        height = Tools.max(height, element.y + element.height);
                    }
                }
                break;


            case AP_CENTERED_H_TOP_TO_BOTTOM:
                for (GUIElement element : children)
                {
                    if (element != background)
                    {
                        width = Tools.max(width, element.width);
                        height = Tools.max(height, element.y + element.height);
                    }
                }
                break;


            case AP_X_0_TOP_TO_BOTTOM:
                for (GUIElement element : children)
                {
                    if (element != background)
                    {
                        width = Tools.max(width, element.width);
                        height = Tools.max(height, element.y + element.height);
                    }
                }
                break;

            //TODO add other AP types

            default:
                throw new IllegalArgumentException("Unimplemented autoplace type: " + subElementAutoplaceMethod);
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
                element.recalc(0);
            }
        }

        postRecalc();

        return this;
    }
}
