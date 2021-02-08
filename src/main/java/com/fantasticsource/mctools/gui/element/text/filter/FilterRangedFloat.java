package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.LinkedHashMap;

public class FilterRangedFloat extends TextFilter<Float>
{
    private static LinkedHashMap<String, FilterRangedFloat> instances = new LinkedHashMap<>();
    private final float min, max;

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
        try
        {
            float f = Float.parseFloat(transformInput(input));
            return f >= min && f <= max;
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    @Override
    public Float parse(String input)
    {
        return !acceptable(input) ? null : Float.parseFloat(transformInput(input));
    }
}
