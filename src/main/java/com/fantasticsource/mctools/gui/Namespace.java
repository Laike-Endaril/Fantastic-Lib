package com.fantasticsource.mctools.gui;

import com.fantasticsource.mctools.gui.element.text.GUITextInput;

import java.util.ArrayList;

public class Namespace
{
    public ArrayList<GUITextInput> inputs = new ArrayList<>();

    public boolean contains(String value)
    {
        if (value == null)
        {
            for (GUITextInput input : inputs)
            {
                if (input.getText() == null) return true;
            }
        }
        else
        {
            for (GUITextInput input : inputs)
            {
                if (value.equals(input.getText())) return true;
            }
        }
        return false;
    }

    public boolean containsIgnoreObject(String value, GUITextInput ignore)
    {
        if (value == null)
        {
            for (GUITextInput input : inputs)
            {
                if (input == ignore) continue;

                if (input.getText() == null) return true;
            }
        }
        else
        {
            for (GUITextInput input : inputs)
            {
                if (input == ignore) continue;

                if (value.equals(input.getText())) return true;
            }
        }
        return false;
    }
}
