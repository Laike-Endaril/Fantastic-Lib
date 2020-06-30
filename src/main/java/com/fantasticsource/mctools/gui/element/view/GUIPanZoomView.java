package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import net.minecraft.client.renderer.GlStateManager;

public class GUIPanZoomView extends GUIView
{
    public double viewX = 0, viewY = 0;
    protected double zoom = 1;
    protected boolean panning = false;
    protected double zoomResetX = Double.MAX_VALUE, zoomResetY = Double.MAX_VALUE, panX = Double.MAX_VALUE, panY = Double.MAX_VALUE;

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

    public int viewPxX()
    {
        return (int) Math.round(viewX * absolutePxWidth());
    }

    public int viewPxY()
    {
        return (int) Math.round(viewY * absolutePxHeight());
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
        while (!children.contains(child) && child.parent != this && child.parent != null)
        {
            child = child.parent;
        }
        if (children.contains(child))
        {
            viewX = child.x + child.width * 0.5 - viewW() * 0.5;
            viewY = child.y + child.height * 0.5 - viewH() * 0.5;
        }
    }

    public GUIPanZoomView setZoom(double zoom)
    {
        return setZoom(zoom, 0.5, 0.5);
    }

    public GUIPanZoomView setZoom(double zoom, double originXPercent, double originYPercent)
    {
        viewX += viewW() * originXPercent;
        viewY += viewH() * originYPercent;
        this.zoom = zoom;
        viewX -= viewW() * originXPercent;
        viewY -= viewH() * originYPercent;

        return this;
    }

    public double getZoom()
    {
        return zoom;
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
        boolean result = super.mousePressed(button);

        //Zoom
        if (isMouseWithin() && button == FantasticConfig.guiSettings.zoomResetButton)
        {
            zoomResetX = mouseX();
            zoomResetY = mouseY();
            result = true;
        }

        //Pan
        if (isMouseWithin() && button == FantasticConfig.guiSettings.panButton)
        {
            panX = mouseX();
            panY = mouseY();
            result = true;
        }

        return result;
    }

    @Override
    public void mouseDrag(int button)
    {
        super.mouseDrag(button);

        //Pan
        if (button == FantasticConfig.guiSettings.panButton && (panning || ((FantasticConfig.guiSettings.panButton != FantasticConfig.guiSettings.zoomResetButton) || (panX != Double.MAX_VALUE && (panX != mouseX() || panY != mouseY())))))
        {
            double xDif = (panX - mouseX()) * absoluteWidth(), yDif = (panY - mouseY()) * absoluteHeight();

            viewX += xDif / zoom;
            viewY += yDif / zoom;

            panning = true;
            panX = mouseX();
            panY = mouseY();
        }
    }

    @Override
    public boolean mouseReleased(int button)
    {
        boolean result = super.mouseReleased(button);

        //Zoom
        if (button == FantasticConfig.guiSettings.zoomResetButton && zoomResetX != Double.MAX_VALUE && ((FantasticConfig.guiSettings.zoomResetButton != FantasticConfig.guiSettings.panButton) || !panning))
        {
            if (GUIScreen.isCtrlKeyDown() != FantasticConfig.guiSettings.zoomFocusMouse)
            {
                double portX = absoluteX(), portY = absoluteY(), portW = absoluteWidth(), portH = absoluteHeight();
                double mouseRelX = mouseX() - portX, mouseRelY = mouseY() - portY;
                double mouseXPercent = mouseRelX / portW, mouseYPercent = mouseRelY / portH;

                double centerX = viewX + viewW() * mouseXPercent, centerY = viewY + viewH() * mouseYPercent;

                setZoom(1);

                viewX = centerX - viewW() * 0.5;
                viewY = centerY - viewH() * 0.5;
            }
            else setZoom(1);

            result = true;
            zoomResetX = Double.MAX_VALUE;
            zoomResetY = Double.MAX_VALUE;
        }

        //Pan
        if (button == FantasticConfig.guiSettings.panButton && panning)
        {
            //TODO

            result = true;
            panning = false;
            panX = Double.MAX_VALUE;
            panY = Double.MAX_VALUE;
        }

        return result;
    }

    @Override
    public void mouseWheel(int delta)
    {
        if (isMouseWithin())
        {
            if (delta > 0)
            {
                if (GUIScreen.isCtrlKeyDown() != FantasticConfig.guiSettings.zoomFocusMouse)
                {
                    double portX = absoluteX(), portY = absoluteY(), portW = absoluteWidth(), portH = absoluteHeight();
                    double mouseRelX = mouseX() - portX, mouseRelY = mouseY() - portY;
                    double mouseXPercent = mouseRelX / portW, mouseYPercent = mouseRelY / portH;

                    setZoom(zoom * FantasticConfig.guiSettings.zoomRate, mouseXPercent, mouseYPercent);
                }
                else setZoom(zoom * FantasticConfig.guiSettings.zoomRate);
            }
            else if (delta < 0)
            {
                if (GUIScreen.isCtrlKeyDown() != FantasticConfig.guiSettings.zoomFocusMouse)
                {
                    double portX = absoluteX(), portY = absoluteY(), portW = absoluteWidth(), portH = absoluteHeight();
                    double mouseRelX = mouseX() - portX, mouseRelY = mouseY() - portY;
                    double mouseXPercent = mouseRelX / portW, mouseYPercent = mouseRelY / portH;

                    setZoom(zoom / FantasticConfig.guiSettings.zoomRate, mouseXPercent, mouseYPercent);
                }
                else setZoom(zoom / FantasticConfig.guiSettings.zoomRate);
            }
        }

        super.mouseWheel(delta);
    }
}
