package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.renderer.GlStateManager;

public class GUIScrollView extends GUIView
{
    public double internalHeight, progress = -1;
    public double top, bottom;

    public GUIScrollView(GUIScreen screen, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc(0);
    }

    public GUIScrollView(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc(0);
    }

    public GUIElement recalcThisOnly()
    {
        internalHeight = 0;
        for (GUIElement element : children)
        {
            internalHeight = Tools.max(internalHeight, element.y + element.height);
        }

        recalc2();

        return this;
    }

    @Override
    public GUIElement recalc(int subIndexChanged)
    {
        recalcAndRepositionSubElements(subIndexChanged);

        recalcThisOnly();

        postRecalc();

        return this;
    }

    protected void recalc2()
    {
        if (internalHeight <= 1)
        {
            progress = -1;
            top = 0;
        }
        else
        {
            if (progress == -1) progress = 0;
            top = (internalHeight - 1) * progress;
        }
        bottom = top + 1;
    }

    public void focus(GUIElement child)
    {
        if (!children.contains(child)) return;

        if (internalHeight <= height) return;

        progress = Tools.min(1, child.y * height / (internalHeight - height));
    }

    @Override
    public void draw()
    {
        recalc2();

        GlStateManager.pushMatrix();
        GlStateManager.translate(0, -top, 0);

        drawChildren();

        GlStateManager.popMatrix();
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        recalc2();
        return super.mousePressed(x, y, button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button)
    {
        recalc2();
        return super.mouseReleased(x, y, button);
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        recalc2();
        super.mouseDrag(x, y, button);
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        recalc2();
        super.mouseWheel(x, y, delta);
    }
}
