package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIGradient extends GUIElement
{
    private Color[] colors, hoverColors, activeColors;

    public GUIGradient(GUIScreen screen, double x, double y, double width, double height, Color color)
    {
        this(screen, x, y, width, height, color, color, color);
    }

    public GUIGradient(GUIScreen screen, double x, double y, double width, double height, Color color, Color hover, Color active)
    {
        this(screen, x, y, width, height, color, color, color, color, hover, hover, hover, hover, active, active, active, active);
    }

    public GUIGradient(GUIScreen screen, double x, double y, double width, double height, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight)
    {
        this(screen, x, y, width, height, topRight, topLeft, bottomLeft, bottomRight, topRight, topLeft, bottomLeft, bottomRight, topRight, topLeft, bottomLeft, bottomRight);
    }

    public GUIGradient(GUIScreen screen, double x, double y, double width, double height, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight, Color topRightHover, Color topLeftHover, Color bottomLeftHover, Color bottomRightHover, Color topRightActive, Color topLeftActive, Color bottomLeftActive, Color bottomRightActive)
    {
        super(screen, x, y, width, height);

        colors = new Color[]{topRight, topLeft, bottomLeft, bottomRight};
        hoverColors = new Color[]{topRightHover, topLeftHover, bottomLeftHover, bottomRightHover};
        activeColors = new Color[]{topRightActive, topLeftActive, bottomLeftActive, bottomRightActive};
    }

    @Override
    public void draw()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();

        float x1 = (float) getScreenX();
        float y1 = (float) getScreenY();
        float x2 = (float) (x1 + getScreenWidth());
        float y2 = (float) (y1 + getScreenHeight());

        Color[] colors = active ? activeColors : isMouseWithin() ? hoverColors : this.colors;

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.color(colors[0].rf(), colors[0].gf(), colors[0].bf(), colors[0].af());
        GlStateManager.glVertex3f(x2, y1, 0);
        GlStateManager.color(colors[1].rf(), colors[1].gf(), colors[1].bf(), colors[1].af());
        GlStateManager.glVertex3f(x1, y1, 0);
        GlStateManager.color(colors[2].rf(), colors[2].gf(), colors[2].bf(), colors[2].af());
        GlStateManager.glVertex3f(x1, y2, 0);
        GlStateManager.color(colors[3].rf(), colors[3].gf(), colors[3].bf(), colors[3].af());
        GlStateManager.glVertex3f(x2, y2, 0);
        GlStateManager.glEnd();

        super.draw();
    }
}
