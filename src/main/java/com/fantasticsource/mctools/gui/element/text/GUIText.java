package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static com.fantasticsource.mctools.gui.GUIScreen.*;

public class GUIText extends GUIElement
{
    public String text;
    protected Color color, hoverColor, activeColor;

    public GUIText(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, getColor(color), getHover(color), color);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, x, y, (double) (FONT_RENDERER.getStringWidth(text) - 1) / screen.width, (double) (FONT_RENDERER.FONT_HEIGHT - 1) / screen.height);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
    }

    @Override
    public GUIElement recalc()
    {
        width = (double) (parent instanceof MultilineTextInput ? MonoASCIIFontRenderer.getStringWidth(text) : FONT_RENDERER.getStringWidth(text) - 1) / screen.width;
        height = (double) (parent instanceof MultilineTextInput ? MonoASCIIFontRenderer.LINE_HEIGHT + 2 : FONT_RENDERER.FONT_HEIGHT - 1) / screen.height;

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
