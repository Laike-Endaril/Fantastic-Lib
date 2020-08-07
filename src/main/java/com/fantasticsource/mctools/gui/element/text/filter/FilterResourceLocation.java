package com.fantasticsource.mctools.gui.element.text.filter;

import com.fantasticsource.tools.Tools;
import net.minecraft.util.ResourceLocation;

public class FilterResourceLocation extends TextFilter<ResourceLocation>
{
    @Override
    public String transformInput(String input)
    {
        return input.trim();
    }

    @Override
    public boolean acceptable(String input)
    {
        String[] tokens = Tools.fixedSplit(transformInput(input), ":");
        return tokens.length == 2 && !tokens[0].trim().equals("") && !tokens[1].trim().equals("");
    }

    @Override
    public ResourceLocation parse(String input)
    {
        return !acceptable(input) ? null : new ResourceLocation(transformInput(input));
    }
}
