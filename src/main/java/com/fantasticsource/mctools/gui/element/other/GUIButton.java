package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.ResourceLocation;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class GUIButton extends GUIAutocroppedView
{
    private GUIElement idleElement, hoverElement, activeElement;

    public GUIButton(GUIScreen screen, GUIElement idleElement, GUIElement hoverElement, GUIElement activeElement)
    {
        super(screen);

        this.idleElement = idleElement;
        this.hoverElement = hoverElement;
        this.activeElement = activeElement;

        add(idleElement);
    }

    public static GUIButton newAddButton(GUIScreen screen)
    {
        GUIImage idle = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        idle.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getIdleColor(Color.GREEN), 0, 1d / 4, 1d / 4, 1d / 4));

        GUIImage hover = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        hover.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getHoverColor(Color.GREEN), 0, 1d / 4, 1d / 4, 1d / 4));

        GUIImage active = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 1d / 4, 0, 1d / 4, 1d / 4);
        active.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), Color.GREEN, 0, 1d / 4, 1d / 4, 1d / 4));

        return new GUIButton(screen, idle, hover, active);
    }

    public static GUIButton newRemoveButton(GUIScreen screen)
    {
        GUIImage idle = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        idle.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getIdleColor(Color.RED), 1d / 4, 1d / 4, 1d / 4, 1d / 4));

        GUIImage hover = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        hover.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getHoverColor(Color.RED), 1d / 4, 1d / 4, 1d / 4, 1d / 4));

        GUIImage active = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 1d / 4, 0, 1d / 4, 1d / 4);
        active.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), Color.RED, 1d / 4, 1d / 4, 1d / 4, 1d / 4));

        return new GUIButton(screen, idle, hover, active);
    }

    public static GUIButton newEditButton(GUIScreen screen)
    {
        GUIImage idle = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        idle.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getIdleColor(Color.ORANGE), 2d / 4, 1d / 4, 1d / 4, 1d / 4));

        GUIImage hover = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        hover.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getHoverColor(Color.ORANGE), 2d / 4, 1d / 4, 1d / 4, 1d / 4));

        GUIImage active = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 1d / 4, 0, 1d / 4, 1d / 4);
        active.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), Color.ORANGE, 2d / 4, 1d / 4, 1d / 4, 1d / 4));

        return new GUIButton(screen, idle, hover, active);
    }

    public static GUIButton newListButton(GUIScreen screen)
    {
        GUIImage idle = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        idle.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getIdleColor(Color.AQUA), 3d / 4, 1d / 4, 1d / 4, 1d / 4));

        GUIImage hover = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 4, 1d / 4);
        hover.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getHoverColor(Color.AQUA), 3d / 4, 1d / 4, 1d / 4, 1d / 4));

        GUIImage active = new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), 1d / 4, 0, 1d / 4, 1d / 4);
        active.add(new GUIImage(screen, 16, 16, new ResourceLocation(MODID, "image/gui.png"), Color.AQUA, 3d / 4, 1d / 4, 1d / 4, 1d / 4));

        return new GUIButton(screen, idle, hover, active);
    }

    @Override
    public void draw()
    {
        if (isMouseWithin())
        {
            if (active) set(activeElement);
            else set(hoverElement);
        }
        else set(idleElement);

        super.draw();
    }

    private void set(GUIElement element)
    {
        int index = indexOf(element);
        if (index != -1) return;

        index = indexOf(idleElement);
        if (index != -1) remove(index);
        index = indexOf(hoverElement);
        if (index != -1) remove(index);
        index = indexOf(activeElement);
        if (index != -1) remove(index);

        add(element);
    }
}
