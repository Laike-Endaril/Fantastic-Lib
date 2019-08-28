package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.guielements.rect.text.filter.TextFilter;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import static com.fantasticsource.mctools.gui.GUIScreen.*;
import static com.fantasticsource.tools.datastructures.Color.*;

public class GUITextInputRect extends GUITextRect
{
    private static final Color T_WHITE = new Color(0xFFFFFF88);

    protected int cursorPosition, selectorPosition = -1;
    protected Color cursorColor, highlightColor;
    protected long cursorTime;
    private TextFilter filter;

    protected long lastClickTime;
    protected int lastAbsMouseX;


    public GUITextInputRect(GUIScreen screen, double x, double y, String text, TextFilter filter)
    {
        super(screen, x, y, text, filter.acceptable(text) ? GREEN : RED);

        cursorPosition = text.length();
        this.filter = filter;

        cursorColor = WHITE;
        highlightColor = T_WHITE;
    }

    public GUITextInputRect(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, Color cursorColor, Color hightlightColor)
    {
        super(screen, x, y, text, color, hoverColor, activeColor);

        cursorPosition = text.length();
        this.cursorColor = cursorColor;
        this.highlightColor = hightlightColor;

        filter = FilterNone.INSTANCE;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        if (!active) return;

//        if (keyCode == Keyboard.KEY_RETURN)
//        {
//            if (parent instanceof MultilineTextInput)
//            {
//                int min = Tools.min(cursorPosition, selectorPosition);
//                if (min == -1) min = cursorPosition;
//                String before = text.substring(0, min);
//                String after = text.substring(Tools.max(cursorPosition, selectorPosition));
//                text = before;
//                selectorPosition = -1;
//                cursorPosition = min + 1;
//            }
//        }
//        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_A)
        if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_A)
        {
            if (text.length() > 0)
            {
                selectorPosition = 0;
                cursorPosition = text.length();
            }
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_X)
        {
            String s = "";

            if (selectorPosition != -1 && selectorPosition != cursorPosition)
            {
                int min = Tools.min(selectorPosition, cursorPosition);
                s = text.substring(min, Tools.max(cursorPosition, selectorPosition));
                text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                selectorPosition = -1;
                cursorPosition = min;
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_C)
        {
            String s = "";

            if (selectorPosition != -1 && selectorPosition != cursorPosition)
            {
                s = text.substring(Tools.min(cursorPosition, selectorPosition), Tools.max(cursorPosition, selectorPosition));
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_V)
        {
            int min = Tools.min(cursorPosition, selectorPosition);
            if (min == -1) min = cursorPosition;
            String before = text.substring(0, min) + removeIllegalChars(GUIScreen.getClipboardString());
            text = before + text.substring(Tools.max(cursorPosition, selectorPosition));
            selectorPosition = -1;
            cursorPosition = before.length();
        }
        else if (typedChar >= ' ' && typedChar <= '~')
        {
            int min = Tools.min(cursorPosition, selectorPosition);
            if (min == -1) min = cursorPosition;
            String before = text.substring(0, min);
            String after = text.substring(Tools.max(cursorPosition, selectorPosition));
            text = before + typedChar + after;
            selectorPosition = -1;
            cursorPosition = min + 1;
        }
        else if (typedChar == '\b')
        {
            if (selectorPosition != -1 && selectorPosition != cursorPosition)
            {
                int min = Tools.min(selectorPosition, cursorPosition);
                text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                selectorPosition = -1;
                cursorPosition = min;
            }
            else if (cursorPosition > 0)
            {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
        }
        else if (keyCode == Keyboard.KEY_DELETE)
        {
            if (selectorPosition != -1 && selectorPosition != cursorPosition)
            {
                int min = Tools.min(selectorPosition, cursorPosition);
                text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                selectorPosition = -1;
                cursorPosition = min;
            }
            else if (cursorPosition < text.length())
            {
                text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
            }
        }
        else if (keyCode == Keyboard.KEY_LEFT)
        {
            if (GUIScreen.isShiftKeyDown())
            {
                if (selectorPosition == -1 && cursorPosition > 0) selectorPosition = cursorPosition;
            }
            else selectorPosition = -1;

            if (cursorPosition > 0) cursorPosition--;
        }
        else if (keyCode == Keyboard.KEY_RIGHT)
        {
            if (GUIScreen.isShiftKeyDown())
            {
                if (selectorPosition == -1 && cursorPosition < text.length()) selectorPosition = cursorPosition;
            }
            else selectorPosition = -1;

            if (cursorPosition < text.length()) cursorPosition++;
        }

        cursorTime = System.currentTimeMillis();

        if (filter.getClass() != FilterNone.class)
        {
            activeColor = filter.acceptable(text) ? GREEN : RED;
            color = getColor(activeColor);
            hoverColor = getHover(activeColor);
        }

        recalc();
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        if (button == 0 && isMouseWithin())
        {
            setActive(true);
            long time = System.currentTimeMillis();
            int absMouseX = (int) (mouseX * screen.width);
            if (time - lastClickTime <= 250 && Math.abs(lastAbsMouseX - absMouseX) < 3)
            {
                //Double-click
                lastClickTime = 0;

                cursorPosition = findCursorPosition(mouseX());
                selectorPosition = cursorPosition;

                char[] chars = text.toCharArray();
                while (cursorPosition < chars.length && chars[cursorPosition] != ' ') cursorPosition++;
                while (selectorPosition > 0 && chars[selectorPosition - 1] != ' ') selectorPosition--;
                if (selectorPosition == cursorPosition) selectorPosition = -1;
            }
            else
            {
                //Single-click
                lastClickTime = time;
                lastAbsMouseX = absMouseX;

                if (isShiftKeyDown())
                {
                    if (selectorPosition == -1) selectorPosition = cursorPosition;
                }
                else selectorPosition = -1;
                cursorPosition = findCursorPosition(mouseX());
            }
        }
        else setActive(false);

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mousePressed(x - this.x, y - this.y, button);

        return active;
    }

    @Override
    public void mouseReleased(double x, double y, int button)
    {
        if (button == 0 && active && isMouseWithin()) MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this));

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseReleased(x - this.x, y - this.y, button);
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        if (button == 0 && isMouseWithin())
        {
            if (selectorPosition == -1) selectorPosition = cursorPosition;
            cursorPosition = findCursorPosition(mouseX());
            if (selectorPosition == cursorPosition) selectorPosition = -1;
        }

        super.mouseDrag(x, y, button);
    }

