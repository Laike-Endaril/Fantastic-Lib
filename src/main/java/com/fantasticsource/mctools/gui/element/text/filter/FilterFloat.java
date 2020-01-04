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
        input = transformInput(input);

        if (input.equals("")) return false;

        if (input.indexOf('.') != input.lastIndexOf('.')) return false;

        boolean first = true;
        for (char c : input.toCharArray())
        {
            if ((c < '0' || c > '9') && c != '.' && (!first || c != '-')) return false;
            first = false;
        }

        int ignored = 0;
        if (input.contains("-")) ignored++;
        if (input.contains(".")) ignored++;
        if (input.length() <= ignored) return false;

        return true;
    }

    @Override
    public Float parse(String input)
    {
        return !acceptable(input) ? null : Float.parseFloat(input);
    }
}
