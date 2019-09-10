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

    @Override
    public void draw()
    {
        double screenWidth = screen.width, screenHeight = screen.height;

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

        double x1 = getScreenX(), y1 = getScreenY();
        double width = getScreenWidth(), height = getScreenHeight();
        double x2 = x1 + width, y2 = y1 + height;

        double pxWidth = width * screenWidth, pxHeight = height * screenHeight;
        double min = Tools.min(0.5, thickness) * Tools.min(pxWidth, pxHeight);
        double xThickness = min / screenWidth, yThickness = min / screenHeight;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);


        bufferbuilder.pos(x2 - xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();


        bufferbuilder.pos(x2 - xThickness, y1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();

        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x2, y1 + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x1 + xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1, y1 + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();


        //Mind the vertex ordering; not only the winding order, but the specific order of corners matters here to correctly interpolate the compound gradients for corners

        bufferbuilder.pos(x2, y1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2, y1 + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x1 + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x1, y1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1, y1 + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y1 + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x1 + xThickness, y1, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();


        tessellator.draw();

        super.draw();
    }
}
