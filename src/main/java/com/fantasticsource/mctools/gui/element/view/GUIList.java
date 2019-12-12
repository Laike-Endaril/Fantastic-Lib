package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.tools.datastructures.Color;

public abstract class GUIList extends GUIScrollView
{
    public static final Color
            AL_WHITE = Color.WHITE.copy().setAF(0.3f),
            AL_BLACK = Color.BLACK.copy().setAF(0.3f);


    public GUIList(GUIScreen screen, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height, subElements);

        addAddLineLine();
    }

    public GUIList(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
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
        line.add(GUIButton.newAddButton(screen)).addClickActions(this::addLine);

        //Add line
        add(line);
    }


    public GUIList addLine()
    {
        return addLine(newLineDefaultElements());
    }

    public GUIList addLine(GUIElement... lineElements)
    {
        return addLine(children.size() - 1, lineElements);
    }

    public GUIList addAllLines(GUIElement[]... lines)
    {
        for (GUIElement[] line : lines) addLine(line);
        return this;
    }

    public GUIList addLine(int index, GUIElement... lineElements)
    {
        if (index >= children.size()) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + size());

        GUIAutocroppedView line = new GUIAutocroppedView(screen, newLineBackgroundElement());

        //Force line to be full width
        line.add(new GUIElement(screen, 1, 0));

        //Add "remove line" button
        line.add(GUIButton.newRemoveButton(screen).addClickActions(() -> remove(line)));

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


    public abstract GUIElement[] newLineDefaultElements();

    public GUIElement newLineBackgroundElement()
    {
        return new GUIGradient(screen, 1, 1, AL_BLACK, AL_BLACK, AL_WHITE, AL_WHITE);
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
