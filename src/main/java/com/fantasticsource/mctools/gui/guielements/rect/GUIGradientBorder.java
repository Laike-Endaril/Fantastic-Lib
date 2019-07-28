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
    private Color border, center;

    public GUIGradientBorder(GUIScreen screen, double x, double y, double width, double height, double borderThickness, Color border, Color center)
    {
        super(screen, x, y, width, height);
        this.thickness = borderThickness;
        this.border = border;
        this.center = center;
    }

    @Override
    public void draw()
    {
        double screenWidth = screen.width, screenHeight = screen.height;

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


        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();


        bufferbuilder.pos(x2 - xThickness, y, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();

        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(x2, y + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x2, y2 - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x, y + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x, y2 - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();


        //Mind the vertex ordering; not only the winding order, but the specific order of corners matters here to correctly interpolate the compound gradients for corners

        bufferbuilder.pos(x2, y, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x2, y + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(x + xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x, y2 - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x, y2, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y2, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(x2 - xThickness, y2 - yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x2 - xThickness, y2, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x2, y2, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x2, y2 - yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();

        bufferbuilder.pos(x, y, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x, y + yThickness, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y + yThickness, 0).color(center.r(), center.g(), center.b(), center.a()).endVertex();
        bufferbuilder.pos(x + xThickness, y, 0).color(border.r(), border.g(), border.b(), border.a()).endVertex();


        tessellator.draw();

        super.draw();
    }
}
