package com.fantasticsource.mctools.gui.guielements;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public abstract class GUIElement
{
    public int[] currentScissor = null;

    public double x, y, width, height;
    public GUIElement parent = null;
    protected ArrayList<GUIElement> children = new ArrayList<>();
    protected GUIScreen screen;
    protected boolean active = false;
    private ArrayList<GUIElement> linkedMouseActivity = new ArrayList<>();
    private ArrayList<GUIElement> linkedMouseActivityReverse = new ArrayList<>();


    public GUIElement(GUIScreen screen, double x, double y)
    {
        this.screen = screen;
        this.x = x;
        this.y = y;
    }

    public abstract boolean isWithin(double x, double y);

    public void draw()
    {
        double screenWidth = screen.width, screenHeight = screen.height;

        int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
        double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;

        currentScissor = new int[]{(int) (getScreenX() * wScale), (int) ((1 - (getScreenY() + height)) * hScale), (int) (width * wScale), (int) (height * hScale)};
        if (parent != null && parent.currentScissor != null)
        {
            currentScissor[0] = Tools.max(currentScissor[0], parent.currentScissor[0]);
            currentScissor[1] = Tools.max(currentScissor[1], parent.currentScissor[1]);
            currentScissor[2] = Tools.min(currentScissor[2], parent.currentScissor[2]);
            currentScissor[3] = Tools.min(currentScissor[3], parent.currentScissor[3]);
        }
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(currentScissor[0], currentScissor[1], currentScissor[2], currentScissor[3]);

        for (GUIElement element : children)
        {
            if (element.x + element.width < 0 || element.x > width || element.y + element.height < 0 || element.y >= height) continue;
            element.draw();
        }

        currentScissor = null;
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public void mouseWheel(double x, double y, int delta)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseWheel(x - this.x, y - this.y, delta);
    }

    public boolean mousePressed(double x, double y, int button)
    {
        if (button == 0 && isMouseWithin()) setActive(true);

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mousePressed(x - this.x, y - this.y, button);

        return active;
    }

    public void mouseReleased(double x, double y, int button)
    {
        if (button == 0)
        {
            if (active && isMouseWithin()) MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this));
            setActive(false);
        }

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseReleased(x - this.x, y - this.y, button);
    }

    public void mouseDrag(double x, double y, int button)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseDrag(x - this.x, y - this.y, button);
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
        for (GUIElement element : linkedMouseActivityReverse)
        {
            if (element.isWithin(mouseX(), mouseY())) return true;
        }

        return isWithin(mouseX(), mouseY()) && (parent == null || parent.isMouseWithin());
    }

    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }

    public void recalc()
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.recalc();
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
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) if (child.parent == this) child.parent = null;
        children.clear();
    }

    public GUIElement get(int index)
    {
        return children.get(index);
    }

    public void linkMouseActivity(GUIElement element)
    {
        linkedMouseActivity.add(element);
        element.linkedMouseActivityReverse.add(this);
    }

    public void unlinkMouseActivity(GUIElement element)
    {
        linkedMouseActivity.remove(element);
        element.linkedMouseActivityReverse.remove(this);
    }

    public void setActive(boolean active)
    {
        this.active = active;
        for (GUIElement element : linkedMouseActivity) element.active = active;
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone())
        {
            child.keyTyped(typedChar, keyCode);
        }
    }
}
