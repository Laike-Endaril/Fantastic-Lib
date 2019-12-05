package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
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

    public GUIImage(GUIScreen screen, ResourceLocation texture, double unscaledWidth, double unscaledHeight)
    {
        this(screen, texture, unscaledWidth, unscaledHeight, 0, 0, 1, 1);
    }

    public GUIImage(GUIScreen screen, ResourceLocation texture, double unscaledWidth, double unscaledHeight, double u, double v, double uw, double vh)
    {
        super(screen, 1, 1);

        this.texture = texture;

        this.unscaledWidth = unscaledWidth * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        this.unscaledHeight = unscaledHeight * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

        this.u = u;
        this.v = v;
        this.uw = uw;
        this.vh = vh;
    }


    public GUIImage(GUIScreen screen, double x, double y, ResourceLocation texture, double unscaledWidth, double unscaledHeight)
    {
        this(screen, x, y, texture, unscaledWidth, unscaledHeight, 0, 0, 1, 1);
    }

    public GUIImage(GUIScreen screen, double x, double y, ResourceLocation texture, double unscaledWidth, double unscaledHeight, double u, double v, double uw, double vh)
    {
        super(screen, x, y, 1, 1);

        this.texture = texture;

        this.unscaledWidth = unscaledWidth * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        this.unscaledHeight = unscaledHeight * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

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
//        width += (1 - unscaledWidth) / screen.width;

        if (parent != null)
        {
            width /= parent.absoluteWidth();
            height /= parent.absoluteHeight();
        }

        recalcAndRepositionSubElements(0);

        postRecalc();

        return this;
    }


    @Override
    public void draw()
    {
        GlStateManager.enableTexture2D();

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


        drawChildren();
    }
}
