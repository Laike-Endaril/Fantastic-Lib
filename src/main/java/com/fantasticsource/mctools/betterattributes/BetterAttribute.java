package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.Pair;
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
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;

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
    public boolean isGood = true, canUseTotalAmountCaching = true, syncClientEntityDataToClient = true, syncOtherEntityDataToClient = false;
    public ArrayList<BetterAttribute> parents = new ArrayList<>(), children = new ArrayList<>();
    public IAttribute mcAttributeToSet = null;
    public double mcAttributeScalar = 1;
    public ArrayList<Predicate<Pair<Entity, ArrayList<String>>>> displayValueArgumentEditors = new ArrayList<>();

    /**
     * @param name              The name of the attribute.  May be used for name/description lang keys.  I suggest using the MC format eg. generic.maxHealth and expecting related lang keys eg. attribute.name.generic.maxHealth, attribute.description.generic.maxHealth.  Try to use a unique namespace instead of "generic".
     * @param defaultBaseAmount The default base amount of the attribute (ie. not accounting for any changes from parent attributes or other external systems).
     * @param parents           Parent attributes whose values have an effect on this attribute's total value.  Mostly important if canUseTotalAmountCaching is true.  May also be used for categorization purposes, eg. in GUIs
     */
    public BetterAttribute(String name, double defaultBaseAmount, BetterAttribute... parents)
    {
        this.name = name;
        this.defaultBaseAmount = defaultBaseAmount;
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

    public final void setBaseAmount(Entity entity, double amount)
    {
        if (amount == getBaseAmount(entity)) return;

        MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "baseAttributes").setDouble(name, amount);
        calculateTotal(entity); //Recalc total (necessary if no caching children exist, and also takes care of any caching and client sync)
        for (BetterAttribute child : children)
        {
            if (!child.canUseTotalAmountCaching) continue;
            if (child.removeFrom(entity)) child.calculateTotal(entity);
        }
    }

    public final double getBaseAmount(Entity entity)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "baseAttributes");
        return compound == null || !compound.hasKey(name) ? defaultBaseAmount : compound.getDouble(name);
    }

    protected double calculateSubtotal(Entity entity)
    {
        double result = getBaseAmount(entity);
        for (BetterAttribute parent : parents) result += parent.getTotalAmount(entity);
        return result;
    }

    protected final double calculateTotal(Entity entity)
    {
        double result = calculateSubtotal(entity);

        BetterAttributeCalcEvent event = new BetterAttributeCalcEvent(this, entity);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.functions.isEmpty())
        {
            double[] d = new double[]{result};
            while (!event.functions.isEmpty())
            {
                if (!event.functions.poll().test(d)) break;
            }
            result = d[0];
        }

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

        if (canUseTotalAmountCaching)
        {
            NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "attributes");
            compound.setDouble(name, result);
        }

        MinecraftForge.EVENT_BUS.post(new BetterAttributeChangedEvent(this, entity));
        sync(entity);
        return result;
    }

    public final double getTotalAmount(Entity entity)
    {
        if (canUseTotalAmountCaching)
        {
            NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "attributes");
            if (compound.hasKey(name)) return compound.getDouble(name);
        }

        return calculateTotal(entity);
    }

    public void setCurrentAmount(Entity entity, double amount)
    {
        if (amount == getCurrentAmount(entity)) return;

        MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "currentAttributes").setDouble(name, amount);
        if (mcAttributeToSet == SharedMonsterAttributes.MAX_HEALTH && entity instanceof EntityLivingBase) ((EntityLivingBase) entity).setHealth((float) amount);

        MinecraftForge.EVENT_BUS.post(new BetterAttributeChangedEvent(this, entity));
        sync(entity);
    }

    public double getCurrentAmount(Entity entity)
    {
        if (mcAttributeToSet == SharedMonsterAttributes.MAX_HEALTH && entity instanceof EntityLivingBase)
        {
            return ((EntityLivingBase) entity).getHealth();
        }

        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "currentAttributes");
        if (compound == null || !compound.hasKey(name)) return getTotalAmount(entity);
        return compound.getDouble(name);
    }

    public final void sync(Entity entity)
    {
        if (!entity.world.isRemote)
        {
            if (syncOtherEntityDataToClient)
            {
                Network.WRAPPER.sendToAllTracking(new Network.BetterAttributePacket(entity, this), entity);
            }
            else if (syncClientEntityDataToClient && entity instanceof EntityPlayerMP)
            {
                Network.WRAPPER.sendTo(new Network.BetterAttributePacket(entity, this), (EntityPlayerMP) entity);
            }
        }
    }

    public BetterAttribute setMCAttribute(IAttribute mcAttribute, double scalar)
    {
        mcAttributeToSet = mcAttribute;
        mcAttributeScalar = scalar;
        return this;
    }

    public BetterAttribute addDisplayValueArgumentEditor(Predicate<Pair<Entity, ArrayList<String>>> predicate)
    {
        displayValueArgumentEditors.add(predicate);
        return this;
    }

    public String getLocalizedName()
    {
        return I18n.translateToLocal("attribute.name." + name);
    }

    public String getLocalizedDisplayValue(Entity entity)
    {
        ArrayList<String> args = new ArrayList<>();
        args.add(Tools.formatNicely(getTotalAmount(entity)));
        args.add(Tools.formatNicely(getBaseAmount(entity)));
        args.add(Tools.formatNicely(getCurrentAmount(entity)));

        for (Predicate<Pair<Entity, ArrayList<String>>> editor : displayValueArgumentEditors) editor.test(new Pair<>(entity, args));

        String result = I18n.translateToLocalFormatted("attribute.value." + name, args.toArray());
        return result.contains("attribute.value") ? "" + args.get(0) : result;
    }

    public String getLocalizedDescription()
    {
        return I18n.translateToLocal("attribute.description." + name);
    }


    /**
     * The "functions" field allows you to add predicates which alter the final calculated value of the attribute
     * Functions will run in ascending priority order (0, 10, 212)
     * Function priority can be negative, and does not need to be consecutive with the priority of other functions
     * The current attribute total (after changes made by any functions run before yours) is stored in the double array passed to your function
     * If your function returns false, it will prevent any other functions from running after it
     * IF YOUR FUNCTION RELIES ON VARIABLES OUTSIDE THOSE OF THE ATTRIBUTE AND ITS PARENTS, SET THE ATTRIBUTE'S canUseTotalAmountCaching FIELD TO FALSE!!!
     */
    public static class BetterAttributeCalcEvent extends Event
    {
        public final BetterAttribute attribute;
        public final Entity entity;
        public final ExplicitPriorityQueue<Predicate<double[]>> functions = new ExplicitPriorityQueue<>();

        protected BetterAttributeCalcEvent(BetterAttribute attribute, Entity entity)
        {
            this.attribute = attribute;
            this.entity = entity;
        }
    }

    /**
     * Despite the event's name, it is possible for the attribute values to be the same as before
     * In some cases, it is also possible that the attribute values have changed even if this event has not been posted (especially true for an attribute linked to MC health)
     */
    public static class BetterAttributeChangedEvent extends Event
    {
        public final BetterAttribute attribute;
        public final Entity entity;

        public BetterAttributeChangedEvent(BetterAttribute attribute, Entity entity)
        {
            this.attribute = attribute;
            this.entity = entity;
        }
    }
}
