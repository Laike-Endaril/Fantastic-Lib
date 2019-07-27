package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;

public class GUITextRect extends GUIRectElement
{
    private static FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;

    private String text;
    private Color color, hoverColor, pressedColor;

    public GUITextRect(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color pressedColor)
    {
        this(screen, x, y, screen.mc.fontRenderer.getStringWidth(text), text, color, hoverColor, pressedColor);
    }

    public GUITextRect(GUIScreen screen, double x, double y, double width, String text, Color color, Color hoverColor, Color pressedColor)
    {
        super(screen, x, y, width, 0);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;

        recalcHeight(screen.width, screen.height);
        active = false;
    }

    public void recalcHeight(double width, double screenHeight)
    {
        height = (double) fontRenderer.getWordWrappedHeight(text, (int) width) / screenHeight;
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        return super.mousePressed(x, y, button);
    }

    @Override
    public void draw(double screenWidth, double screenHeight)
    {
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.scale(1 / screenWidth, 1 / screenHeight, 1);

        Color c = !isMouseWithin() ? color : active ? pressedColor : hoverColor;
        fontRenderer.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);

        GlStateManager.popMatrix();
    }

    @Override
    public String toString()
    {
        return text;
    }
}
