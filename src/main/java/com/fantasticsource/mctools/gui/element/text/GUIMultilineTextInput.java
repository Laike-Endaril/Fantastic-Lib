package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;
import static com.fantasticsource.tools.datastructures.Color.GRAY;

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
        GlStateManager.disableTexture2D();

        double scale = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

        if (!filter.acceptable(text))
        {
            //Highlight red if text does not pass filter
            GlStateManager.color(T_RED.rf(), T_RED.gf(), T_RED.bf(), T_RED.af());

            GlStateManager.glBegin(GL11.GL_QUADS);
            GlStateManager.glVertex3f(0, 0, 0);
            GlStateManager.glVertex3f(0, 1, 0);
            GlStateManager.glVertex3f(1, 1, 0);
            GlStateManager.glVertex3f(1, 0, 0);
            GlStateManager.glEnd();
        }
        else if (active)
        {
            //If we pass the filter, highlight gray if active
            GlStateManager.color(GRAY.rf(), GRAY.gf(), GRAY.bf(), 0.2f);

            GlStateManager.glBegin(GL11.GL_QUADS);
            GlStateManager.glVertex3f(0, 0, 0);
            GlStateManager.glVertex3f(0, 1, 0);
            GlStateManager.glVertex3f(1, 1, 0);
            GlStateManager.glVertex3f(1, 0, 0);
            GlStateManager.glEnd();
        }


        //Actual text
        if (text.length() > 0)
        {
            GlStateManager.enableTexture2D();

            GlStateManager.pushMatrix();
            GlStateManager.scale(scale / absolutePxWidth(), scale / absolutePxHeight(), 1);

            Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
            int yy = 0;
            for (String line : lines)
            {
                FONT_RENDERER.drawString(line, 1, yy, (c.color() >> 8) | c.a() << 24, false);
                yy += FONT_RENDERER.FONT_HEIGHT;
            }

            GlStateManager.popMatrix();

            GlStateManager.disableTexture2D();
        }


        //Draw cursor and selection highlight
        if (active)
        {
            float wUnit = (float) (scale / absolutePxWidth());
            float lineHeight = 1f / fullLines.size();

            //Calculate cursor position
            float cursorX = 0;
            int cursorLine = 0;
            {
                int lineStart = 0;
                for (String fullLine : fullLines)
                {
                    if (cursorPosition <= lineStart + fullLine.length())
                    {
                        cursorX = FONT_RENDERER.getStringWidth(fullLine.substring(0, cursorPosition - lineStart).replace("\n", "")) + 0.5f;
                        break;
                    }

                    cursorLine++;
                    lineStart += fullLine.length();
                }
            }
            cursorX = Tools.max(cursorX, 1f / absolutePxWidth());
            cursorX *= wUnit;


            if (selectorPosition != -1 && selectorPosition != cursorPosition)
            {
                //Selection highlight
                float selectorX = 0;
                int selectorLine = 0;
                int lineStart = 0;
                for (String fullLine : fullLines)
                {
                    if (selectorPosition <= lineStart + fullLine.length())
                    {
                        selectorX = FONT_RENDERER.getStringWidth(fullLine.substring(0, selectorPosition - lineStart).replace("\n", "")) + 0.5f;
                        break;
                    }

                    selectorLine++;
                    lineStart += fullLine.length();
                }
                selectorX *= wUnit;

                GlStateManager.color(1, 1, 1, 0.3f);

                if (selectorLine == cursorLine)
                {
                    float min = Tools.min(cursorX, selectorX), max = Tools.max(cursorX, selectorX);

                    GlStateManager.glBegin(GL11.GL_QUADS);
                    GlStateManager.glVertex3f(min, cursorLine * lineHeight, 0);
                    GlStateManager.glVertex3f(min, (cursorLine + 1) * lineHeight, 0);
                    GlStateManager.glVertex3f(max, (cursorLine + 1) * lineHeight, 0);
                    GlStateManager.glVertex3f(max, cursorLine * lineHeight, 0);
                    GlStateManager.glEnd();
                }
                else
                {
                    if (selectorPosition < cursorPosition)
                    {
                        GlStateManager.glBegin(GL11.GL_QUADS);

                        //First line
                        GlStateManager.glVertex3f(selectorX, selectorLine * lineHeight, 0);
                        GlStateManager.glVertex3f(selectorX, (selectorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(1, (selectorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(1, selectorLine * lineHeight, 0);

                        //Middle lines (if any)
                        for (int middleLine = selectorLine + 1; middleLine < cursorLine; middleLine++)
                        {
                            GlStateManager.glVertex3f(0, middleLine * lineHeight, 0);
                            GlStateManager.glVertex3f(0, (middleLine + 1) * lineHeight, 0);
                            GlStateManager.glVertex3f(1, (middleLine + 1) * lineHeight, 0);
                            GlStateManager.glVertex3f(1, middleLine * lineHeight, 0);
                        }

                        //Last line
                        GlStateManager.glVertex3f(0, cursorLine * lineHeight, 0);
                        GlStateManager.glVertex3f(0, (cursorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(cursorX, (cursorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(cursorX, cursorLine * lineHeight, 0);

                        GlStateManager.glEnd();
                    }
                    else
                    {
                        GlStateManager.glBegin(GL11.GL_QUADS);

                        //First line
                        GlStateManager.glVertex3f(cursorX, cursorLine * lineHeight, 0);
                        GlStateManager.glVertex3f(cursorX, (cursorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(1, (cursorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(1, cursorLine * lineHeight, 0);

                        //Middle lines (if any)
                        for (int middleLine = cursorLine + 1; middleLine < selectorLine; middleLine++)
                        {
                            GlStateManager.glVertex3f(0, middleLine * lineHeight, 0);
                            GlStateManager.glVertex3f(0, (middleLine + 1) * lineHeight, 0);
                            GlStateManager.glVertex3f(1, (middleLine + 1) * lineHeight, 0);
                            GlStateManager.glVertex3f(1, middleLine * lineHeight, 0);
                        }

                        //Last line
                        GlStateManager.glVertex3f(0, selectorLine * lineHeight, 0);
                        GlStateManager.glVertex3f(0, (selectorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(selectorX, (selectorLine + 1) * lineHeight, 0);
                        GlStateManager.glVertex3f(selectorX, selectorLine * lineHeight, 0);

                        GlStateManager.glEnd();
                    }
                }
            }

            //Cursor
            if ((System.currentTimeMillis() - cursorTime) % 1000 < 500)
            {
                GlStateManager.color(1, 1, 1, 1);

                GlStateManager.glBegin(GL11.GL_LINES);
                GlStateManager.glVertex3f(cursorX, cursorLine * lineHeight, 0);
                GlStateManager.glVertex3f(cursorX, (cursorLine + 1) * lineHeight, 0);
                GlStateManager.glEnd();
            }
        }


        drawChildren();
    }

    public int fullLineCount()
    {
        return fullLines.size();
    }

    public int cursorLine()
    {
        int pos = cursorPosition;
        int line = 0;
        for (String fullLine : fullLines)
        {
            pos -= fullLine.length();
            if (pos <= 0) return line;
            line++;
        }
        return line;
    }
}
