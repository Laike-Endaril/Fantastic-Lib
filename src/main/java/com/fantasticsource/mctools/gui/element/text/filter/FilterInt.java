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
        if (input.equals("")) return false;

        boolean first = true;
        for (char c : transformInput(input).toCharArray())
        {
            if ((c < '0' || c > '9') && (!first || c != '-')) return false;
            first = false;
        }

        if (input.charAt(0) == '-' && input.length() == 1) return false;

        return true;
    }

    @Override
    public Integer parse(String input)
    {
        return !acceptable(input) ? null : Integer.parseInt(transformInput(input));
    }
}
