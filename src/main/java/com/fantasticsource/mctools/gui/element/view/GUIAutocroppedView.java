package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;

public class GUIAutocroppedView extends GUIView
{
    public GUIElement background;
    public double padding;

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

        this.padding = padding;

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

        this.padding = padding;

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
        super.recalc(0);

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

        super.recalc(0);

        double xPad = padding / screen.width, yPad = padding / screen.height;
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

        return this;
    }
}
