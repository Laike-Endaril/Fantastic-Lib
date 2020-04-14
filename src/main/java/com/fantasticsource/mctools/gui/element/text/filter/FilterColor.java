package com.fantasticsource.mctools.gui.element.text.filter;

import com.fantasticsource.tools.Tools;

public class FilterColor extends TextFilter<Integer>
{
    public static final FilterColor INSTANCE = new FilterColor();

    private FilterColor()
    {
    }

    @Override
    public String transformInput(String input)
    {
        return input.trim().replace("0x", "");
    }

    @Override
    public boolean acceptable(String input)
    {
        String transformed = transformInput(input);
        if (input.equals("") || input.length() > 8) return false;

        for (char c : transformed.toCharArray())
        {
            if (c >= '0' && c <= '9') continue;
            if (c >= 'a' && c <= 'f') continue;
            if (c >= 'A' && c <= 'F') continue;
            return false;
        }

        return true;
    }

    @Override
    public Integer parse(String input)
    {
        return !acceptable(input) ? null : Tools.parseHexInt(transformInput(input));
    }
}
