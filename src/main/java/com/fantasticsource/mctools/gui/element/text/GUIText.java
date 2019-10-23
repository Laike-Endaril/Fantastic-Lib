package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import java.util.ArrayList;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUIText extends GUIElement
{
    public final double scale;
    public String text;
    protected ArrayList<String> lines = new ArrayList<>(), fullLines = new ArrayList<>();
    protected Color color, hoverColor, activeColor;


    public GUIText(GUIScreen screen, String text)
    {
        this(screen, text, Color.WHITE, 1);
    }

    public GUIText(GUIScreen screen, String text, double scale)
    {
        this(screen, text, Color.WHITE, scale);
    }

    public GUIText(GUIScreen screen, String text, Color color)
    {
        this(screen, text, color, color, color, 1);
    }

    public GUIText(GUIScreen screen, String text, Color color, double scale)
    {
        this(screen, text, color, color, color, scale);
    }

    public GUIText(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor)
    {
        this(screen, text, color, hoverColor, activeColor, 1);
    }

    public GUIText(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, double scale)
    {
        super(screen, 0, 0);
        this.scale = scale * screen.textScale;
        this.text = text;
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
        recalc();
    }


    public GUIText(GUIScreen screen, double x, double y, String text)
    {
        this(screen, x, y, text, Color.WHITE, 1);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, double scale)
    {
        this(screen, x, y, text, Color.WHITE, scale);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color)
    {
        this(screen, x, y, text, color, color, color, 1);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color, double scale)
    {
        this(screen, x, y, text, color, color, color, scale);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor)
    {
        this(screen, x, y, text, color, hoverColor, activeColor, 1);
    }

    public GUIText(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, double scale)
    {
        super(screen, x, y, 0, 0);
        this.scale = scale * screen.textScale;
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
    public GUIText recalc(int subIndexChanged)
    {
        text = text.replaceAll("\r", "");

        lines.clear();
        fullLines.clear();

        if (parent instanceof CodeInput)
        {
            lines.add(text);
            fullLines.add(text);

            width = (double) MonoASCIIFontRenderer.getStringWidth(text) * scale / screen.width;
            height = (double) (MonoASCIIFontRenderer.LINE_HEIGHT + 2) * scale / screen.height;
        }
        else
        {
            StringBuilder previous = new StringBuilder();

            String[] words = Tools.preservedSplit(text, "[\n]|[ ]+", true);

            double parentW = parent == null ? 1 : parent.absoluteWidth();

            StringBuilder line = new StringBuilder();
            StringBuilder fullLine = new StringBuilder();

            int index = 0;
            double maxLineW = 0, lineW = -1d / screen.width;
            while (index < words.length)
            {
                String word = words[index++];

                if (word.equals("")) continue;

                if (word.equals("\n"))
                {
                    lines.add(line.toString());
                    fullLines.add(fullLine.toString());

                    line = new StringBuilder();
                    fullLine = new StringBuilder("\n");

                    maxLineW = 1;
                    lineW = -1d / screen.width;
                }
                else
                {
                    double wordW = (double) Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) * scale / screen.width;

                    if (lineW + wordW > parentW)
                    {
                        if (word.trim().equals(""))
                        {
                            fullLine.append(word);
                            continue;
                        }

                        if (line.length() == 0)
                        {
                            line.append(word);
                            fullLine.append(word);

                            lineW += wordW;
                        }
                        else
                        {
                            lines.add(line.toString());
                            fullLines.add(fullLine.toString());

                            line = new StringBuilder(word);
                            fullLine = new StringBuilder(word);

                            maxLineW = parentW;
                            lineW = (double) (Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) - 1) * scale / screen.width;
                        }
                    }
                    else
                    {
                        line.append(word);
                        fullLine.append(word);

                        lineW += wordW;
                    }
                }

                previous.append(word);
            }

            if (line.length() > 0)
            {
                lines.add(line.toString());
                maxLineW = Tools.max(maxLineW, lineW);
            }
            if (fullLine.length() > 0) fullLines.add(fullLine.toString());

            width = maxLineW;
            if (this instanceof GUIMultilineTextInput && text.length() > 0 && text.charAt(text.length() - 1) == '\n')
            {
                height = (double) (Tools.max(1, fullLines.size()) * FONT_RENDERER.FONT_HEIGHT - 1) * scale / screen.height;
            }
            else height = (double) (Tools.max(1, lines.size()) * FONT_RENDERER.FONT_HEIGHT - 1) * scale / screen.height;
        }

        if (parent != null)
        {
            width /= parent.absoluteWidth();
            height /= parent.absoluteHeight();
        }

        recalcAndRepositionSubElements(0);

        postRecalc();

        return this;
    }

    @Override
    public void draw()
    {
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        double adjustedScale = scale * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        GlStateManager.scale(adjustedScale / absolutePxWidth(), adjustedScale / absolutePxHeight(), 1);

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

    @Override
    public String toString()
    {
        return text;
    }
}
