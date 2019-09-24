package com.fantasticsource.mctools.gui.element.text.filter;

public abstract class TextFilter
{
    public abstract String transformInput(String input);

    public abstract boolean acceptable(String input);
}
