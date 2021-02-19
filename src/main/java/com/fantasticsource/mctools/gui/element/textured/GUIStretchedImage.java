package com.fantasticsource.mctools.gui.element.textured;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIStretchedImage extends GUIElement
{
    protected ResourceLocation texture;
    protected double u, v, uw, vh;
    protected Color color;
    protected boolean ignoreMCGUIScale = false;

    public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture)
    {
        this(screen, width, height, texture, Color.WHITE);
    }

    public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture, Color color)
    {
        this(screen, width, height, texture, color, 0, 0, 1, 1);
    }

    public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture, double u, double v, double uw, double vh)
    {
        this(screen, width, height, texture, Color.WHITE, u, v, uw, vh);
    }

    public GUIStretchedImage(GUIScreen screen, double width, double height, ResourceLocation texture, Color color, double u, double v, double uw, double vh)
    {
        super(screen, width, height);

        this.texture = texture;

        this.color = color;

        this.u = u;
        this.v = v;
        this.uw = uw;
        this.vh = vh;
    }


    public GUIStretchedImage(GUIScreen screen, ResourceLocation texture)
    {
        this(screen, texture, Color.WHITE);
    }

    public GUIStretchedImage(GUIScreen screen, ResourceLocation texture, Color color)
    {
        this(screen, 0, 0, 1, 1, texture, color);
    }

    public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture)
    {
        this(screen, x, y, width, height, texture, Color.WHITE);
    }

    public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture, Color color)
    {
        this(screen, x, y, width, height, texture, color, 0, 0, 1, 1);
    }

    public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture, double u, double v, double uw, double vh)
    {
        this(screen, x, y, width, height, texture, Color.WHITE, u, v, uw, vh);
    }

    public GUIStretchedImage(GUIScreen screen, double x, double y, double width, double height, ResourceLocation texture, Color color, double u, double v, double uw, double vh)
    {
        super(screen, x, y, width, height);

        this.texture = texture;

        this.color = color;

        this.u = u;
        this.v = v;
        this.uw = uw;
        this.vh = vh;
    }


    public void setColor(Color color)
    {
        this.color = color;
    }

    public Color getColor()
    {
        return color.copy();
    }


    @Override
    public void draw()
    {
        GlStateManager.enableTexture2D();
        GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());

        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);

        double u2 = u + uw, v2 = v + vh;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();

        builder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        builder.pos(1, 0, 0).tex(u2, v).endVertex();
        builder.pos(0, 0, 0).tex(u, v).endVertex();
        builder.pos(0, 1, 0).tex(u, v2).endVertex();
        builder.pos(1, 1, 0).tex(u2, v2).endVertex();
        tessellator.draw();

        GlStateManager.color(1, 1, 1, 1);


        drawChildren();
    }
}
