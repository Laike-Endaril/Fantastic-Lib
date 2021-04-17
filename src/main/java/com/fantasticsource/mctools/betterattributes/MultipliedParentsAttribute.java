package com.fantasticsource.mctools.betterattributes;

import net.minecraft.entity.Entity;

public class MultipliedParentsAttribute extends BetterAttribute
{
    public MultipliedParentsAttribute(String name, boolean isGood, double defaultBaseAmount, BetterAttribute... parents)
    {
        super(name, isGood, defaultBaseAmount, true, parents);
    }

    @Override
    public double calculateTotalAmount(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (BetterAttribute parent : parents) result *= parent.getTotalAmount(entity);
        return result;
    }
}
