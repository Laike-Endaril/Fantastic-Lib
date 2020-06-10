package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import net.minecraft.client.renderer.GlStateManager;

public class GUIPanZoomView extends GUIView
{
    private static final double PAN_RATE = 0.01, ZOOM_RATE = 2;

    public double viewX = 0, viewY = 0, zoom = 1;
    public double panBorderSize = 0.1;

    public GUIPanZoomView(GUIScreen screen, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc(0);
    }

    public GUIPanZoomView(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc(0);
    }

    public double viewW()
    {
        return absoluteWidth() / zoom;
    }

    public double viewH()
    {
        return absoluteHeight() / zoom;
    }

    public int viewPxW()
    {
        return (int) Math.round(viewW() * screen.pxWidth);
    }

    public int viewPxH()
    {
        return (int) Math.round(viewH() * screen.pxHeight);
    }

    public void focus(GUIElement child)
    {
        if (!children.contains(child)) return;

        viewX = child.x - viewW() * 0.5;
        viewY = child.y - viewH() * 0.5;
    }

    @Override
    public void tick()
    {
        if (isMouseWithin())
        {
            double portX = absoluteX(), portY = absoluteY(), portW = absoluteWidth(), portH = absoluteHeight();
            double mouseRelX = mouseX() - portX, mouseRelY = mouseY() - portY;

            double mouseXPercent = mouseRelX / portW, mouseYPercent = mouseRelY / portH;

            if (mouseXPercent < panBorderSize)
            {
                viewX -= viewW() * PAN_RATE * (panBorderSize - mouseXPercent) / panBorderSize;
            }
            else if (mouseXPercent > 1 - panBorderSize)
            {
                viewX += viewW() * PAN_RATE * (mouseXPercent - (1 - panBorderSize)) / panBorderSize;
            }

            if (mouseYPercent < panBorderSize)
            {
                viewY -= viewH() * PAN_RATE * (panBorderSize - mouseYPercent) / panBorderSize;
            }
            else if (mouseYPercent > 1 - panBorderSize)
            {
                viewY += viewH() * PAN_RATE * (mouseYPercent - (1 - panBorderSize)) / panBorderSize;
            }
        }
    }

    @Override
    public void draw()
    {
        GlStateManager.pushMatrix();
        GlStateManager.scale(zoom, zoom, 1);
        GlStateManager.translate(-viewX, -viewY, 0);

        drawChildren();

        GlStateManager.popMatrix();
    }

    @Override
    public boolean mousePressed(int button)
    {
        return super.mousePressed(button);
    }

    @Override
    public boolean mouseReleased(int button)
    {
        return super.mouseReleased(button);
    }

    @Override
    public void mouseDrag(int button)
    {
        super.mouseDrag(button);
    }

    @Override
    public void mouseWheel(int delta)
    {
        if (isMouseWithin())
        {
            if (delta > 0)
            {
                viewX += viewW() * 0.5;
                viewY += viewH() * 0.5;
                zoom *= ZOOM_RATE;
                viewX -= viewW() * 0.5;
                viewY -= viewH() * 0.5;
            }
            else if (delta < 0)
            {
                viewX += viewW() * 0.5;
                viewY += viewH() * 0.5;
                zoom /= ZOOM_RATE;
                viewX -= viewW() * 0.5;
                viewY -= viewH() * 0.5;
            }
        }

        super.mouseWheel(delta);
    }
}
