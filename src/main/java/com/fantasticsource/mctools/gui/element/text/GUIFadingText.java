package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUIFadingText extends GUIText
{
    protected int startFadeTicks, endFadeTicks, age = 0;

    public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks)
    {
        this(screen, text, startFadeTicks, endFadeTicks, 1);
    }

    public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, double scale)
    {
        this(screen, text, startFadeTicks, endFadeTicks, Color.WHITE, scale);
    }

    public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color)
    {
        this(screen, text, startFadeTicks, endFadeTicks, color, 1);
    }

    public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color, double scale)
    {
        this(screen, text, startFadeTicks, endFadeTicks, color, color, color, scale);
    }

    public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor)
    {
        this(screen, text, startFadeTicks, endFadeTicks, color, hoverColor, activeColor, 1);
    }

    public GUIFadingText(GUIScreen screen, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor, double scale)
    {
        super(screen, text, color, hoverColor, activeColor, scale);

        this.startFadeTicks = startFadeTicks;
        this.endFadeTicks = endFadeTicks;
    }


    public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks)
    {
        this(screen, x, y, text, startFadeTicks, endFadeTicks, 1);
    }

    public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, double scale)
    {
        this(screen, x, y, text, startFadeTicks, endFadeTicks, Color.WHITE, scale);
    }

    public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color)
    {
        this(screen, x, y, text, startFadeTicks, endFadeTicks, color, 1);
    }

    public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color, double scale)
    {
        this(screen, x, y, text, startFadeTicks, endFadeTicks, color, color, color, scale);
    }

    public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor)
    {
        this(screen, x, y, text, startFadeTicks, endFadeTicks, color, hoverColor, activeColor, 1);
    }

    public GUIFadingText(GUIScreen screen, double x, double y, String text, int startFadeTicks, int endFadeTicks, Color color, Color hoverColor, Color activeColor, double scale)
    {
        super(screen, x, y, text, color, hoverColor, activeColor, scale);

        this.startFadeTicks = startFadeTicks;
        this.endFadeTicks = endFadeTicks;
    }


    @Override
    public void draw()
    {
        Color c = (active ? activeColor : isMouseWithin() ? hoverColor : color).copy();
        c.setAF(c.af() * Tools.min(Tools.max(1f - (float) (age - startFadeTicks) / (endFadeTicks - startFadeTicks), 0), 1));
        int mcColor = (c.color() >>> 8) | c.a() << 24;
        if ((mcColor & -67108864) == 0) return;


        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        double adjustedScale = scale * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        if (parent instanceof GUIPanZoomView) adjustedScale *= ((GUIPanZoomView) parent).getZoom();
        GlStateManager.scale(adjustedScale / absolutePxWidth(), adjustedScale / absolutePxHeight(), 1);

        int yy = 0;
        for (String line : lines)
        {
            FONT_RENDERER.drawString(line, 0, yy, mcColor, false);
            yy += FONT_RENDERER.FONT_HEIGHT;
        }

        GlStateManager.popMatrix();


        drawChildren();


        if (++age >= endFadeTicks) parent.remove(this);
    }
}
