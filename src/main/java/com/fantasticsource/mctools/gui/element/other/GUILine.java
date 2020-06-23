package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.GL_LINES;
import static org.lwjgl.opengl.GL11.GL_SCISSOR_TEST;

public class GUILine extends GUIElement
{
    protected Color color, hoverColor, activeColor;
    protected boolean isDownRight;
    protected float thickness;

    public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color)
    {
        this(screen, x1, y1, x2, y2, color, color, color, 1);
    }

    public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, float thickness)
    {
        this(screen, x1, y1, x2, y2, color, color, color, thickness);
    }

    public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, Color hoverColor, Color activeColor)
    {
        this(screen, x1, y1, x2, y2, color, hoverColor, activeColor, 1);
    }

    public GUILine(GUIScreen screen, double x1, double y1, double x2, double y2, Color color, Color hoverColor, Color activeColor, float thickness)
    {
        super(screen, Tools.min(x1, x2), Tools.min(y1, y2), Math.abs(x2 - x1), Math.abs(y2 - y1));

        isDownRight = (x1 < x2 == y1 < y2);
        setColor(color, hoverColor, activeColor);
        this.thickness = thickness;
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

        GL11.glDisable(GL_SCISSOR_TEST);
        GlStateManager.glLineWidth(thickness);

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

        GlStateManager.glLineWidth(1);
        GL11.glEnable(GL_SCISSOR_TEST);


        drawChildren();
    }
}
