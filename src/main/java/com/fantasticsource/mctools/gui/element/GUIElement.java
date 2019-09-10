package com.fantasticsource.mctools.gui.element;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public abstract class GUIElement
{
    public static final Color T_GRAY = new Color(0xAAAAAA77);

    public static final byte
            AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM = 0,
            AP_RIGHT_TO_LEFT_TOP_TO_BOTTOM = 1,
            AP_LEFT_TO_RIGHT_BOTTOM_TO_TOP = 2,
            AP_RIGHT_TO_LEFT_BOTTOM_TO_TOP = 3,
            AP_TOP_TO_BOTTOM_LEFT_TO_RIGHT = 4,
            AP_TOP_TO_BOTTOM_RIGHT_TO_LEFT = 5,
            AP_BOTTOM_TO_TOP_LEFT_TO_RIGHT = 6,
            AP_BOTTOM_TO_TOP_RIGHT_TO_LEFT = 7;

    public static final byte
            HA_LEFT = 0,
            HA_MIDDLE = 1,
            HA_CENTER = 1,
            HA_RIGHT = 2,
            VA_TOP = 0,
            VA_MIDDLE = 1,
            VA_CENTER = 1,
            VA_BOTTOM = 1;


    public int[] currentScissor = null;

    public double x, y, width, height;
    public GUIElement parent = null;
    public ArrayList<GUIElement> children = new ArrayList<>();
    protected boolean autoplace = false;
    protected byte subElementAutoplaceMethod, hAlign, vAlign;
    protected GUIScreen screen;
    protected boolean active = false, externalDeactivation = false;
    private ArrayList<GUIElement> linkedMouseActivity = new ArrayList<>();
    private ArrayList<GUIElement> linkedMouseActivityReverse = new ArrayList<>();


    public GUIElement(GUIScreen screen, double width, double height)
    {
        this(screen, width, height, HA_LEFT, VA_TOP, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public GUIElement(GUIScreen screen, double width, double height, byte hAlign, byte vAlign)
    {
        this(screen, width, height, hAlign, vAlign, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public GUIElement(GUIScreen screen, double width, double height, byte subElementAutoplaceMethod)
    {
        this(screen, width, height, HA_LEFT, VA_TOP, subElementAutoplaceMethod);
    }

    public GUIElement(GUIScreen screen, double width, double height, byte hAlign, byte vAlign, byte subElementAutoplaceMethod)
    {
        this(screen, 0, 0, width, height, hAlign, vAlign, subElementAutoplaceMethod);
        autoplace = true;
    }


    public GUIElement(GUIScreen screen, double x, double y, double width, double height)
    {
        this(screen, x, y, width, height, HA_LEFT, VA_TOP, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public GUIElement(GUIScreen screen, double x, double y, double width, double height, byte hAlign, byte vAlign)
    {
        this(screen, x, y, width, height, hAlign, vAlign, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public GUIElement(GUIScreen screen, double x, double y, double width, double height, byte subElementAutoplaceMethod)
    {
        this(screen, x, y, width, height, HA_LEFT, VA_TOP, subElementAutoplaceMethod);
    }

    public GUIElement(GUIScreen screen, double x, double y, double width, double height, byte hAlign, byte vAlign, byte subElementAutoplaceMethod)
    {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.hAlign = hAlign;
        this.vAlign = vAlign;
        this.subElementAutoplaceMethod = subElementAutoplaceMethod;
    }

    public boolean isWithin(double x, double y)
    {
        double xx = getScreenX(), yy = getScreenY();
        return xx <= x && x < xx + getScreenWidth() && yy <= y && y < yy + getScreenHeight();
    }

    public void draw()
    {
        if (children.size() > 0 && width > 0 && height > 0)
        {
            double screenWidth = screen.width, screenHeight = screen.height;

            int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
            double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;

            currentScissor = new int[]{(int) (getScreenX() * wScale), (int) ((1 - (getScreenY() + getScreenHeight())) * hScale), (int) (getScreenWidth() * wScale), (int) (getScreenHeight() * hScale)};
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
                if (element.x + element.width < 0 || element.x > 1 || element.y + element.height < 0 || element.y >= 1) continue;
                element.draw();
            }

            currentScissor = null;
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
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
        return parent.getScreenX() + x * parent.getScreenWidth();
    }

    public double getScreenY()
    {
        if (parent == null) return y;
        return parent.getScreenY() + y * parent.getScreenHeight();
    }

    public double getScreenWidth()
    {
        if (parent == null) return width;
        return parent.getScreenWidth() * width;
    }

    public double getScreenHeight()
    {
        if (parent == null) return height;
        return parent.getScreenHeight() * height;
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

    public GUIElement recalc()
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.recalc();
        return this;
    }


    public GUIElement add(GUIElement element)
    {
        if (element.autoplace)
        {
            //TODO
        }

        element.parent = this;
        children.add(element);
        recalc();
        return element;
    }

    public GUIElement add(int index, GUIElement element)
    {
        if (element.autoplace)
        {
            //TODO
        }

        element.parent = this;
        children.add(index, element);
        recalc();
        return element;
    }

    public void remove(GUIElement element)
    {
        if (element.parent == this) element.parent = null;
        children.remove(element);
        recalc();
    }

    public void remove(int index)
    {
        GUIElement element = children.remove(index);
        if (element.parent == this) element.parent = null;
        recalc();
    }

    public int size()
    {
        return children.size();
    }

    public void clear()
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) if (child.parent == this) child.parent = null;
        children.clear();
        recalc();
    }

    public GUIElement get(int index)
    {
        return children.get(index);
    }

    public int indexOf(GUIElement child)
    {
        int i = 0;
        for (GUIElement element : children)
        {
            if (element == child) return i;
            i++;
        }
        return -1;
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

    public void setExternalDeactivation(boolean external, boolean recursive)
    {
        externalDeactivation = external;
        if (recursive)
        {
            for (GUIElement child : children) child.setExternalDeactivation(external, true);
        }
    }

    public void setActive(boolean active, boolean external)
    {
        if (!active && externalDeactivation && !external) return;

        this.active = active;
        for (GUIElement element : linkedMouseActivity) element.active = active;
    }

    public boolean isActive()
    {
        return active;
    }

    public void setActive(boolean active)
    {
        setActive(active, false);
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone())
        {
            child.keyTyped(typedChar, keyCode);
        }
    }
}