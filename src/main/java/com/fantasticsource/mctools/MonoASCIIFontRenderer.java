package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_LMAP_COLOR;
import static org.lwjgl.opengl.GL11.*;

public class MonoASCIIFontRenderer
{
    public static final int LINE_HEIGHT = CHAR_HEIGHT + 2;
    private static final int CHAR_WIDTH = 8, CHAR_HEIGHT = 8;
    private static final TextureManager TEXTURE_MANAGER = Minecraft.getMinecraft().getTextureManager();
    private static final double CHAR_UV = 1d / 16, CHAR_U_TO_RIGHT = CHAR_UV - CHAR_UV / (CHAR_WIDTH << 1), CHAR_V_TO_BOTTOM = CHAR_UV - CHAR_UV / (CHAR_HEIGHT << 1);
    private static final ResourceLocation TEXTURE = new ResourceLocation(FantasticLib.MODID, "image/monospace.png");

    public static void draw(String text, double x, double y, Color color, Color shadowColor)
    {
        if (shadowColor.a() <= 0 && color.a() <= 0) return;

        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableTexture2D();
        TEXTURE_MANAGER.bindTexture(TEXTURE);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL_QUADS, POSITION_TEX_LMAP_COLOR);

        if (shadowColor.a() > 0)
        {
            drawText(text, bufferBuilder, x, y + 1, shadowColor);
            drawText(text, bufferBuilder, x + 2, y + 1, shadowColor);
            drawText(text, bufferBuilder, x + 1, y, shadowColor);
            drawText(text, bufferBuilder, x + 1, y + 2, shadowColor);
        }

        if (color.a() > 0) drawText(text, bufferBuilder, x + 1, y + 1, color);

        tessellator.draw();
    }

    public static int getStringWidth(String text)
    {
        return text.length() * (CHAR_WIDTH + 2);
    }

    private static void drawText(String text, BufferBuilder bufferBuilder, double left, double top, Color color)
    {
        for (char c : text.toCharArray())
        {
            if (c < 256) drawChar(c, bufferBuilder, left, top, color.r(), color.g(), color.b(), color.a());
            left += CHAR_WIDTH + 2;
        }
    }

    private static void drawChar(char c, BufferBuilder bufferBuilder, double left, double top, int r, int g, int b, int a)
    {
        double u = (double) (c % 16) * CHAR_UV;
        double v = (double) (c >> 4) * CHAR_UV;

        bufferBuilder.pos(left, top, 0).tex(u, v).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
        bufferBuilder.pos(left, top + CHAR_HEIGHT, 0).tex(u, v + CHAR_V_TO_BOTTOM).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
        bufferBuilder.pos(left + CHAR_WIDTH, top + CHAR_HEIGHT, 0).tex(u + CHAR_U_TO_RIGHT, v + CHAR_V_TO_BOTTOM).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
        bufferBuilder.pos(left + CHAR_WIDTH, top, 0).tex(u + CHAR_U_TO_RIGHT, v).lightmap(15728880, 15728880).color(r, g, b, a).endVertex();
    }
}
