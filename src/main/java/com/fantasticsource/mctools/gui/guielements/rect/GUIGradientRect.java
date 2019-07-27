package com.fantasticsource.mctools.gui.guielements.rect;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class GUIGradientRect extends GUIRectElement
{
    private Color topRight, topLeft, bottomLeft, bottomRight;

    public GUIGradientRect(GUIScreen screen, double left, double top, double right, double bottom, Color topRight, Color topLeft, Color bottomLeft, Color bottomRight)
    {
        super(screen, left, top, right - left, bottom - top);
        this.topRight = topRight;
        this.topLeft = topLeft;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
    }

    @Override
    public void draw()
    {
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();

        float x1 = (float) getScreenX();
        float y1 = (float) getScreenY();
        float x2 = (float) (x1 + width);
        float y2 = (float) (y1 + height);

        GlStateManager.glBegin(GL_QUADS);
        GlStateManager.color(topRight.rf(), topRight.gf(), topRight.bf(), topRight.af());
        GlStateManager.glVertex3f(x2, y1, 0);
        GlStateManager.color(topLeft.rf(), topLeft.gf(), topLeft.bf(), topLeft.af());
        GlStateManager.glVertex3f(x1, y1, 0);
        GlStateManager.color(bottomLeft.rf(), bottomLeft.gf(), bottomLeft.bf(), bottomLeft.af());
        GlStateManager.glVertex3f(x1, y2, 0);
        GlStateManager.color(bottomRight.rf(), bottomRight.gf(), bottomRight.bf(), bottomRight.af());
        GlStateManager.glVertex3f(x2, y2, 0);
        GlStateManager.glEnd();
    }
}
