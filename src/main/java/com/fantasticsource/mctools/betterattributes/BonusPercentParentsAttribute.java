package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class BonusPercentParentsAttribute extends AdditiveParentsAttribute
{
    public BonusPercentParentsAttribute(String name, boolean isGood, double defaultBaseAmount, BetterAttribute... parents)
    {
        super(name, isGood, defaultBaseAmount, parents);
    }

    public BonusPercentParentsAttribute(String name, boolean isGood, double defaultBaseAmount, Pair<BetterAttribute, Double>... parents)
    {
        super(name, isGood, defaultBaseAmount, parents);
    }

    public BonusPercentParentsAttribute(String name, boolean isGood, double defaultBaseAmount, HashMap<BetterAttribute, Double> parents)
    {
        super(name, isGood, defaultBaseAmount, parents);
    }

    @Override
    public double calculateTotalAmount(Entity entity)
    {
        double multiplier = 1;
        for (Map.Entry<BetterAttribute, Double> entry : parentMultipliers.entrySet()) multiplier += entry.getKey().getTotalAmount(entity) * entry.getValue() / 100;
        return getBaseAmount(entity) * multiplier;
    }
}
