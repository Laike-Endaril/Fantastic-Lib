package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import net.minecraft.client.renderer.GlStateManager;

public class GUIPanZoomView extends GUIView
{
    double viewX = 0, viewY = 0, zoom = 1;
    double panBorderSize = 0.1;

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
        return absolutePxWidth() / zoom;
    }

    public double viewH()
    {
        return absolutePxHeight() / zoom;
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
        System.out.println("Tick");
        if (isMouseWithin())
        {
            double portX = absoluteX(), portY = absoluteY(), portW = absoluteWidth(), portH = absoluteHeight();
            double mouseRelX = mouseX() - portX, mouseRelY = mouseY() - portY;

            double mouseXPercent = mouseRelX / portW, mouseYPercent = mouseRelY / portH;

            if (mouseXPercent < panBorderSize)
            {
                System.out.println("Pan left");
                viewX -= viewW() * (panBorderSize - mouseXPercent) / panBorderSize;
            }
            else if (mouseXPercent > 1 - panBorderSize)
            {
                System.out.println("Pan right");
                viewX += viewW() * (mouseXPercent - (1 - panBorderSize)) / panBorderSize;
            }

            if (mouseYPercent < panBorderSize)
            {
                System.out.println("Pan up");
                viewY -= viewH() * (panBorderSize - mouseYPercent) / panBorderSize;
            }
            else if (mouseYPercent > 1 - panBorderSize)
            {
                System.out.println("Pan down");
                viewY += viewH() * (mouseYPercent - (1 - panBorderSize)) / panBorderSize;
            }
        }
    }

    @Override
    public void draw()
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(-viewX, -viewY, 0);
        GlStateManager.scale(zoom, zoom, 1);

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
                System.out.println("Zoom in");
                viewX += viewW() * 0.5;
                viewY += viewH() * 0.5;
                zoom *= 1.2;
                viewX -= viewW() * 0.5;
                viewY -= viewH() * 0.5;
            }
            else if (delta < 0)
            {
                System.out.println("Zoom out");
                viewX += viewW() * 0.5;
                viewY += viewH() * 0.5;
                zoom /= 1.2;
                viewX -= viewW() * 0.5;
                viewY -= viewH() * 0.5;
            }
        }

        super.mouseWheel(delta);
    }
}
