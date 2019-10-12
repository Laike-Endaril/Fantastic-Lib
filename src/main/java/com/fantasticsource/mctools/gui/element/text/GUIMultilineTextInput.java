package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUIMultilineTextInput extends GUITextInput
{
    public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter)
    {
        super(screen, text, filter);
    }

    public GUIMultilineTextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor)
    {
        super(screen, text, filter, activeColor);
    }

    public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter)
    {
        super(screen, x, y, text, filter);
    }

    public GUIMultilineTextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor)
    {
        super(screen, x, y, text, filter, activeColor);
    }

    @Override
    public void draw()
    {
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        double scale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GlStateManager.scale(scale / absolutePxWidth(), scale / absolutePxHeight(), 1);

        Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
        int yy = 0;
        for (String line : lines)
        {
            FONT_RENDERER.drawString(line, 0, yy, (c.color() >> 8) | c.a() << 24, false);
            yy += FONT_RENDERER.FONT_HEIGHT;
        }

        GlStateManager.popMatrix();


        drawChildren();
    }
}
