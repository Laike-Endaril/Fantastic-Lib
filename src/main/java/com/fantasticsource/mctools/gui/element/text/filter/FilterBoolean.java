package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterBoolean extends TextFilter<Boolean>
{
    public static final FilterBoolean INSTANCE = new FilterBoolean();

    private FilterBoolean()
    {
    }

    @Override
    public String transformInput(String input)
    {
        input = input.trim().toLowerCase();
        if (input.equals("t") || input.equals("y") || input.equals("yes")) input = "true";
        else if (input.equals("f") || input.equals("n") || input.equals("no")) input = "false";
        return input;
    }

    @Override
    public boolean acceptable(String input)
    {
        String s = transformInput(input);
        return s.equals("true") || s.equals("false");
    }

    @Override
    public Boolean parse(String input)
    {
        return !acceptable(input) ? null : Boolean.parseBoolean(transformInput(input));
    }
}
