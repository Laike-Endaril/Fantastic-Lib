package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;

public class GUIButton extends GUIAutocroppedView
{
    private GUIElement idleElement, hoverElement, activeElement;

    public GUIButton(GUIScreen screen, GUIElement idleElement, GUIElement hoverElement, GUIElement activeElement)
    {
        super(screen);

        this.idleElement = idleElement;
        this.hoverElement = hoverElement;
        this.activeElement = activeElement;

        add(idleElement);
    }

    @Override
    public void draw()
    {
        if (isMouseWithin())
        {
            if (active) set(activeElement);
            else set(hoverElement);
        }
        else set(idleElement);

        super.draw();
    }

    private void set(GUIElement element)
    {
        int index = indexOf(element);
        if (index != -1) return;

        index = indexOf(idleElement);
        if (index != -1) remove(index);
        index = indexOf(hoverElement);
        if (index != -1) remove(index);
        index = indexOf(activeElement);
        if (index != -1) remove(index);

        add(element);
    }
}
