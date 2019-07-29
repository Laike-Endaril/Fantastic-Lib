package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class GUITextRect extends GUIRectElement
{
    protected static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    protected String text;
    protected Color color, hoverColor, activeColor;

    public GUITextRect(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, x, y, (double) screen.mc.fontRenderer.getStringWidth(text) / screen.width, (double) screen.mc.fontRenderer.FONT_HEIGHT / screen.height);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
    }

    @Override
    public void recalc()
    {
        width = (double) screen.mc.fontRenderer.getStringWidth(text) / screen.width;
        height = (double) screen.mc.fontRenderer.FONT_HEIGHT / screen.height;
        super.recalc();
    }

    @Override
    public void draw()
    {
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.scale(1d / screen.width, 1d / screen.height, 1);

        Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
        fontRenderer.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);

        GlStateManager.popMatrix();

        super.draw();
    }

    @Override
    public String toString()
    {
        return text;
    }
}
