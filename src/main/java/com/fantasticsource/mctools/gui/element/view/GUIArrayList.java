package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.util.ResourceLocation;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public abstract class GUIArrayList<T extends GUIElement> extends GUIScrollView
{
    public GUIArrayList(GUIScreen screen, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height, subElements);

        addAddLineLine();
    }

    public GUIArrayList(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height, subElements);

        addAddLineLine();
    }


    private void addAddLineLine()
    {
        GUIAutocroppedView line = new GUIAutocroppedView(screen);

        //Force line to be full width
        line.add(new GUIElement(screen, 1, 0));

        //Add "add line" button
        GUIImage idle = new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 2, 1d / 2);
        idle.add(new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getIdleColor(Color.GREEN), 0, 1d / 2, 1d / 2, 1d / 2));

        GUIImage hover = new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 2, 1d / 2);
        hover.add(new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getHoverColor(Color.GREEN), 0, 1d / 2, 1d / 2, 1d / 2));

        GUIImage active = new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), 1d / 2, 0, 1d / 2, 1d / 2);
        active.add(new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), Color.GREEN, 0, 1d / 2, 1d / 2, 1d / 2));

        line.add(new GUIButton(screen, idle, hover, active).addClickActions(this::addLine));

        //Add line
        add(line);
    }


    public GUIArrayList<T> addLine()
    {
        return addLine(newLineDefaultElements());
    }

    public GUIArrayList<T> addLine(T... lineElements)
    {
        return addLine(children.size() - 1, lineElements);
    }

    public GUIArrayList<T> addAllLines(T[]... lines)
    {
        for (T[] line : lines) addLine(line);
        return this;
    }

    public GUIArrayList<T> addLine(int index, T... lineElements)
    {
        if (index >= children.size()) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + size());

        GUIAutocroppedView line = new GUIAutocroppedView(screen, newLineBackgroundElement());

        //Force line to be full width
        line.add(new GUIElement(screen, 1, 0));

        //Add "remove line" button
        GUIImage idle = new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 2, 1d / 2);
        idle.add(new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getIdleColor(Color.RED), 1d / 2, 1d / 2, 1d / 2, 1d / 2));

        GUIImage hover = new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), 0, 0, 1d / 2, 1d / 2);
        hover.add(new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), GUIScreen.getHoverColor(Color.RED), 1d / 2, 1d / 2, 1d / 2, 1d / 2));

        GUIImage active = new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), 1d / 2, 0, 1d / 2, 1d / 2);
        active.add(new GUIImage(screen, 8, 8, new ResourceLocation(MODID, "image/gui.png"), Color.RED, 1d / 2, 1d / 2, 1d / 2, 1d / 2));

        line.add(new GUIButton(screen, idle, hover, active).addClickActions(() -> remove(line)));

        //Line elements
        if (lineElements != null) line.addAll(lineElements);

        //Add line
        add(children.size() - 1, line);

        return this;
    }


    public final double buttonWidth()
    {
        GUIElement button = children.get(children.size() - 1).get(1);
        return button.width;
    }


    public abstract T[] newLineDefaultElements();

    public GUIElement newLineBackgroundElement()
    {
        return null;
    }


    public int lineCount()
    {
        return size() - 1;
    }


    @Override
    public GUIAutocroppedView get(int index)
    {
        return (GUIAutocroppedView) super.get(index);
    }
}
