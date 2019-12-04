package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.regex.Pattern;

public class FilterFloatColorChannel extends TextFilter<Float>
{
    public static final FilterFloatColorChannel INSTANCE = new FilterFloatColorChannel();

    private FilterFloatColorChannel()
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
        return input != null && Pattern.matches("(0*([.][0-9]*)?)|(0*1([.]0*)?)", transformInput(input));
    }

    @Override
    public Float parse(String input)
    {
        return !acceptable(input) ? null : Float.parseFloat(input);
    }
}
