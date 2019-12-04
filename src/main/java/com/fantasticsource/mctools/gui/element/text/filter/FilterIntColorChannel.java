package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterIntColorChannel extends TextFilter<Integer>
{
    public static final FilterIntColorChannel INSTANCE = new FilterIntColorChannel();

    private FilterIntColorChannel()
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
        String transformed = transformInput(input);
        if (input.equals("") || input.length() > 3) return false;

        for (char c : transformed.toCharArray())
        {
            if (c < '0' || c > '9') return false;
        }

        return Integer.parseInt(transformed) <= 255;
    }

    @Override
    public Integer parse(String input)
    {
        return !acceptable(input) ? null : Integer.parseInt(input);
    }
}
