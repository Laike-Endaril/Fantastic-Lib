package com.fantasticsource.mctools.gui.element;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;

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
            AP_BOTTOM_TO_TOP_RIGHT_TO_LEFT = 7,
            AP_CENTERED_H_TOP_TO_BOTTOM = 8,
            AP_CENTERED_V_LEFT_TO_RIGHT = 9,
            AP_X_0_TOP_TO_BOTTOM = 10;


    public double x, y, width, height;
    public GUIElement parent = null;
    public ArrayList<GUIElement> children = new ArrayList<>();
    public boolean autoplace = false;
    protected double autoX = 0, autoY = 0, furthestX = 0, furthestY = 0;
    protected byte subElementAutoplaceMethod;
    protected GUIScreen screen;
    protected boolean active = false, externalDeactivation = false;
    private ArrayList<GUIElement> linkedMouseActivity = new ArrayList<>();
    private ArrayList<GUIElement> linkedMouseActivityReverse = new ArrayList<>();

    public final ArrayList<Runnable> onClickActions = new ArrayList<>();
    public final ArrayList<Runnable> onRecalcActions = new ArrayList<>();


    public GUIElement(GUIScreen screen, double width, double height)
    {
        this(screen, width, height, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public GUIElement(GUIScreen screen, double width, double height, byte subElementAutoplaceMethod)
    {
        this(screen, 0, 0, width, height, subElementAutoplaceMethod);
        autoplace = true;
    }


    public GUIElement(GUIScreen screen, double x, double y, double width, double height)
    {
        this(screen, x, y, width, height, AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM);
    }

    public GUIElement(GUIScreen screen, double x, double y, double width, double height, byte subElementAutoplaceMethod)
    {
        this.screen = screen;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.subElementAutoplaceMethod = subElementAutoplaceMethod;
    }

    public boolean isWithin(double x, double y)
    {
        double xx = absoluteX(), yy = absoluteY();
        return xx <= x && x < xx + absoluteWidth() && yy <= y && y < yy + absoluteHeight();
    }

    private int[] preDraw()
    {
        //Matrix
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(width, height, 1);

        //Scissor setup
        int[] lastScissor = new int[4];
        System.arraycopy(GUIScreen.currentScissor, 0, lastScissor, 0, 4);

        GUIScreen.currentScissor[0] = Tools.max(GUIScreen.currentScissor[0], absolutePxX());
        GUIScreen.currentScissor[1] = Tools.max(GUIScreen.currentScissor[1], absolutePxY());
        GUIScreen.currentScissor[2] = Tools.min(GUIScreen.currentScissor[2], absolutePxX() + absolutePxWidth());
        GUIScreen.currentScissor[3] = Tools.min(GUIScreen.currentScissor[3], absolutePxY() + absolutePxHeight());

        //Attempt scissor
        if (!screen.scissor())
        {
//            System.out.println(toString());
//            System.out.println(x + ", " + y + ", " + width + ", " + height);
//            System.out.println(absolutePxX() + ", " + absolutePxY() + ", " + (absolutePxX() + absolutePxWidth()) + ", " + (absolutePxY() + absolutePxHeight()));

            GUIScreen.currentScissor = lastScissor;

//            System.out.println(GUIScreen.currentScissor[0] + ", " + GUIScreen.currentScissor[1] + ", " + GUIScreen.currentScissor[2] + ", " + GUIScreen.currentScissor[3]);
//            System.out.println();

            GlStateManager.popMatrix();
            return null;
        }
        return lastScissor;
    }

    protected final void drawChildren()
    {
        if (children.size() > 0 && width > 0 && height > 0)
        {
            for (GUIElement element : children)
            {
                if (element.x + element.width < 0 || element.x > 1 || element.y + element.height < 0 || element.y >= 1) continue;

                int[] lastScissor = element.preDraw();
                if (lastScissor != null)
                {
                    element.draw();
                    element.postDraw(lastScissor);
                }
            }
        }
    }

    private void postDraw(int[] lastScissor)
    {
        //Undo scissor
        GUIScreen.currentScissor = lastScissor;

        //Undo matrix
        GlStateManager.popMatrix();
    }

    public abstract void draw();

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

    public boolean mouseReleased(double x, double y, int button)
    {
        boolean result = false;
        if (button == 0)
        {
            if (active && isMouseWithin())
            {
                if (!MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this))) click();
                result = true;
            }
            setActive(false);
        }

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseReleased(x - this.x, y - this.y, button);

        return result;
    }

    public void click()
    {
        for (Runnable action : onClickActions) action.run();
    }

    public GUIElement addClickActions(Runnable... actions)
    {
        onClickActions.addAll(Arrays.asList(actions));
        return this;
    }

    public GUIElement addRecalcActions(Runnable... actions)
    {
        onRecalcActions.addAll(Arrays.asList(actions));
        return this;
    }

    public void mouseDrag(double x, double y, int button)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseDrag(x - this.x, y - this.y, button);
    }

    public final double absoluteX()
    {
        if (parent == null) return x;
        return parent.absoluteX() + x * parent.absoluteWidth();
    }

    public final double absoluteY()
    {
        if (parent == null) return y;

        if (parent instanceof GUIScrollView) return parent.absoluteY() + (y - ((GUIScrollView) parent).top) * parent.absoluteHeight();

        return parent.absoluteY() + y * parent.absoluteHeight();
    }

    public final int absolutePxX()
    {
        return (int) (absoluteX() * screen.pxWidth);
    }

    public final int absolutePxY()
    {
        return (int) (absoluteY() * screen.pxHeight);
    }

    public final double absoluteWidth()
    {
        if (parent == null) return width;
        return parent.absoluteWidth() * width;
    }

    public final double absoluteHeight()
    {
        if (parent == null) return height;
        return parent.absoluteHeight() * height;
    }

    public final int absolutePxWidth()
    {
        return (int) (absoluteWidth() * screen.pxWidth);
    }

    public final int absolutePxHeight()
    {
        return (int) (absoluteHeight() * screen.pxHeight);
    }

    public final double mouseX()
    {
        if (parent == null) return GUIScreen.mouseX;
        return parent.mouseX() + parent.childMouseXOffset();
    }

    public final double mouseY()
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

    public final boolean isMouseWithin()
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
        return recalc(0);
    }

    public GUIElement recalc(int subIndexChanged)
    {
        recalcAndRepositionSubElements(subIndexChanged);

        for (Runnable action : onRecalcActions) action.run();

        return this;
    }


    public GUIElement add(GUIElement element)
    {
        element.parent = this;
        children.add(element);
        recalc(size() - 1);
        return element;
    }

    public GUIElement add(int index, GUIElement element)
    {
        element.parent = this;
        children.add(index, element);
        recalc(index);
        return element;
    }

    public void recalcAndRepositionSubElements(int startIndex)
    {
        switch (subElementAutoplaceMethod)
        {
            case AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM:
                if (size() <= 1 || startIndex != size() - 1)
                {
                    autoX = 0;
                    autoY = 0;
                    furthestX = 0;
                    furthestY = 0;

                    for (int i = 0; i < startIndex; i++)
                    {
                        GUIElement element = get(i);
                        if (element.autoplace)
                        {
                            autoX = element.x + element.width;
                            autoY = element.y;

                            furthestX = Tools.max(furthestX, autoX);
                            furthestY = Tools.max(furthestY, autoY + element.height);
                        }
                    }
                }

                for (int i = startIndex; i < size(); i++)
                {
                    GUIElement element = get(i);
                    element.recalc();
                    if (element.autoplace)
                    {
                        if (autoX != 0 && autoX + element.width > 1)
                        {
                            element.x = 0;
                            element.y = furthestY;
                        }
                        else
                        {
                            element.x = autoX;
                            element.y = autoY;
                        }

                        autoX = element.x + element.width;
                        autoY = element.y;

                        furthestX = Tools.max(furthestX, autoX);
                        furthestY = Tools.max(furthestY, autoY + element.height);
                    }
                }
                break;

            case AP_CENTERED_H_TOP_TO_BOTTOM:
                if (size() <= 1 || startIndex != size() - 1)
                {
                    autoY = 0;
                    furthestY = 0;

                    for (int i = 0; i < startIndex; i++)
                    {
                        GUIElement element = get(i);
                        if (element.autoplace)
                        {
                            autoY = element.y;
                            furthestY = Tools.max(furthestY, autoY + element.height);
                        }
                    }
                }

                for (int i = startIndex; i < size(); i++)
                {
                    GUIElement element = get(i);
                    element.recalc();
                    if (element.autoplace)
                    {
                        element.x = 0.5 - element.width / 2;
                        element.y = furthestY;

                        autoY = element.y;
                        furthestY = Tools.max(furthestY, autoY + element.height);
                    }
                }
                break;

            case AP_X_0_TOP_TO_BOTTOM:
                if (size() <= 1 || startIndex != size() - 1)
                {
                    autoY = 0;
                    furthestY = 0;

                    for (int i = 0; i < startIndex; i++)
                    {
                        GUIElement element = get(i);
                        if (element.autoplace)
                        {
                            autoY = element.y;
                            furthestY = Tools.max(furthestY, autoY + element.height);
                        }
                    }
                }

                for (int i = startIndex; i < size(); i++)
                {
                    GUIElement element = get(i);
                    element.recalc();
                    if (element.autoplace)
                    {
                        element.x = 0;
                        element.y = furthestY;

                        autoY = element.y;
                        furthestY = Tools.max(furthestY, autoY + element.height);
                    }
                }
                break;

            //TODO add other AP types

            default:
                throw new IllegalArgumentException("Unimplemented autoplace type: " + subElementAutoplaceMethod);
        }
    }

    public void setSubElementAutoplaceMethod(byte subElementAutoplaceMethod)
    {
        this.subElementAutoplaceMethod = subElementAutoplaceMethod;
        recalc();
    }

    public void remove(GUIElement element)
    {
        int index = indexOf(element);
        if (element.parent == this) element.parent = null;
        children.remove(index);
        recalc(index);
    }

    public void remove(int index)
    {
        GUIElement element = children.remove(index);
        if (element.parent == this) element.parent = null;
        recalc(index);
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
