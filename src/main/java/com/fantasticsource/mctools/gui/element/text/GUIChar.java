package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUIChar extends GUIElement
{
    public char value;

    public GUIChar(GUIScreen screen, char value)
    {
        super(screen, 0, 0);
        this.value = value;
    }

    public GUIChar(GUIScreen screen, double x, double y, char value)
    {
        super(screen, x, y, 0, 0);
        this.value = value;
    }

    @Override
    public GUIElement recalc()
    {
        super.recalc();

        width = (double) FONT_RENDERER.getCharWidth(value) / screen.width;
        height = (double) FONT_RENDERER.FONT_HEIGHT / screen.height;

        return this;
    }

    @Override
    public void draw()
    {
        super.draw();

        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(absoluteX(), absoluteY(), 0);
        GlStateManager.scale(1d / screen.width, 1d / screen.height, 1);

        Color c = Color.WHITE;
        FONT_RENDERER.drawString("" + value, 0, 0, (c.color() >> 8) | c.a() << 24, false);

        GlStateManager.popMatrix();
    }

}