    @Override
    public void draw()
    {
        double screenWidth = screen.width, screenHeight = screen.height;

        int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
        double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (x * wScale), (int) ((1 - (y + height)) * hScale), (int) (width * wScale), (int) (height * hScale));

        for (GUIElement element : children)
        {
            if (element.x + element.width < 0 || element.x > width || element.y + element.height < 0 || element.y >= height) continue;
            element.draw();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);


        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.scale(1d / screen.width, 1d / screen.height, 1);


        //Highlight red if text does not pass filter
        if (!filter.acceptable(text))
        {
            GlStateManager.disableTexture2D();
            GlStateManager.color(1, 0, 0, 0.7f);
            GlStateManager.glBegin(GL11.GL_LINES);
            GlStateManager.glVertex3f(0, -0.5f, 0);
            GlStateManager.glVertex3f(0, (float) (height * screenHeight), 0);
            GlStateManager.glEnd();
        }


        //Actual text
        Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
        FONT_RENDERER.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);


        //Cursor and selection highlight
        if (active)
        {
            float cursorX = FONT_RENDERER.getStringWidth(text.substring(0, cursorPosition)) - 0.5f;
            float selectorX = selectorPosition == -1 ? cursorX : FONT_RENDERER.getStringWidth(text.substring(0, selectorPosition)) - 0.5f;

            if (cursorX != selectorX)
            {
                float min = Tools.min(cursorX, selectorX), max = Tools.max(cursorX, selectorX);
                GlStateManager.disableTexture2D();
                GlStateManager.color(highlightColor.rf(), highlightColor.gf(), highlightColor.bf(), highlightColor.af());
                GlStateManager.glBegin(GL11.GL_QUADS);
                GlStateManager.glVertex3f(min, -0.5f, 0);
                GlStateManager.glVertex3f(min, (float) (height * screenHeight), 0);
                GlStateManager.glVertex3f(max, (float) (height * screenHeight), 0);
                GlStateManager.glVertex3f(max, -0.5f, 0);
                GlStateManager.glEnd();
            }

            if ((System.currentTimeMillis() - cursorTime) % 1000 < 500)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.color(cursorColor.rf(), cursorColor.gf(), cursorColor.bf(), cursorColor.af());
                GlStateManager.glBegin(GL11.GL_LINES);
                GlStateManager.glVertex3f(cursorX, -0.5f, 0);
                GlStateManager.glVertex3f(cursorX, (float) (height * screenHeight), 0);
                GlStateManager.glEnd();
            }
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void setActive(boolean active)
    {
        if (active && !this.active) cursorTime = System.currentTimeMillis();
        super.setActive(active);
    }

    protected String removeIllegalChars(String text)
    {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray())
        {
            if (c >= ' ' && c <= '~') result.append(c);
        }
        return result.toString();
    }

    protected int findCursorPosition(double x)
    {
        double dif = x - getScreenX();
        int result = 0;
        for (char c : text.toCharArray())
        {
            double lastDif = dif;
            dif -= (double) FONT_RENDERER.getCharWidth(c) / screen.width;
            if (dif <= 0)
            {
                if (Math.abs(dif) < lastDif) result++;
                break;
            }
            result++;
        }
        return result;
    }
}
