package com.fantasticsource.mctools;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.HashMap;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class BetterAttribute
{
    public static final HashMap<String, BetterAttribute> BETTER_ATTRIBUTES = new HashMap<>();

    public final String name;
    public final double defaultValue, minValue, maxValue;
    public final BetterAttribute[] parents;

    public BetterAttribute(String name, double defaultValue, double minValue, double maxValue, BetterAttribute... parents)
    {
        this.name = name;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.parents = parents;

        boolean good = true;
        for (BetterAttribute parent : parents)
        {
            if (parent == null)
            {
                good = false;
                System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE ONE OF ITS PARENTS IS NULL: " + name);
                break;
            }
        }
        if (good)
        {
            if (BETTER_ATTRIBUTES.containsKey(name)) System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE IT ALREADY EXISTS: " + name);
            else BETTER_ATTRIBUTES.put(name, this);
        }
    }

    public double get(Entity entity)
    {
        double result = defaultValue;
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "attributes");
        if (compound != null && compound.hasKey(name)) result = compound.getDouble(name);

        for (BetterAttribute parent : parents) result += parent.get(entity); //This can easily be overridden in subclasses to provide different formulae

        return result;
    }

    public void set(Entity entity, double value)
    {
        MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "attributes").setDouble(name, value);
    }
}
