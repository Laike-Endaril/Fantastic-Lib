package com.fantasticsource.mctools.gui.guielements;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

public abstract class GUIElement
{
    public double x, y, width, height;
    public GUIElement parent = null;
    protected ArrayList<GUIElement> children = new ArrayList<>();
    protected GUIScreen screen;
    protected boolean active = false;


    public GUIElement(GUIScreen screen, double x, double y)
    {
        this.screen = screen;
        this.x = x;
        this.y = y;
    }

    public abstract boolean isWithin(double x, double y);

    public void draw()
    {
        for (GUIElement child : children) child.draw();
    }

    public void mouseWheel(double x, double y, int delta)
    {
        for (GUIElement child : children) child.mouseWheel(x - this.x, y - this.y, delta);
    }

    public boolean mousePressed(double x, double y, int button)
    {
        if (button == 0 && isMouseWithin()) active = true;

        for (GUIElement child : children) child.mousePressed(x - this.x, y - this.y, button);

        return active;
    }

    public void mouseReleased(double x, double y, int button)
    {
        if (button == 0)
        {
            if (active && isMouseWithin()) MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this));
            active = false;
        }

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseReleased(x - this.x, y - this.y, button);
    }

    public void mouseDrag(double x, double y, int button)
    {
        for (GUIElement child : children) child.mouseDrag(x - this.x, y - this.y, button);
    }

    public double getScreenX()
    {
        if (parent == null) return x;
        return parent.getScreenX() + x;
    }

    public double getScreenY()
    {
        if (parent == null) return y;
        return parent.getScreenY() + y;
    }

    public double mouseX()
    {
        if (parent == null) return GUIScreen.mouseX;
        return parent.mouseX() + parent.childMouseXOffset();
    }

    public double mouseY()
    {
        if (parent == null) return GUIScreen.mouseY;
        return parent.mouseY() + parent.childMouseYOffset();
    }

    public double childMouseXOffset()
    {
        return 0;
    }

    public double childMouseYOffset()
    {
        return 0;
    }

    public boolean isMouseWithin()
    {
        return isWithin(mouseX(), mouseY());
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    public void recalc()
    {
        for (GUIElement element : children) element.recalc();
    }


    public void add(GUIElement element)
    {
        element.parent = this;
        children.add(element);
    }

    public void add(int index, GUIElement element)
    {
        element.parent = this;
        children.add(index, element);
    }

    public void remove(GUIElement element)
    {
        if (element.parent == this) element.parent = null;
        children.remove(element);
    }

    public void remove(int index)
    {
        GUIElement element = children.remove(index);
        if (element.parent == this) element.parent = null;
    }

    public int size()
    {
        return children.size();
    }

    public void clear()
    {
        for (GUIElement element : children) if (element.parent == this) element.parent = null;
        children.clear();
    }

    public GUIElement get(int index)
    {
        return children.get(index);
    }
}
