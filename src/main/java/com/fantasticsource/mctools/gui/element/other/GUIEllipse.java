package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.renderer.GlStateManager;

import static org.lwjgl.opengl.GL11.GL_TRIANGLE_FAN;

public class GUIEllipse extends GUIElement
{
    protected Color color, hoverColor, activeColor;
    protected TrigLookupTable sideStepper;

    public GUIEllipse(GUIScreen screen, double x, double y, double width, double height, int segments, Color color)
    {
        this(screen, x, y, width, height, segments, color, color, color);
    }

    public GUIEllipse(GUIScreen screen, double x, double y, double width, double height, int segments, Color color, Color hoverColor, Color activeColor)
    {
        super(screen, x, y, width, height);

        sideStepper = TrigLookupTable.getInstance(segments);
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


        GlStateManager.glBegin(GL_TRIANGLE_FAN);
        GlStateManager.color(color.rf(), color.gf(), color.bf(), color.af());
        double cos, sin;
        double[] array = sideStepper.getArray();
        for (int i = 0; i < array.length; i++)
        {
            sin = array[i];
            cos = array[Tools.posMod(i + (array.length >> 2), array.length)];
            GlStateManager.glVertex3f(0.5f + (float) (cos * 0.5), 0.5f - (float) (sin * 0.5), 0);
        }


        GlStateManager.glEnd();


        drawChildren();
    }
}
