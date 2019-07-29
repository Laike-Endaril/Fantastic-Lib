package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class GUITextInputRect extends GUITextRect
{
    protected int cursorPosition, selectorPosition = -1;
    protected Color cursorColor, highlightColor;
    protected long cursorTime;

    public GUITextInputRect(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, Color cursorColor, Color hightlightColor)
    {
        super(screen, x, y, text, color, hoverColor, activeColor);

        cursorPosition = text.length();
        this.cursorColor = cursorColor;
        this.highlightColor = hightlightColor;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        if (!active) return;

        if (typedChar >= ' ' && typedChar <= '~')
        {
            String before = text.substring(0, cursorPosition);
            String after = text.substring(cursorPosition);
            text = before + typedChar + after;
            cursorPosition++;
        }
        else if (typedChar == '\b')
        {
            if (cursorPosition > 0)
            {
                text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                cursorPosition--;
            }
        }
        else if (keyCode == Keyboard.KEY_DELETE)
        {
            if (cursorPosition < text.length())
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
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        setActive(button == 0 && isMouseWithin());

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
    public void draw()
    {
        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.scale(1d / screen.width, 1d / screen.height, 1);

        Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
        fontRenderer.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);

        if (active)
        {
            float cursorX = fontRenderer.getStringWidth(text.substring(0, cursorPosition)) - 0.5f;
            float selectorX = selectorPosition == -1 ? cursorX : fontRenderer.getStringWidth(text.substring(0, selectorPosition)) - 0.5f;

            if (cursorX != selectorX)
            {
                float min = Tools.min(cursorX, selectorX), max = Tools.max(cursorX, selectorX);
                GlStateManager.disableTexture2D();
                GlStateManager.color(highlightColor.rf(), highlightColor.gf(), highlightColor.bf(), highlightColor.af());
                GlStateManager.glBegin(GL11.GL_QUADS);
                GlStateManager.glVertex3f(min, -0.5f, 0);
                GlStateManager.glVertex3f(min, fontRenderer.FONT_HEIGHT - 1, 0);
                GlStateManager.glVertex3f(max, fontRenderer.FONT_HEIGHT - 1, 0);
                GlStateManager.glVertex3f(max, -0.5f, 0);
                GlStateManager.glEnd();
            }

            if ((System.currentTimeMillis() - cursorTime) % 1000 < 500)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.color(cursorColor.rf(), cursorColor.gf(), cursorColor.bf(), cursorColor.af());
                GlStateManager.glBegin(GL11.GL_LINES);
                GlStateManager.glVertex3f(cursorX, -0.5f, 0);
                GlStateManager.glVertex3f(cursorX, fontRenderer.FONT_HEIGHT - 1, 0);
                GlStateManager.glEnd();
            }
        }

        GlStateManager.popMatrix();

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.draw();
    }

    @Override
    protected void setActive(boolean active)
    {
        if (active && !this.active) cursorTime = System.currentTimeMillis();
        super.setActive(active);
    }
}
