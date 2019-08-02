package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUITextRect extends GUIRectElement
{
    protected String text;
    protected Color color, hoverColor, activeColor;

    public GUITextRect(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, x, y, (double) (screen.mc.fontRenderer.getStringWidth(text) - 1) / screen.width, (double) (screen.mc.fontRenderer.FONT_HEIGHT - 1) / screen.height);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
    }

    @Override
    public GUIElement recalc()
    {
        width = (double) (screen.mc.fontRenderer.getStringWidth(text) - 1) / screen.width;
        height = (double) (screen.mc.fontRenderer.FONT_HEIGHT - 1) / screen.height;

        return super.recalc();
    }

    @Override
    public void draw()
    {
        super.draw();

        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.scale(1d / screen.width, 1d / screen.height, 1);

        Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
        FONT_RENDERER.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);

        GlStateManager.popMatrix();
    }

    @Override
    public String toString()
    {
        return text;
    }

    @Override
    public double getScreenWidth()
    {
        return width;
    }

    @Override
    public double getScreenHeight()
    {
        return height;
    }
}
