package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIGradientBorder extends GUIRectElement
{
    private double thickness;
    private Color border, center, hoverBorder, hoverCenter, activeBorder, activeCenter;

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

        double x2 = x + width;
        double y2 = y + height;

        double min = Tools.min((x2 - x) * 0.5 * screenWidth, (y2 - y) * 0.5 * screenHeight, thickness * screenWidth, thickness * screenHeight);
        double xThickness = min / screenWidth;
        double yThickness = min / screenHeight;


        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);


        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();


        bufferbuilder.pos(x2 - xThickness, y, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();

        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x2, y + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x, y + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();


        //Mind the vertex ordering; not only the winding order, but the specific order of corners matters here to correctly interpolate the compound gradients for corners

        bufferbuilder.pos(x2, y, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2, y + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2, y2, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x2, y2 - yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();

        bufferbuilder.pos(x, y, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x, y + yThickness, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(c.r(), c.g(), c.b(), c.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y, 0).color(b.r(), b.g(), b.b(), b.a()).endVertex();


        tessellator.draw();

        super.draw();
    }
}
