package com.fantasticsource.mctools.gui.element.text.filter;

import com.fantasticsource.tools.Tools;
import net.minecraft.util.ResourceLocation;

public class FilterNullableResourceLocation extends TextFilter<ResourceLocation>
{
    public static final FilterNullableResourceLocation INSTANCE = new FilterNullableResourceLocation();

    protected FilterNullableResourceLocation()
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
        if (input.equals("")) return true;

        String[] tokens = Tools.fixedSplit(input, ":");
        return tokens.length == 2 && !tokens[0].trim().equals("") && !tokens[1].trim().equals("");
    }

    @Override
    public ResourceLocation parse(String input)
    {
        if (!acceptable(input)) return null;

        input = transformInput(input);
        return input.equals("") ? null : new ResourceLocation(input);
    }
}
