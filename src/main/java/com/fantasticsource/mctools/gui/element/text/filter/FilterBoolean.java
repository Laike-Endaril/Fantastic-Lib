package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterBoolean extends TextFilter
{
    public static final FilterBoolean INSTANCE = new FilterBoolean();

    private FilterBoolean()
    {
    }

    @Override
    public String transformInput(String input)
    {
        input = input.trim().toLowerCase();
        if (input.equals("t")) input = "true";
        else if (input.equals("f")) input = "false";
        return input;
    }

    @Override
    public boolean acceptable(String input)
    {
        String s = transformInput(input);
        return s.equals("true") || s.equals("false");
    }
}
