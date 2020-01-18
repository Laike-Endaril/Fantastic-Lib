package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterNotEmpty extends TextFilter<String>
{
    public static final FilterNotEmpty INSTANCE = new FilterNotEmpty();

    protected FilterNotEmpty()
    {
    }

    @Override
    public String transformInput(String input)
    {
        return input.trim();
    }

    @Override
    public boolean acceptable(String input)
    {
        if (input == null) return false;

        return !transformInput(input).equals("");
    }

    @Override
    public String parse(String input)
    {
        return !acceptable(input) ? null : input;
    }
}
