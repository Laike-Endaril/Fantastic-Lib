package com.fantasticsource.mctools.gui.guielements.rect.text.filter;

public class FilterNotEmpty extends TextFilter
{
    public static final FilterNotEmpty INSTANCE = new FilterNotEmpty();

    private FilterNotEmpty()
    {
    }

    @Override
    public boolean acceptable(String input)
    {
        return !input.trim().equals("");
    }
}
