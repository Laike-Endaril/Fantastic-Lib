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
        Line line = new Line(screen);

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
        return addLine(children.size() - 1, lineElements);
    }

    public GUIList addLine(int index, GUIElement... lineElements)
    {
        if (index >= children.size()) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Size: " + size());

        Line line = new Line(screen, newLineBackgroundElement());

        //Add blank element to force line to be full width
        line.add(new GUIElement(screen, 1, 0));

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
        return size() - 1;
    }


    @Override
    public Line get(int index)
    {
        return (Line) super.get(index);
    }


    public static class Line extends GUIAutocroppedView
    {
        public Line(GUIScreen screen)
        {
            super(screen);
        }

        public Line(GUIScreen screen, double padding)
        {
            super(screen, padding);
        }

        public Line(GUIScreen screen, GUIElement background)
        {
            super(screen, background);
        }

        public Line(GUIScreen screen, double padding, GUIElement background)
        {
            super(screen, padding, background);
        }

        public Line(GUIScreen screen, double x, double y)
        {
            super(screen, x, y);
        }

        public Line(GUIScreen screen, double x, double y, double padding)
        {
            super(screen, x, y, padding);
        }

        public Line(GUIScreen screen, double x, double y, GUIElement background)
        {
            super(screen, x, y, background);
        }

        public Line(GUIScreen screen, double x, double y, double padding, GUIElement background)
        {
            super(screen, x, y, padding, background);
        }


        public ArrayList<GUIElement> getLineElements()
        {
            if (size() < 4) return new ArrayList<>();

            int offset = 3;
            if (get(0) == background)
            {
                if (size() < 5) return new ArrayList<>();
                else offset++;
            }

            ArrayList<GUIElement> list = new ArrayList<>();
            for (int i = offset; i < children.size(); i++) list.add(get(i));
            return list;
        }

        public GUIElement getLineElement(int index)
        {
            return get(get(0) == background ? index + 4 : index + 3);
        }
    }
}
