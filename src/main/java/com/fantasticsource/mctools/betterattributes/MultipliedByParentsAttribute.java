package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class MultipliedByParentsAttribute extends ScaledParentsAttribute
{
    public MultipliedByParentsAttribute(String name, boolean isGood, double defaultBaseAmount, BetterAttribute... parents)
    {
        super(name, isGood, defaultBaseAmount, parents);
    }

    public MultipliedByParentsAttribute(String name, boolean isGood, double defaultBaseAmount, Pair<BetterAttribute, Double>... parents)
    {
        super(name, isGood, defaultBaseAmount, parents);
    }

    public MultipliedByParentsAttribute(String name, boolean isGood, double defaultBaseAmount, HashMap<BetterAttribute, Double> parents)
    {
        super(name, isGood, defaultBaseAmount, parents);
    }

    @Override
    public double calculateTotalAmount(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (Map.Entry<BetterAttribute, Double> entry : parentMultipliers.entrySet()) result *= entry.getKey().getTotalAmount(entity) * entry.getValue();
        return result;
    }
}
