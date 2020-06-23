package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_LINES;

public class GUILine extends GUIElement
{
    private Color color, hoverColor, activeColor;
    private boolean isDownRight;

    public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color)
    {
        this(screen, x1, y1, x2, y2, color, color, color);
    }

    public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, Tools.min(x1, x2), Tools.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));

        isDownRight = (x1 < x2 == y1 < y2);
        setColor(color, hoverColor, activeColor);
    }


    public void setColor(Color color)
    {
        setColor(color, color, color);
    }

    public void setColor(Color color, Color hoverColor, Color activeColor)
    {
        this.color = color;
        this.hoverColor = hoverColor;
        this.activeColor = activeColor;
    }


    @Override
    public void draw()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();

        Color color = active ? activeColor : isMouseWithin() ? hoverColor : this.color;

        GlStateManager.glBegin(GL_LINES);
        GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());
        if (isDownRight)
        {
            GlStateManager.glVertex3f(0, 0, 0);
            GlStateManager.glVertex3f(1, 1, 0);
        }
        else
        {
            GlStateManager.glVertex3f(0, 1, 0);
            GlStateManager.glVertex3f(1, 0, 0);
        }
        GlStateManager.glEnd();


        drawChildren();
    }
}
