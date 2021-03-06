package com.fantasticsource.mctools.gui.element;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUILine;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.mctools.gui.element.view.GUITooltipView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class GUIElement
{
    public static final Color T_GRAY = new Color(0xAAAAAA77);

    public static final byte
            AP_CENTER = -1,
            AP_LEFT_TO_RIGHT_TOP_TO_BOTTOM = 0,
    //            AP_RIGHT_TO_LEFT_TOP_TO_BOTTOM = 1,
//            AP_LEFT_TO_RIGHT_BOTTOM_TO_TOP = 2,
//            AP_RIGHT_TO_LEFT_BOTTOM_TO_TOP = 3,
//            AP_TOP_TO_BOTTOM_LEFT_TO_RIGHT = 4,
//            AP_TOP_TO_BOTTOM_RIGHT_TO_LEFT = 5,
//            AP_BOTTOM_TO_TOP_LEFT_TO_RIGHT = 6,
//            AP_BOTTOM_TO_TOP_RIGHT_TO_LEFT = 7,
    AP_CENTERED_H_TOP_TO_BOTTOM = 8,
    //            AP_CENTERED_V_LEFT_TO_RIGHT = 9,
    AP_X_0_TOP_TO_BOTTOM = 10,
            AP_Y_0_LEFT_TO_RIGHT = 11;

    public final ArrayList<Runnable>
            onClickActions = new ArrayList<>(),
            onRecalcActions = new ArrayList<>(),
            onEditActions = new ArrayList<>();

    public final ArrayList<Predicate<GUIElement>>
            onRemoveChildActions = new ArrayList<>();

    public double x, y, width, height;
    public GUIElement parent = null;
    public ArrayList<GUIElement> children = new ArrayList<>();
    public Object tooltip = null;
    public boolean autoplace = false;
    protected double autoX = 0, autoY = 0, furthestX = 0, furthestY = 0;
    protected byte subElementAutoplaceMethod;
    public GUIScreen screen;
    protected boolean active = false, externalDeactivation = false, useParentScissor = false;
    private ArrayList<GUIElement> linkedMouseActivity = new ArrayList<>();
    private ArrayList<GUIElement> linkedMouseActivityReverse = new ArrayList<>();
    protected double dragStartX, dragStartY;


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


    public GUIElement useParentScissor(boolean useParentScissor)
    {
        this.useParentScissor = useParentScissor;
        return this;
    }


    public GUIElement setTooltip(String tooltip)
    {
        if (tooltip == null || tooltip.trim().equals(""))
        {
            this.tooltip = null;
            return this;
        }

        if (!(this.tooltip instanceof GUITooltipView)) this.tooltip = new GUITooltipView(screen);
        ((GUITooltipView) this.tooltip).setTooltip(tooltip);
        return this;
    }

    public GUIElement setTooltip(ItemStack stack)
    {
        tooltip = stack;
        return this;
    }

    public boolean isWithin(double x, double y)
    {
        double xx = absoluteX(), yy = absoluteY();
        return xx <= x && x < xx + absoluteWidth() && yy <= y && y < yy + absoluteHeight();
    }

    private int[] preDraw()
    {
        //Reset state
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.disableDepth();

        //Matrix
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, 0);
        GlStateManager.scale(width, height, 1);

        //Scissor setup
        int[] lastScissor = new int[4];
        System.arraycopy(GUIScreen.currentScissor, 0, lastScissor, 0, 4);

        if (!useParentScissor)
        {
            if (this instanceof GUILine)
            {
                GUIScreen.currentScissor[0] = (int) Tools.max(GUIScreen.currentScissor[0], absolutePxX() - ((GUILine) this).thickness);
                GUIScreen.currentScissor[1] = (int) Tools.max(GUIScreen.currentScissor[1], absolutePxY() - ((GUILine) this).thickness);
                GUIScreen.currentScissor[2] = (int) Tools.min(GUIScreen.currentScissor[2], absolutePxX() + absolutePxWidth() + ((GUILine) this).thickness * 2);
                GUIScreen.currentScissor[3] = (int) Tools.min(GUIScreen.currentScissor[3], absolutePxY() + absolutePxHeight() + ((GUILine) this).thickness * 2);
            }
            else
            {
                GUIScreen.currentScissor[0] = Tools.max(GUIScreen.currentScissor[0], absolutePxX());
                GUIScreen.currentScissor[1] = Tools.max(GUIScreen.currentScissor[1], absolutePxY());
                GUIScreen.currentScissor[2] = Tools.min(GUIScreen.currentScissor[2], absolutePxX() + absolutePxWidth());
                GUIScreen.currentScissor[3] = Tools.min(GUIScreen.currentScissor[3], absolutePxY() + absolutePxHeight());
            }
        }


        //Attempt scissor
        if (!screen.scissor())
        {
            GUIScreen.currentScissor = lastScissor;

            GlStateManager.popMatrix();
            return null;
        }
        return lastScissor;
    }

    protected final void drawChildren()
    {
        if (children.size() > 0 && width > 0 && height > 0)
        {
            for (GUIElement element : children.toArray(new GUIElement[0]))
            {
                element.tick();

                int[] lastScissor = element.preDraw();
                if (lastScissor != null)
                {
                    element.draw();
                    element.postDraw(lastScissor);
                }
            }
        }

        if (isMouseWithin())
        {
            if (tooltip instanceof GUITooltipView) screen.tooltips.add((GUITooltipView) tooltip);
            else if (tooltip instanceof ItemStack) screen.tooltipStack = (ItemStack) tooltip;
        }
    }

    private void postDraw(int[] lastScissor)
    {
        //Undo scissor
        GUIScreen.currentScissor = lastScissor;

        //Undo matrix
        GlStateManager.popMatrix();
    }

    protected void tick()
    {
    }

    public void draw()
    {
        drawChildren();
    }

    public void mouseWheel(int delta)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseWheel(delta);
    }

    public boolean mousePressed(int button)
    {
        dragStartX = x;
        dragStartY = y;

        boolean result = false;
        if (button == 0 && isMouseWithin())
        {
            result = true;
            setActive(true);
        }

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) result |= child.mousePressed(button);

        return result;
    }

    public boolean mouseReleased(int button)
    {
        boolean result = false;
        if (button == 0)
        {
            if (active && isMouseWithin() && x == dragStartX && y == dragStartY)
            {
                if (!MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this))) click();
                result = true;
            }
            setActive(false);
        }

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseReleased(button);

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

    public GUIElement addEditActions(Runnable... actions)
    {
        onEditActions.addAll(Arrays.asList(actions));
        return this;
    }

    public GUIElement addRemoveChildActions(Predicate<GUIElement>... actions)
    {
        onRemoveChildActions.addAll(Arrays.asList(actions));
        return this;
    }

    public void mouseDrag(int button)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseDrag(button);
    }


    public final double absoluteX()
    {
        if (parent == null) return x;

        if (parent instanceof GUIPanZoomView) return parent.absoluteX() + (x - ((GUIPanZoomView) parent).viewX) * ((GUIPanZoomView) parent).getZoom() * parent.absoluteWidth();

        return parent.absoluteX() + x * parent.absoluteWidth();
    }

    public final double absoluteY()
    {
        if (parent == null) return y;

        if (parent instanceof GUIPanZoomView) return parent.absoluteY() + (y - ((GUIPanZoomView) parent).viewY) * ((GUIPanZoomView) parent).getZoom() * parent.absoluteHeight();

        if (parent instanceof GUIScrollView) return parent.absoluteY() + (y - ((GUIScrollView) parent).top) * parent.absoluteHeight();

        return parent.absoluteY() + y * parent.absoluteHeight();
    }

    public final GUIElement setAbsoluteX(double absX)
    {
        if (parent == null)
        {
            x = absX;
        }
        else if (parent instanceof GUIPanZoomView)
        {
            x = (absX - parent.absoluteX()) / ((GUIPanZoomView) parent).getZoom() / parent.absoluteWidth() + ((GUIPanZoomView) parent).viewX;
        }
        else
        {
            x = (absX - parent.absoluteX()) / parent.absoluteWidth();
        }

        return this;
    }

    public final GUIElement setAbsoluteY(double absY)
    {
        if (parent == null)
        {
            y = absY;
        }
        else if (parent instanceof GUIPanZoomView)
        {
            y = (absY - parent.absoluteY()) / ((GUIPanZoomView) parent).getZoom() / parent.absoluteHeight() + ((GUIPanZoomView) parent).viewY;
        }
        else if (parent instanceof GUIScrollView)
        {
            y = (absY - parent.absoluteY()) / parent.absoluteHeight() + ((GUIScrollView) parent).top;
        }
        else
        {
            y = (absY - parent.absoluteY()) / parent.absoluteHeight();
        }

        return this;
    }

    public final double absoluteWidth()
    {
        if (parent == null) return width;

        if (parent instanceof GUIPanZoomView) return parent.absoluteWidth() * width * ((GUIPanZoomView) parent).getZoom();

        return parent.absoluteWidth() * width;
    }

    public final double absoluteHeight()
    {
        if (parent == null) return height;

        if (parent instanceof GUIPanZoomView) return parent.absoluteHeight() * height * ((GUIPanZoomView) parent).getZoom();

        return parent.absoluteHeight() * height;
    }


    public final int absolutePxX()
    {
        return (int) Math.round(absoluteX() * screen.pxWidth);
    }

    public final int absolutePxY()
    {
        return (int) Math.round(absoluteY() * screen.pxHeight);
    }

    public final int absolutePxWidth()
    {
        return (int) Math.round(absoluteWidth() * screen.pxWidth);
    }

    public final int absolutePxHeight()
    {
        return (int) Math.round(absoluteHeight() * screen.pxHeight);
    }


    public final double mouseX()
    {
        return GUIScreen.mouseX;
    }

    public final double mouseY()
    {
        return GUIScreen.mouseY;
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

    public GUIElement recalc(int subIndexChanged)
    {
        recalcAndRepositionSubElements(subIndexChanged);

        postRecalc();

        return this;
    }

    public final void postRecalc()
    {
        for (Runnable action : onRecalcActions) action.run();
    }

    public GUIElement addAll(GUIElement... elements)
    {
        for (GUIElement element : elements) add(element);

        return this;
    }

    public GUIElement add(GUIElement element)
    {
        return add(children.size(), element);
    }

    public GUIElement add(int index, GUIElement element)
    {
        element.parent = this;
        children.add(index, element);
        switch (subElementAutoplaceMethod)
        {
            case AP_CENTER:
                recalc(0);
                break;

            default:
                recalc(index);
        }
        return element;
    }

    public void recalcAndRepositionSubElements(int startIndex)
    {
        double yy = 0;
        switch (subElementAutoplaceMethod)
        {
            case AP_CENTER:
                for (GUIElement element : children)
                {
                    element.recalc(0);
                    if (element.autoplace)
                    {
                        element.x = 0.5 - element.width * 0.5;
                        element.y = yy;
                        yy = Tools.max(yy, element.y + element.height);
                    }
                }

                yy *= 0.5;
                for (GUIElement element : children)
                {
                    if (element.autoplace) element.y += 0.5 - yy;
                }
                break;


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
                    element.recalc(0);
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
                    element.recalc(0);
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
                    element.recalc(0);
                    if (element.autoplace)
                    {
                        element.x = 0;
                        element.y = furthestY;

                        autoY = element.y;
                        furthestY = Tools.max(furthestY, autoY + element.height);
                    }
                }
                break;


            case AP_Y_0_LEFT_TO_RIGHT:
                if (size() <= 1 || startIndex != size() - 1)
                {
                    autoX = 0;
                    furthestX = 0;

                    for (int i = 0; i < startIndex; i++)
                    {
                        GUIElement element = get(i);
                        if (element.autoplace)
                        {
                            autoX = element.x;
                            furthestX = Tools.max(furthestX, autoX + element.width);
                        }
                    }
                }

                for (int i = startIndex; i < size(); i++)
                {
                    GUIElement element = get(i);
                    element.recalc(0);
                    if (element.autoplace)
                    {
                        element.x = furthestX;
                        element.y = 0;

                        autoX = element.x;
                        furthestX = Tools.max(furthestX, autoX + element.width);
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
        recalc(0);
    }

    public void remove(GUIElement element)
    {
        int index = indexOf(element);
        if (index != -1) remove(index);
        else if (element.parent == this) element.parent = null;
    }

    public void remove(int index)
    {
        GUIElement element = children.remove(index);
        if (element != null)
        {
            if (element.parent == this) element.parent = null;

            recalc(0);

            for (Predicate<GUIElement> action : onRemoveChildActions) action.test(element);

            recalc(0);
        }
    }

    public int size()
    {
        return children.size();
    }

    public void clear()
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) remove(child);
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

    public void setActive(boolean active)
    {
        setActive(active, false);
    }

    public void setActive(boolean active, boolean external)
    {
        if (!active && externalDeactivation && !external) return;

        this.active = active;
        for (GUIElement element : linkedMouseActivity) element.active = active;
    }

    public void setActiveRecursive(boolean active)
    {
        setActive(active, true);
        for (GUIElement child : children) child.setActiveRecursive(active);
    }

    public boolean isActive()
    {
        return active;
    }

    public void keyTyped(char typedChar, int keyCode)
    {
        for (GUIElement child : (ArrayList<GUIElement>) children.clone())
        {
            child.keyTyped(typedChar, keyCode);
        }
    }
}
