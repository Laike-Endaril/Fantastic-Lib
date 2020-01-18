package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterWhitelist extends TextFilter<String>
{
    protected ArrayList<String> allowed = new ArrayList<>();

    protected FilterWhitelist(String... allowed)
    {
        this.allowed.addAll(Arrays.asList(allowed));
    }

    @Override
    public String transformInput(String input)
    {
        if (input == null) return null;
        return input.trim();
    }

    @Override
    public boolean acceptable(String input)
    {
        if (input == null) return false;

        for (String s : allowed) if (input.equals(s)) return true;

        return false;
    }

    @Override
    public String parse(String input)
    {
        return input == null || !acceptable(input) ? null : transformInput(input);
    }
}
