package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIButton;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.screen.YesNoGUI;
import com.fantasticsource.tools.datastructures.Color;

import java.util.ArrayList;

public abstract class GUIList extends GUIScrollView
{
    public static final Color
            AL_WHITE = Color.WHITE.copy().setAF(0.3f),
            AL_BLACK = Color.BLACK.copy().setAF(0.3f);

    public final boolean editable;
    protected boolean confirmLineDeletion = false;


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


    public GUIList setConfirmLineDeletion(boolean confirmLineDeletion)
    {
        this.confirmLineDeletion = confirmLineDeletion;
        return this;
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


    public Line addLine()
    {
        return addLine(lineCount());
    }

    public Line addLine(int index)
    {
        return addLine(index, newLineDefaultElements());
    }

    public Line addLine(GUIElement... lineElements)
    {
        return addLine(lineCount(), lineElements);
    }

    public Line addLine(int index, GUIElement... lineElements)
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
            line.add(GUIButton.newRemoveButton(screen).addClickActions(() ->
                    {
                        if (!confirmLineDeletion) remove(line);
                        else
                        {
                            YesNoGUI yesNoGUI = new YesNoGUI("Remove line?", "Are you sure you want to remove this?");
                            yesNoGUI.addOnClosedActions(() ->
                            {
                                if (yesNoGUI.pressedYes) remove(line);
                            });
                            yesNoGUI.show();
                        }
                    }
            ));
        }

        //Line elements
        if (lineElements != null) line.addAll(lineElements);

        //Add line to list
        add(index, line);

        return line;
    }

    public Line getLineContaining(GUIElement containedElement)
    {
        for (Line line : getLines())
        {
            if (line.children.contains(containedElement)) return line;
        }
        return null;
    }

    public int getLineIndexContaining(GUIElement containedElement)
    {
        int i = 0;
        for (Line line : getLines())
        {
            if (line.children.contains(containedElement)) return i;
            else i++;
        }
        return -1;
    }

    public GUIList addAllLines(GUIElement[]... lines)
    {
        for (GUIElement[] line : lines) addLine(line);
        return this;
    }

    public Line[] getLines()
    {
        Line[] result = new Line[lineCount()];
        System.arraycopy(children.toArray(new Line[0]), 0, result, 0, result.length);
        return result;
    }

    public Line getLine(int index)
    {
        return get(index);
    }

    public Line getLastFilledLine()
    {
        return getLine(lineCount() - 1);
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
