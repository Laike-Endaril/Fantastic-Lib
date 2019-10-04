package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIGradientBorder extends GUIElement
{
    public Color border, center, hoverBorder, hoverCenter, activeBorder, activeCenter;
    protected double thickness;

    public GUIGradientBorder(GUIScreen screen, double width, double height, double borderThickness, Color border, Color center)
    {
        this(screen, width, height, borderThickness, border, center, border, center, border, center);
    }

    public GUIGradientBorder(GUIScreen screen, double width, double height, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, width, height);
        this.thickness = borderThickness;

        this.border = border;
        this.center = center;
        this.hoverBorder = hoverBorder;
        this.hoverCenter = hoverCenter;
        this.activeBorder = activeBorder;
        this.activeCenter = activeCenter;
    }

    public GUIGradientBorder(GUIScreen screen, double x, double y, double width, double height, double borderThickness, Color border, Color center)
    {
        this(screen, x, y, width, height, borderThickness, border, center, border, center, border, center);
    }

    public GUIGradientBorder(GUIScreen screen, double x, double y, double width, double height, double borderThickness, Color border, Color center, Color hoverBorder, Color hoverCenter, Color activeBorder, Color activeCenter)
    {
        super(screen, x, y, width, height);
        this.thickness = borderThickness;

        this.border = border;
        this.center = center;
        this.hoverBorder = hoverBorder;
        this.hoverCenter = hoverCenter;
        this.activeBorder = activeBorder;
        this.activeCenter = activeCenter;
    }

    public GUIGradientBorder setThickness(double thickness)
    {
        this.thickness = thickness;
        return this;
    }

    @Override
    public void draw()
    {
        Color b, c;
        if (active)
        {
            b = activeBorder;
            c = activeCenter;
        }
        else if (isMouseWithin())
        {
            b = hoverBorder;
            c = hoverCenter;
        }
        else
        {
            b = border;
            c = center;
        }

        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();

        double min = Tools.min(0.5, thickness) * Tools.min(absolutePxWidth(), absolutePxHeight());
        double xThickness = min / screen.pxWidth, yThickness = min / screen.pxHeight;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);


        bufferbuilder.pos(1 - xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();


        bufferbuilder.pos(1 - xThickness, 0, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(xThickness, 0, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();

        bufferbuilder.pos(1 - xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(xThickness, 1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, 1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(1, yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(1, 1 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(0, yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(0, 1 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();


        //Mind the vertex ordering; not only the winding order, but the specific order of corners matters here to correctly interpolate the compound gradients for corners

        bufferbuilder.pos(1, 0, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, 0, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(1, yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(0, 1 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(0, 1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(xThickness, 1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(1 - xThickness, 1 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(1 - xThickness, 1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(1, 1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(1, 1 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(0, 0, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(0, yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(xThickness, yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(xThickness, 0, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();


        tessellator.draw();


        drawChildren();
    }
}
