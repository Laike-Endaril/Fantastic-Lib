package com.fantasticsource.mctools.gui;

import com.fantasticsource.mctools.gui.element.text.GUITextInput;

import java.util.ArrayList;

public class Namespace
{
    public final String name;
    public final ArrayList<GUITextInput> inputs = new ArrayList<>();


    public Namespace(String name)
    {
        this.name = name;
    }


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

    public String getFirstAvailableNumberedName()
    {
        return getFirstAvailableNumberedName(name);
    }

    public String getFirstAvailableNumberedName(String baseName)
    {
        if (!contains(baseName)) return baseName;

        int i = 2;
        while (contains(baseName + i)) i++;
        return baseName + i;
    }
}
