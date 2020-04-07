package com.fantasticsource.mctools.gui.element.text.filter;

import java.util.ArrayList;
import java.util.Arrays;

public class FilterBlacklist extends TextFilter<String>
{
    public ArrayList<String> disallowed = new ArrayList<>();

    public FilterBlacklist(String... disallowed)
    {
        this.disallowed.addAll(Arrays.asList(disallowed));
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

        input = transformInput(input);
        if (input.equals("")) return false;

        for (String s : disallowed) if (input.equals(s)) return false;

        return true;
    }

    @Override
    public String parse(String input)
    {
        return input == null || !acceptable(input) ? null : transformInput(input);
    }
}
