package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.datastructures.Color;

public class TextInputGUI extends GUIScreen
{
    public final String title;
    public boolean pressedDone = false;
    public GUILabeledTextInput input;
    public GUITextButton doneButton, cancelButton;

    public TextInputGUI(String title, String inputLabel, String defaultInput)
    {
        this(title, inputLabel, defaultInput, FilterNone.INSTANCE, 1);
    }

    public TextInputGUI(String title, String inputLabel, String defaultInput, double textScale)
    {
        this(title, inputLabel, defaultInput, FilterNone.INSTANCE, textScale);
    }

    public TextInputGUI(String title, String inputLabel)
    {
        this(title, inputLabel, FilterNone.INSTANCE, 1);
    }

    public TextInputGUI(String title, String inputLabel, double textScale)
    {
        this(title, inputLabel, "", FilterNone.INSTANCE, textScale);
    }

    public TextInputGUI(String title, String inputLabel, TextFilter filter)
    {
        this(title, inputLabel, filter, 1);
    }

    public TextInputGUI(String title, String inputLabel, TextFilter filter, double textScale)
    {
        this(title, inputLabel, "", filter, textScale);
    }

    public TextInputGUI(String title, String inputLabel, String defaultInput, TextFilter filter)
    {
        this(title, inputLabel, defaultInput, filter, 1);
    }

    public TextInputGUI(String title, String inputLabel, String defaultInput, TextFilter filter, double textScale)
    {
        super(textScale);
        this.title = title;
        show();
        drawStack = false;


        //Background and Navigation bar
        root.addAll(
                new GUIDarkenedBackground(this),
                new GUINavbar(this)
        );


        //Add elements
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
        input = new GUILabeledTextInput(this, inputLabel, defaultInput, filter);
        doneButton = new GUITextButton(this, "Done", Color.GREEN);
        cancelButton = new GUITextButton(this, "Cancel", Color.RED);
        root.addAll
                (
                        new GUITextSpacer(this),
                        input,
                        new GUITextSpacer(this),
                        doneButton.addClickActions(() ->
                        {
                            pressedDone = true;
                            close();
                        }),
                        new GUITextSpacer(this),
                        cancelButton.addClickActions(this::close)
                );
    }

    @Override
    public String title()
    {
        return title;
    }
}
