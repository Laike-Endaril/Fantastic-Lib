package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;

public class GUIAutocroppedView extends GUIView
{
    private GUIElement background = null;

    public GUIAutocroppedView(GUIScreen screen)
    {
        super(screen, 1, 1);
    }

    public GUIAutocroppedView(GUIScreen screen, GUIElement background)
    {
        super(screen, 1, 1);

        this.background = background;
        background.autoplace = false;
        add(background);
    }

    public GUIAutocroppedView(GUIScreen screen, double x, double y)
    {
        super(screen, x, y, 1, 1);
    }

    public GUIAutocroppedView(GUIScreen screen, double x, double y, GUIElement background)
    {
        super(screen, x, y, 1, 1);

        this.background = background;
        background.autoplace = false;
        add(background);
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

        return this;
    }
}
