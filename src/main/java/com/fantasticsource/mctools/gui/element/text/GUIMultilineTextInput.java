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
            float lineHeight = 1f / lines.size();

            //Calculate cursor position
            float cursorX = 0;
            int lineStart = 0;
            int cursorLine = 0;
            for (String fullLine : fullLines)
            {
                if (cursorPosition <= lineStart + fullLine.length())
                {
                    cursorX = FONT_RENDERER.getStringWidth(fullLine.replace("\n", "").substring(0, cursorPosition - lineStart)) + 0.5f;
                    break;
                }

                cursorLine++;
                lineStart += fullLine.length();
            }


            if (selectorPosition != -1 && selectorPosition != cursorPosition)
            {
                //Selection highlight
//                float selectorX = selectorPosition == -1 ? cursorX : FONT_RENDERER.getStringWidth(text.substring(0, selectorPosition)) - 0.5f;
//
//                float min = Tools.min(cursorX, selectorX), max = Tools.max(cursorX, selectorX);
//                GlStateManager.color(1, 1, 1, 0.3f);
//
//                GlStateManager.glBegin(GL11.GL_QUADS);
//                GlStateManager.glVertex3f(min, 0, 0);
//                GlStateManager.glVertex3f(min, 1, 0);
//                GlStateManager.glVertex3f(max, 1, 0);
//                GlStateManager.glVertex3f(max, 0, 0);
//                GlStateManager.glEnd();
            }

            cursorX = Tools.max(cursorX, 1f / absolutePxWidth());
            cursorX *= scale / absolutePxWidth();

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
}
