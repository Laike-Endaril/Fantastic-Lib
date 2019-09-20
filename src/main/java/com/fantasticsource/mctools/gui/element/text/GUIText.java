package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUIText extends GUIElement
{
    public String text;
    protected ArrayList<String> lines = new ArrayList<>();
    protected Color color, hoverColor, activeColor;

    public GUIText(GUIScreen screen, String text)
    {
        this(screen, text, Color.WHITE);
    }

    public GUIText(GUIScreen screen, String text, Color color)
    {
        this(screen, text, color, color, color);
    }

    public GUIText(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, 0, 0);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        recalc();
    }

    public GUIText(GUIScreen screen, double x, double y, String text)
    {
        this(screen, x, y, text, Color.WHITE);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, color, color, color);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, x, y, 0, 0);
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        recalc();
    }


    public GUIText setColor(Color color)
    {
        return setColor(color, color, color);
    }

    public GUIText setColor(Color color, Color hoverColor, Color activeColor)
    {
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        return this;
    }


    @Override
    public GUIElement recalc()
    {
        lines.clear();
        if (parent instanceof MultilineTextInput)
        {
            lines.add(text);
            width = (double) MonoASCIIFontRenderer.getStringWidth(text) / screen.width;
            height = (double) (MonoASCIIFontRenderer.LINE_HEIGHT + 2) / screen.height;
        }
        else
        {
            String[] words = Tools.preservedSplit(text, "[\r\n]|[ ]+", true);

            double parentW = parent == null ? 1 : parent.getScreenWidth();

            StringBuilder line = new StringBuilder();
            int index = 0;
            double maxLineW = 0, lineW = -1d / screen.width;
            while (index < words.length)
            {
                String word = words[index++];

                if (word.equals("") || word.equals("\r")) continue;

                if (word.equals("\n"))
                {
                    lines.add(line.toString());
                    line = new StringBuilder();

                    maxLineW = 1;
                    lineW = -1d / screen.width;
                }
                else
                {
                    double wordW = (double) FONT_RENDERER.getStringWidth(word) / screen.width;

                    if (lineW + wordW > parentW)
                    {
                        if (word.trim().equals("")) continue;

                        if (lines.size() == 0 && line.length() == 0)
                        {
                            line.append(word);
                            lineW += wordW;
                        }
                        else
                        {
                            lines.add(line.toString());
                            line = new StringBuilder(word);

                            maxLineW = parentW;
                            lineW = (double) (FONT_RENDERER.getStringWidth(word) - 1) / screen.width;
                        }
                    }
                    else
                    {
                        line.append(word);
                        lineW += wordW;
                    }
                }
            }

            if (line.length() > 0)
            {
                lines.add(line.toString());
                maxLineW = Tools.max(maxLineW, lineW);
            }

            width = maxLineW;
            height = (double) (lines.size() * FONT_RENDERER.FONT_HEIGHT - 1) / screen.height;
        }

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
        int yy = 0;
        for (String line : lines)
        {
            FONT_RENDERER.drawString(line, 0, yy, (c.color() >> 8) | c.a() << 24, false);
            yy += FONT_RENDERER.FONT_HEIGHT;
        }

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
