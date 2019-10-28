package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUITextSpacer extends GUIElement
{
    protected double scale;
    protected boolean vertical;


    public GUITextSpacer(GUIScreen screen)
    {
        this(screen, 1);
    }

    public GUITextSpacer(GUIScreen screen, boolean vertical)
    {
        this(screen, 1, vertical);
    }

    public GUITextSpacer(GUIScreen screen, double length)
    {
        this(screen, length, 1);
    }

    public GUITextSpacer(GUIScreen screen, double length, double scale)
    {
        this(screen, length, scale, false);
    }

    public GUITextSpacer(GUIScreen screen, double length, boolean vertical)
    {
        this(screen, length, 1, vertical);
    }

    public GUITextSpacer(GUIScreen screen, double length, double scale, boolean vertical)
    {
        super(screen, vertical ? 0 : length, vertical ? length : 0);
        this.vertical = vertical;
        this.scale = scale * screen.textScale * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        recalc(0);
    }


    @Override
    public GUITextSpacer recalc(int subIndexChanged)
    {
        if (parent instanceof CodeInput)
        {
            if (vertical)
            {
                width = (double) (MonoASCIIFontRenderer.LINE_HEIGHT + 2) * scale / screen.width;
            }
            else
            {
                height = (double) (MonoASCIIFontRenderer.LINE_HEIGHT + 2) * scale / screen.height;
            }
        }
        else
        {
            if (vertical) width = (double) (FONT_RENDERER.FONT_HEIGHT - 1) * scale / screen.width;
            else height = (double) (FONT_RENDERER.FONT_HEIGHT - 1) * scale / screen.height;
        }

        if (parent != null)
        {
            width /= parent.absoluteWidth();
            height /= parent.absoluteHeight();
        }

        recalcAndRepositionSubElements(0);

        postRecalc();

        return this;
    }


    @Override
    public String toString()
    {
        return getClass().getSimpleName();
    }
}
