package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;

public class GUIView extends GUIElement
{
    public GUIView(GUIScreen screen, double width, double height)
    {
        super(screen, width, height);
    }

    public GUIView(GUIScreen screen, double x, double y, double width, double height)
    {
        super(screen, x, y, width, height);
    }

    @Override
    public void draw()
    {
        drawChildren();
    }
}
