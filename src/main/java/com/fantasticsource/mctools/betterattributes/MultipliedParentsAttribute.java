package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.fantasticlib.api.ABetterAttribute;
import net.minecraft.entity.Entity;

public class MultipliedParentsAttribute extends ABetterAttribute
{
    public MultipliedParentsAttribute(String name, boolean isGood, double defaultBaseAmount, ABetterAttribute... parents)
    {
        super(name, isGood, defaultBaseAmount, true, parents);
    }

    @Override
    public double calculateTotalAmount(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (ABetterAttribute parent : parents) result *= parent.getTotalAmount(entity);
        return result;
    }
}
