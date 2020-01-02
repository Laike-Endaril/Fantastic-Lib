package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

public abstract class GUIList extends GUIScrollView
{
    public static final Color
            AL_WHITE = Color.WHITE.copy().setAF(0.3f),
            AL_BLACK = Color.BLACK.copy().setAF(0.3f);

    public final boolean editable;


    public GUIList(GUIScreen screen, boolean editable, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height, subElements);

        this.editable = editable;

        if (editable) addAddLineLine();
    }

    public GUIList(GUIScreen screen, boolean editable, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height, subElements);

        this.editable = editable;

        if (editable) addAddLineLine();
    }


    private void addAddLineLine()
    {
        Line line = new Line(screen, editable);

        //Add blank element to force line to be full width
        line.add(new GUIElement(screen, 1, 0));

        //Add "add line" button
        line.add(GUIButton.newAddButton(screen)).addClickActions(this::addLine);

        //Add line to list
        add(line);
    }


    public GUIList addLine()
    {
        return addLine(newLineDefaultElements());
    }

    public GUIList addLine(int index)
    {
        return addLine(index, newLineDefaultElements());
    }

    public GUIList addLine(GUIElement... lineElements)
    {
        return addLine(lineCount(), lineElements);
    }

    public GUIList addLine(int index, GUIElement... lineElements)
    {
        if (index > lineCount()) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + lineCount());

        Line line = new Line(screen, editable, newLineBackgroundElement());

        //Add blank element to force line to be full width
        line.add(new GUIElement(screen, 1, 0));

        if (editable)
        {
            //Add "add line" button
            GUIButton button = GUIButton.newAddButton(screen);
            line.add(button).addClickActions(() ->
            {
                for (int i = 0; i < size(); i++)
                {
                    if (get(i).indexOf(button) != -1)
                    {
                        addLine(i);
                        break;
                    }
                }
            });

            //Add "remove line" button
            line.add(GUIButton.newRemoveButton(screen).addClickActions(() -> remove(line)));
        }

        //Line elements
        if (lineElements != null) line.addAll(lineElements);

        //Add line to list
        add(index, line);

        return this;
    }

    public GUIList addAllLines(GUIElement[]... lines)
    {
        for (GUIElement[] line : lines) addLine(line);
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
        return editable ? size() - 1 : size();
    }


    @Override
    public Line get(int index)
    {
        return (Line) super.get(index);
    }


    public static class Line extends GUIAutocroppedView
    {
        public final boolean editable;


        public Line(GUIScreen screen, boolean editable)
        {
            this(screen, editable, 0);
        }

        public Line(GUIScreen screen, boolean editable, double padding)
        {
            this(screen, editable, padding, null);
        }

        public Line(GUIScreen screen, boolean editable, GUIElement background)
        {
            this(screen, editable, 0, background);
        }

        public Line(GUIScreen screen, boolean editable, double padding, GUIElement background)
        {
            super(screen, padding, background);

            this.editable = editable;
        }


        public Line(GUIScreen screen, boolean editable, double x, double y)
        {
            this(screen, editable, x, y, 0);
        }

        public Line(GUIScreen screen, boolean editable, double x, double y, double padding)
        {
            this(screen, editable, x, y, padding, null);
        }

        public Line(GUIScreen screen, boolean editable, double x, double y, GUIElement background)
        {
            this(screen, editable, x, y, 0, background);
        }

        public Line(GUIScreen screen, boolean editable, double x, double y, double padding, GUIElement background)
        {
            super(screen, x, y, padding, background);

            this.editable = editable;
        }


        public ArrayList<GUIElement> getLineElements()
        {
            int offset = editable ? 3 : 1;
            if (size() > 0 && get(0) == background) offset++;

            if (size() <= offset) return new ArrayList<>();


            ArrayList<GUIElement> list = new ArrayList<>();
            for (int i = offset; i < children.size(); i++) list.add(get(i));
            return list;
        }

        public GUIElement getLineElement(int index)
        {
            return getLineElements().get(index);
        }
    }
}
