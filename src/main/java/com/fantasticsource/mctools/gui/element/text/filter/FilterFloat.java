package com.fantasticsource.mctools.gui.element.text.filter;

public class FilterFloat extends TextFilter<Float>
{
    public static final FilterFloat INSTANCE = new FilterFloat();

    private FilterFloat()
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
            Float.parseFloat(transformInput(input));
            return true;
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
