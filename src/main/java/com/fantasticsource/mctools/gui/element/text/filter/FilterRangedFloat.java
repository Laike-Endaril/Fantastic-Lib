package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.LinkedHashMap;

public class FilterRangedFloat extends TextFilter<Float>
{
    private final float min, max;

    private static LinkedHashMap<String, FilterRangedFloat> instances = new LinkedHashMap<>();

    private FilterRangedFloat(float min, float max)
    {
        this.min = min;
        this.max = max;
    }

    public static FilterRangedFloat get(float min, float max)
    {
        return instances.computeIfAbsent(min + "," + max, o -> new FilterRangedFloat(min, max));
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
        if (transformed.equals("") || transformed.indexOf('.') != transformed.lastIndexOf('.')) return false;

        boolean first = true, digits = false;
        for (char c : transformed.toCharArray())
        {
            if (c == '-' && first)
            {
                first = false;
                continue;
            }
            first = false;
            if (c == '.') continue;
            if (c < '0' || c > '9') return false;
            else digits = true;
        }
        if (!digits) return false;

        float f = Float.parseFloat(transformed);
        return f >= min && f <= max;
    }

    @Override
    public Float parse(String input)
    {
        return !acceptable(input) ? null : Float.parseFloat(input);
    }
}
