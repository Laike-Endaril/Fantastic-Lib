package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterInt extends TextFilter
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
        for (char c : transformInput(input).toCharArray())
        {
            if (c < '0' || c > '9') return false;
        }
        return true;
    }
}
