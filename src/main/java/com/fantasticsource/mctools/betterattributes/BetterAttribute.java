package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class BetterAttribute
{
    public static final HashMap<String, BetterAttribute> BETTER_ATTRIBUTES = new HashMap<>();
    public static final Field
            MODIFIABLE_ATTRIBUTE_INSTANCE_NEEDS_UPDATE_FIELD = ReflectionTool.getField(ModifiableAttributeInstance.class, "field_111133_g", "needsUpdate"),
            PLAYER_CAPABILITIES_WALK_SPEED_FIELD = ReflectionTool.getField(PlayerCapabilities.class, "field_75097_g", "walkSpeed");

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
    public final ArrayList<BetterAttribute> parents = new ArrayList<>(), children = new ArrayList<>();
    public IAttribute mcAttributeToSet = null;
    public double mcAttributeScalar = 1;

    public BetterAttribute(String name, boolean isGood, double defaultBaseAmount, BetterAttribute... parents)
    {
        this(name, isGood, defaultBaseAmount, true, parents);
    }

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
        for (BetterAttribute parent : parents) parent.children.add(this);
        register(this);
    }

    public final boolean removeFrom(Entity entity)
    {
        NBTTagCompound mainCompound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID);
        if (mainCompound == null) return false;

        NBTTagCompound compound = MCTools.getSubCompoundIfExists(mainCompound, "baseAttributes");
        boolean found = false;
        if (compound != null && compound.hasKey(name))
        {
            compound.removeTag(name);
            found = true;
        }

        compound = MCTools.getSubCompoundIfExists(mainCompound, "attributes");
        if (compound != null && compound.hasKey(name))
        {
            compound.removeTag(name);
            found = true;
        }

        return found;
    }

    public final void setBaseAmount(Entity entity, double value)
    {
        MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "baseAttributes").setDouble(name, value);
        getTotalAmount(entity); //Recalc total (necessary if no caching children exist)
        for (BetterAttribute child : children)
        {
            if (!child.canUseTotalAmountCaching) continue;
            if (child.removeFrom(entity)) child.getTotalAmount(entity);
        }
    }

    public final double getBaseAmount(Entity entity)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "baseAttributes");
        return compound == null || !compound.hasKey(name) ? defaultBaseAmount : compound.getDouble(name);
    }

    public final double getTotalAmount(Entity entity)
    {
        double result;
        if (canUseTotalAmountCaching)
        {
            NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "attributes");
            if (compound.hasKey(name)) result = compound.getDouble(name);
            else
            {
                result = calculateTotalAmount(entity);
                compound.setDouble(name, result);
            }
        }
        else result = calculateTotalAmount(entity);

        if (mcAttributeToSet != null && entity instanceof EntityLivingBase)
        {
            AttributeMap attributeMap = (AttributeMap) ((EntityLivingBase) entity).getAttributeMap();
            IAttributeInstance attributeInstance = attributeMap.getAttributeInstance(mcAttributeToSet);
            if (attributeInstance != null)
            {
                double convertedAmount = result * mcAttributeScalar;
                attributeInstance.setBaseValue(convertedAmount);
                ReflectionTool.set(MODIFIABLE_ATTRIBUTE_INSTANCE_NEEDS_UPDATE_FIELD, attributeInstance, true);
                if (entity instanceof EntityPlayerMP && mcAttributeToSet == SharedMonsterAttributes.MOVEMENT_SPEED)
                {
                    //Because MC is EXTRA SPECIAL and can't even use its own attribute system correctly
                    ReflectionTool.set(PLAYER_CAPABILITIES_WALK_SPEED_FIELD, ((EntityPlayer) entity).capabilities, (float) convertedAmount);
                }
            }
        }

        return result;
    }

    public double calculateTotalAmount(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (BetterAttribute parent : parents) result += parent.getTotalAmount(entity);
        return result;
    }

    public BetterAttribute setMCAttribute(IAttribute mcAttribute, double scalar)
    {
        mcAttributeToSet = mcAttribute;
        mcAttributeScalar = scalar;
        return this;
    }

    public String getLocalizedName()
    {
        return I18n.translateToLocal("attribute.name." + name);
    }

    public String getLocalizedDisplayValue(Entity entity)
    {
        double total = getTotalAmount(entity);

        String result;
        if (mcAttributeToSet == SharedMonsterAttributes.MAX_HEALTH && entity instanceof EntityLivingBase)
        {
            result = I18n.translateToLocalFormatted("attribute.value." + name, Tools.formatNicely(((EntityLivingBase) entity).getHealth()), Tools.formatNicely(total));
            return result.contains("attribute.value") ? Tools.formatNicely(((EntityLivingBase) entity).getHealth()) + " / " + Tools.formatNicely(total) : result;
        }

        result = I18n.translateToLocalFormatted("attribute.value." + name, Tools.formatNicely(total));
        return result.contains("attribute.value") ? "" + Tools.formatNicely(total) : result;
    }

    public String getLocalizedDescription()
    {
        return I18n.translateToLocal("attribute.description." + name);
    }
}
