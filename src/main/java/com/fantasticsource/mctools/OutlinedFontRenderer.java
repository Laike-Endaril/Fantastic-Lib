package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
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

        x += 1;
        y += 1;

        int c = (shadowColor.color() >>> 8) | ((shadowColor.color() & 0xFF) << 24);
        if ((c & -67108864) != 0)
        {
            FR.drawString(text, x - 1, y, c, false);
            FR.drawString(text, x + 1, y, c, false);
            FR.drawString(text, x, y - 1, c, false);
            FR.drawString(text, x, y + 1, c, false);

            if (Compat.smoothfont)
            {
                FR.drawString(text, x + 0.5f, y + 0.5f, c, false);
                FR.drawString(text, x + 0.5f, y - 0.5f, c, false);
                FR.drawString(text, x - 0.5f, y + 0.5f, c, false);
                FR.drawString(text, x - 0.5f, y - 0.5f, c, false);
            }
        }

        c = (color.color() >>> 8) | ((color.color() & 0xFF) << 24);
        if ((c & -67108864) != 0) FR.drawString(text, x, y, c, false);
    }

    public static int getStringWidth(String text)
    {
        return FR.getStringWidth(text) + 1;
    }
}
