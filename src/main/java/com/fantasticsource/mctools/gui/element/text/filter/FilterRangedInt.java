package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.LinkedHashMap;

public class FilterRangedInt extends TextFilter<Integer>
{
    private static LinkedHashMap<String, FilterRangedInt> instances = new LinkedHashMap<>();
    private final int min, max;

    private FilterRangedInt(int min, int max)
    {
        this.min = min;
        this.max = max;
    }

    public static FilterRangedInt get(int min, int max)
    {
        return instances.computeIfAbsent(min + "," + max, o -> new FilterRangedInt(min, max));
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
            int i = Integer.parseInt(transformInput(input));
            return i >= min && i <= max;
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
