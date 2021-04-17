package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class BetterAttribute
{
    public static final HashMap<String, BetterAttribute> BETTER_ATTRIBUTES = new HashMap<>();

    public static void register(BetterAttribute betterAttribute)
    {
        String name = betterAttribute.name;

        for (BetterAttribute parent : betterAttribute.parents)
        {
            if (parent == null)
            {
                System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE ONE OF ITS PARENTS IS NULL: " + name);
                return;
            }
        }

        if (BETTER_ATTRIBUTES.containsKey(name))
        {
            System.err.println(TextFormatting.RED + "COULD NOT REGISTER BETTER ATTRIBUTE, BECAUSE IT ALREADY EXISTS: " + name);
            return;
        }

        BETTER_ATTRIBUTES.put(name, betterAttribute);
    }

    public final String name;
    public final double defaultBaseAmount;
    public final boolean isGood, canUseTotalAmountCaching;
    public final ArrayList<BetterAttribute> parents = new ArrayList<>();

    /**
     * @param name                     The name of the attribute.  May be used for name/description lang keys.  I suggest using the MC format eg. generic.maxHealth and expecting related lang keys eg. attribute.name.generic.maxHealth, attribute.description.generic.maxHealth.  Try to use a unique namespace instead of "generic".
     * @param defaultBaseAmount        The default base amount of the attribute (ie. not accounting for any changes from parent attributes or other external systems).
     * @param canUseTotalAmountCaching Whether the total can be cached and referenced via cache.  If true, the total is only recalculated when a parent attribute's value changes.  If false, calculateTotalAmount() is called every time getTotalAmount() is called.
     * @param parents                  Parent attributes whose values have an effect on this attribute's total value.  Mostly important if canUseTotalAmountCaching is true.  May also be used for categorization purposes, eg. in GUIs
     */
    public BetterAttribute(String name, boolean isGood, double defaultBaseAmount, boolean canUseTotalAmountCaching, BetterAttribute... parents)
    {
        this.name = name;
        this.isGood = isGood;
        this.defaultBaseAmount = defaultBaseAmount;
        this.canUseTotalAmountCaching = canUseTotalAmountCaching;
        this.parents.addAll(Arrays.asList(parents));
        register(this);
    }

    public final void setBaseAmount(Entity entity, double value)
    {
        MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "attributes").setDouble(name, value);
    }

    public final double getBaseAmount(Entity entity)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "attributes");
        return compound == null || !compound.hasKey(name) ? defaultBaseAmount : compound.getDouble(name);
    }

    public final double getTotalAmount(Entity entity)
    {
        if (canUseTotalAmountCaching)
        {
            NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "attributes");
            if (compound.hasKey(name)) return compound.getDouble(name);
            else
            {
                double result = calculateTotalAmount(entity);
                compound.setDouble(name, result);
                return result;
            }
        }

        return calculateTotalAmount(entity);
    }

    public double calculateTotalAmount(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (BetterAttribute parent : parents) result += parent.getTotalAmount(entity);
        return result;
    }
}
