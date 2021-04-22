package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class ScalarParentsAttribute extends AdditiveParentsAttribute
{
    public ScalarParentsAttribute(String name, double defaultBaseAmount, BetterAttribute... parents)
    {
        super(name, defaultBaseAmount, parents);
    }

    public ScalarParentsAttribute(String name, double defaultBaseAmount, Pair<BetterAttribute, Double>... parents)
    {
        super(name, defaultBaseAmount, parents);
    }

    public ScalarParentsAttribute(String name, double defaultBaseAmount, HashMap<BetterAttribute, Double> parents)
    {
        super(name, defaultBaseAmount, parents);
    }

    @Override
    protected double calculateSubtotal(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (Map.Entry<BetterAttribute, Double> entry : parentMultipliers.entrySet()) result *= entry.getKey().getTotalAmount(entity) * entry.getValue();
        return result;
    }
}
