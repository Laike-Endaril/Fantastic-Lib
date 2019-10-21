package com.fantasticsource.mctools;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public class OutlinedFontRenderer
{
    public static final int LINE_HEIGHT = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT + 1;
    private static final FontRenderer FR = Minecraft.getMinecraft().fontRenderer;

    public static void draw(String text, float x, float y, Color color, Color shadowColor)
    {
        if (shadowColor.a() <= 0 && shadowColor.a() <= 0) return;

        int c = (shadowColor.color() >> 8) | ((shadowColor.color() & 0xFF) << 24);
        FR.drawString(text, x, y + 1, c, false);
        FR.drawString(text, x + 2, y + 1, c, false);
        FR.drawString(text, x + 1, y, c, false);
        FR.drawString(text, x + 1, y + 2, c, false);

        c = (color.color() >> 8) | ((color.color() & 0xFF) << 24);
        FR.drawString(text, x + 1, y + 1, c, false);
    }

    public static int getStringWidth(String text)
    {
        return FR.getStringWidth(text) + 1;
    }
}
