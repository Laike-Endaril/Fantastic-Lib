package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterInt extends TextFilter<Integer>
{
    public static final FilterInt INSTANCE = new FilterInt();

    private FilterInt()
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
        try
        {
            Integer.parseInt(transformInput(input));
            return true;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    @Override
    public Integer parse(String input)
    {
        return !acceptable(input) ? null : Integer.parseInt(transformInput(input));
    }
}
