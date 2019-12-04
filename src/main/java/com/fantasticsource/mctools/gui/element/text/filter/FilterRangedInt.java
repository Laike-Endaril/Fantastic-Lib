package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.LinkedHashMap;

public class FilterRangedInt extends TextFilter<Integer>
{
    private final int min, max;

    private static LinkedHashMap<String, FilterRangedInt> instances = new LinkedHashMap<>();

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
        String transformed = transformInput(input);
        if (transformed.equals("")) return false;

        boolean first = true, digits = false;
        for (char c : transformed.toCharArray())
        {
            if (c == '-' && first)
            {
                first = false;
                continue;
            }
            first = false;
            if (c < '0' || c > '9') return false;
            else digits = true;
        }
        if (!digits) return false;

        int i = Integer.parseInt(transformed);
        return i >= min && i <= max;
    }

    @Override
    public Integer parse(String input)
    {
        return !acceptable(input) ? null : Integer.parseInt(input);
    }
}
