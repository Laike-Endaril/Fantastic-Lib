package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.Namespace;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.mctools.gui.element.view.GUIList.Line;
import com.fantasticsource.tools.datastructures.Color;

import java.util.LinkedHashSet;
import java.util.function.Predicate;

public class StringListGUI extends GUIScreen
{
    protected String title;

    protected StringListGUI(String title)
    {
        this.title = title;
    }

    public static void show(String title, String prompt, String baseName, LinkedHashSet<String> strings)
    {
        StringListGUI gui = new StringListGUI(title);
        showStacked(gui);
        gui.drawStack = false;


        //Background
        gui.root.add(new GUIDarkenedBackground(gui));


        //Header
        GUINavbar navbar = new GUINavbar(gui);
        GUITextButton done = new GUITextButton(gui, "Done", Color.GREEN);
        GUITextButton cancel = new GUITextButton(gui, "Cancel", Color.RED);
        gui.root.addAll(navbar, done, cancel);


        //Main
        GUIList list = new GUIList(gui, true, 0.98, 1 - (cancel.y + cancel.height))
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                Namespace namespace = gui.namespaces.computeIfAbsent("Strings", o -> new Namespace());
                String nameString = namespace.getFirstAvailableNumberedName(baseName);
                GUILabeledTextInput input = new GUILabeledTextInput(gui, prompt, nameString, FilterNotEmpty.INSTANCE).setNamespace("Strings");

                return new GUIElement[]{input};
            }
        };
        list.addRemoveChildActions((Predicate<GUIElement>) element ->
        {
            if (element instanceof Line)
            {
                Line line = (Line) element;
                GUILabeledTextInput labeledTextInput = (GUILabeledTextInput) line.getLineElement(0);
                gui.namespaces.get("Strings").inputs.remove(labeledTextInput.input);
            }
            return false;
        });
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(gui, 0.02, 1 - (cancel.y + cancel.height), Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list);
        gui.root.addAll
                (
                        list,
                        scrollbar
                );
        for (String s : strings)
        {
            ((GUILabeledTextInput) list.addLine().getLineElement(0)).setText(s);
        }


        //Add main header actions
        cancel.addRecalcActions(() ->
        {
            list.height = 1 - (cancel.y + cancel.height);
            scrollbar.height = 1 - (cancel.y + cancel.height);
        });
        cancel.addClickActions(gui::close);
        done.addClickActions(() ->
        {
            //Validation
            for (Line line : list.getLines())
            {
                if (!((GUILabeledTextInput) line.getLineElement(0)).valid()) return;
            }


            //Processing
            strings.clear();
            for (Line line : list.getLines())
            {
                strings.add(((GUILabeledTextInput) line.getLineElement(0)).getText());
            }


            //Close GUI
            gui.close();
        });
    }

    @Override
    public String title()
    {
        return title;
    }
}
