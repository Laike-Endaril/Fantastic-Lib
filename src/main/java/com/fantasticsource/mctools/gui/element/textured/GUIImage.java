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

public class GUIImage extends GUIElement
{
    private ResourceLocation texture;
    private double unscaledWidth, unscaledHeight, u, v, uw, vh;
    private Color color;

    public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture)
    {
        this(screen, unscaledWidth, unscaledHeight, texture, Color.WHITE);
    }

    public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color)
    {
        this(screen, unscaledWidth, unscaledHeight, texture, color, 0, 0, 1, 1);
    }

    public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture, double u, double v, double uw, double vh)
    {
        this(screen, unscaledWidth, unscaledHeight, texture, Color.WHITE, u, v, uw, vh);
    }

    public GUIImage(GUIScreen screen, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color, double u, double v, double uw, double vh)
    {
        super(screen, 1, 1);

        this.unscaledWidth = unscaledWidth;
        this.unscaledHeight = unscaledHeight;

        this.texture = texture;

        this.color = color;

        this.u = u;
        this.v = v;
        this.uw = uw;
        this.vh = vh;
    }


    public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture)
    {
        this(screen, x, y, unscaledWidth, unscaledHeight, texture, Color.WHITE);
    }

    public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color)
    {
        this(screen, x, y, unscaledWidth, unscaledHeight, texture, color, 0, 0, 1, 1);
    }

    public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture, double u, double v, double uw, double vh)
    {
        this(screen, x, y, unscaledWidth, unscaledHeight, texture, Color.WHITE, u, v, uw, vh);
    }

    public GUIImage(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ResourceLocation texture, Color color, double u, double v, double uw, double vh)
    {
        super(screen, x, y, 1, 1);

        this.unscaledWidth = unscaledWidth;
        this.unscaledHeight = unscaledHeight;

        this.texture = texture;

        this.color = color;

        this.u = u;
        this.v = v;
        this.uw = uw;
        this.vh = vh;
    }


    @Override
    public GUIImage recalc(int subIndexChanged)
    {
        width = unscaledWidth / screen.width;
        height = unscaledHeight / screen.height;

        //TODO this line is cancelling a scissor offset issue of unknown origin; offset = 1 - ()
        //TODO I might've fixed the root issue and not need this line?
//        width += (1 - scaledWidth) / screen.width;

        if (parent != null)
        {
            width /= parent.absoluteWidth();
            height /= parent.absoluteHeight();
        }

        recalcAndRepositionSubElements(0);

        postRecalc();

        return this;
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
