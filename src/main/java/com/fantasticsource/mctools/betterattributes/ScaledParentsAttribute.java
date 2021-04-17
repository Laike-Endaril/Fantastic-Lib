package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.fantasticlib.api.ABetterAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;
import java.util.Map;

public class ScaledParentsAttribute extends ABetterAttribute
{
    public final HashMap<ABetterAttribute, Double> parentMultipliers;

    public ScaledParentsAttribute(String name, boolean isGood, double defaultBaseAmount, ABetterAttribute... parents)
    {
        this(name, isGood, defaultBaseAmount, generateDefaults(parents));
    }

    public ScaledParentsAttribute(String name, boolean isGood, double defaultBaseAmount, HashMap<ABetterAttribute, Double> parents)
    {
        super(name, isGood, defaultBaseAmount, true, parents.keySet().toArray(new ABetterAttribute[0]));
        parentMultipliers = parents;

        boolean good = true;
        for (Map.Entry<ABetterAttribute, Double> entry : parents.entrySet())
        {
            if (entry == null || entry.getKey() == null)
            {
                good = false;
                System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE ONE OF ITS PARENTS IS NULL: " + name);
                break;
            }

            if (entry.getValue() == null)
            {
                good = false;
                System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE ONE OF ITS PARENTS' MULTIPLIERS IS NULL: " + name + "; " + entry.getKey().name);
                break;
            }
        }

        if (!good) BETTER_ATTRIBUTES.remove(name, this);
    }

    protected static HashMap<ABetterAttribute, Double> generateDefaults(ABetterAttribute... parents)
    {
        HashMap<ABetterAttribute, Double> result = new HashMap<>();
        for (ABetterAttribute parent : parents) result.put(parent, 1d);
        return result;
    }

    @Override
    public double calculateTotalAmount(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (Map.Entry<ABetterAttribute, Double> entry : parentMultipliers.entrySet()) result += entry.getKey().getTotalAmount(entity) * entry.getValue();
        return result;
    }
}
