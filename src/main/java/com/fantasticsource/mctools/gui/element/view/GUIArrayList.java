package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;

public class GUIArrayList<T extends GUIElement> extends GUIScrollView
{
    public GUIArrayList(GUIScreen screen, double width, double height, GUIElement... subElements)
    {
        super(screen, width, height, subElements);

        //TODO add "add" button
    }

    public GUIArrayList(GUIScreen screen, double x, double y, double width, double height, GUIElement... subElements)
    {
        super(screen, x, y, width, height, subElements);

        //TODO add "add" button
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

        GUIAutocroppedView view = new GUIAutocroppedView(screen);
        view.add(new GUIElement(screen, 1, 0)); //Force full width
//        view.add(); //TODO add "remove" button
        if (lineElements != null) view.addAll(lineElements);
        super.add(view);
        return this;
    }
}
