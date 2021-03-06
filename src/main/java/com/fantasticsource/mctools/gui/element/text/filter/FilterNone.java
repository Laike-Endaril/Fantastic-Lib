package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterNone extends TextFilter<String>
{
    public static final FilterNone INSTANCE = new FilterNone();

    private FilterNone()
    {
    }

    @Override
    public String transformInput(String input)
    {
        return input;
    }

    @Override
    public boolean acceptable(String input)
    {
        return true;
    }

    @Override
    public String parse(String input)
    {
        return input;
    }
}
