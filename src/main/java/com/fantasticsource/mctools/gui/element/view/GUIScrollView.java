package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

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

        recalc();
    }

    public GUIScrollView(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height);

        for (GUIElement element : subElements)
        {
            children.add(element);
            element.parent = this;
        }

        recalc();
    }

    @Override
    public GUIElement recalc()
    {
        internalHeight = 0;
        super.recalc(0);
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
        return recalc();
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

        if (children.size() > 0 && width > 0 && height > 0)
        {
            double screenWidth = screen.width, screenHeight = screen.height;

            int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
            double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;

            currentScissor = new int[]{(int) (absoluteX() * wScale), (int) ((1 - (absoluteY() + absoluteHeight())) * hScale), (int) (absoluteWidth() * wScale), (int) (absoluteHeight() * hScale)};
            if (parent != null)
            {
                currentScissor[0] = Tools.max(currentScissor[0], parent.currentScissor[0]);
                currentScissor[1] = Tools.max(currentScissor[1], parent.currentScissor[1]);
                currentScissor[2] = Tools.min(currentScissor[2], parent.currentScissor[2]);
                currentScissor[3] = Tools.min(currentScissor[3], parent.currentScissor[3]);
            }
            else GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(currentScissor[0], currentScissor[1], currentScissor[2], currentScissor[3]);

            GlStateManager.pushMatrix();
            GlStateManager.translate(0, -childMouseYOffset(), 0);

            for (GUIElement element : children)
            {
                if (element.y + element.height < top || element.y >= bottom) continue;
                element.draw();
            }

            GlStateManager.popMatrix();

            currentScissor = null;
            if (parent == null) GL11.glDisable(GL11.GL_SCISSOR_TEST);
            else GL11.glScissor(parent.currentScissor[0], parent.currentScissor[1], parent.currentScissor[2], parent.currentScissor[3]);
        }
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        recalc2();
        y -= childMouseYOffset();

        return super.mousePressed(x, y, button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button)
    {
        recalc2();
        y -= childMouseYOffset();

        return super.mouseReleased(x, y, button);
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        recalc2();
        y -= childMouseYOffset();

        super.mouseDrag(x, y, button);
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        recalc2();
        y -= childMouseYOffset();

        super.mouseWheel(x, y, delta);
    }

    @Override
    public double childMouseYOffset()
    {
        return top * absoluteHeight();
    }
}
