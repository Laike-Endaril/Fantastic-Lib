package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.component.NBTSerializableComponent;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class BetterAttributeMod extends NBTSerializableComponent
{
    public static final int
            OPERATION_ADD_AMOUNT = 0,
            OPERATION_ADD_OLD_TIER_TIMES_AMOUNT = 1,
            OPERATION_MULT = 2;

    public String name = "", betterAttributeName = "";
    public int priority = 0, operation = 0;
    public double amount = 0;


    public BetterAttributeMod()
    {
    }

    public BetterAttributeMod(String name, String betterAttributeName, double amount)
    {
        this(name, betterAttributeName, 0, amount);
    }

    public BetterAttributeMod(String name, String betterAttributeName, int operation, double amount)
    {
        this(name, betterAttributeName, 0, operation, amount);
    }

    public BetterAttributeMod(String name, String betterAttributeName, int priority, int operation, double amount)
    {
        this.name = name;
        this.betterAttributeName = betterAttributeName;
        this.priority = priority;
        this.operation = operation;
        this.amount = amount;
    }


    @Override
    public String toString()
    {
        BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(betterAttributeName);
        boolean good = attribute == null || attribute.isGood;
        switch (operation)
        {
            case 0:
            case 1:
                if (amount < 0) good = !good;
                break;

            case 2:
                if (amount < 1) good = !good;
                break;
        }
        String result = "" + (good ? TextFormatting.GREEN : TextFormatting.RED);

        switch (operation)
        {
            case 0:
            case 1:
                if (amount >= 0) result += "+" + Tools.formatNicely(amount);
                break;

            case 2:
                result += Tools.formatNicely(amount) + "x";
                break;
        }

        result += TextFormatting.GRAY + " " + I18n.translateToLocal(betterAttributeName);

        return result;
    }


    @Override
    public BetterAttributeMod write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, name);
        ByteBufUtils.writeUTF8String(buf, betterAttributeName);

        buf.writeInt(priority);
        buf.writeInt(operation);

        buf.writeDouble(amount);

        return this;
    }

    @Override
    public BetterAttributeMod read(ByteBuf buf)
    {
        name = ByteBufUtils.readUTF8String(buf);
        betterAttributeName = ByteBufUtils.readUTF8String(buf);

        priority = buf.readInt();
        operation = buf.readInt();

        amount = buf.readDouble();

        return this;
    }

    @Override
    public BetterAttributeMod save(OutputStream stream)
    {
        new CStringUTF8().set(name).save(stream).set(betterAttributeName).save(stream);
        new CInt().set(priority).save(stream).set(operation).save(stream);
        new CDouble().set(amount).save(stream);

        return this;
    }

    @Override
    public BetterAttributeMod load(InputStream stream)
    {
        CStringUTF8 cs = new CStringUTF8();
        CInt ci = new CInt();

        name = cs.load(stream).value;
        betterAttributeName = cs.load(stream).value;

        priority = ci.load(stream).value;
        operation = ci.load(stream).value;

        amount = new CDouble().load(stream).value;

        return this;
    }


    @Override
    public NBTBase serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();

        compound.setString("name", name);
        compound.setString("betterAttributeName", betterAttributeName);

        compound.setInteger("priority", priority);
        compound.setInteger("operation", operation);

        compound.setDouble("amount", amount);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        NBTTagCompound compound = (NBTTagCompound) nbt;

        name = compound.getString("name");
        betterAttributeName = compound.getString("betterAttributeName");

        priority = compound.getInteger("priority");
        operation = compound.getInteger("operation");

        amount = compound.getDouble("amount");
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        for (BetterAttribute attribute : BetterAttribute.BETTER_ATTRIBUTES.values()) attribute.calculateTotal(entity);
    }

    @SubscribeEvent
    public static void betterAttributeCalc(BetterAttribute.BetterAttributeCalcEvent event)
    {
        String attributeName = event.attribute.name;
        Entity entity = event.entity;

        ArrayList<NBTTagCompound> compounds = new ArrayList<>();
        compounds.add(entity.getEntityData());
        for (ItemStack stack : GlobalInventory.getValidEquippedItems(entity))
        {
            if (stack.hasTagCompound()) compounds.add(stack.getTagCompound());
        }


        for (NBTTagCompound compound : compounds)
        {
            compound = MCTools.getSubCompoundIfExists(compound, MODID, "betterAttributeMods", attributeName);
            if (compound == null) continue;

            for (String key : compound.getKeySet())
            {
                BetterAttributeMod mod = new BetterAttributeMod();
                mod.deserializeNBT(compound.getCompoundTag(key));

                event.functions.add(d ->
                {
                    switch (mod.operation)
                    {
                        case 0:
                            d[0] += mod.amount;
                            break;

                        case 1:
                            d[0] += d[1] * mod.amount;
                            break;

                        case 2:
                            d[0] *= mod.amount;
                            break;
                    }
                    return true;
                }, mod.priority);
            }
        }
    }


    public static void addMods(Entity entity, BetterAttributeMod... mods)
    {
        NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID, "betterAttributeMods");

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (BetterAttributeMod mod : mods)
        {
            MCTools.getOrGenerateSubCompound(compound, mod.betterAttributeName).setTag(mod.name, mod.serializeNBT());
            attributesToRecalc.add(mod.betterAttributeName);
        }

        for (String attributeName : attributesToRecalc)
        {
            BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) attribute.calculateTotal(entity);
        }
    }


    public static void addMods(Entity entity, ItemStack stack, BetterAttributeMod... mods)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = MCTools.getOrGenerateSubCompound(stack.getTagCompound(), MODID, "betterAttributeMods");

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (BetterAttributeMod mod : mods)
        {
            MCTools.getOrGenerateSubCompound(compound, mod.betterAttributeName).setTag(mod.name, mod.serializeNBT());
            attributesToRecalc.add(mod.betterAttributeName);
        }

        if (entity != null)
        {
            for (String attributeName : attributesToRecalc)
            {
                BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                if (attribute != null) attribute.calculateTotal(entity);
            }
        }
    }


    public static void removeMods(Entity entity, BetterAttributeMod... mods)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (BetterAttributeMod mod : mods)
        {
            NBTTagCompound compound2 = MCTools.getSubCompoundIfExists(compound, mod.betterAttributeName);
            if (compound2 != null && compound2.hasKey(mod.name))
            {
                compound2.removeTag(mod.name);
                attributesToRecalc.add(mod.betterAttributeName);
            }
        }

        for (String attributeName : attributesToRecalc)
        {
            BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) attribute.calculateTotal(entity);
        }
    }

    public static void removeModsWithName(Entity entity, String modName)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (String attributeName : compound.getKeySet())
        {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            if (compound2.hasKey(modName))
            {
                compound2.removeTag(modName);
                attributesToRecalc.add(attributeName);
            }
        }

        for (String attributeName : attributesToRecalc)
        {
            BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) attribute.calculateTotal(entity);
        }
    }

    public static void removeModsWithNameContaining(Entity entity, String partialModName, boolean ignored)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (String attributeName : compound.getKeySet())
        {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            for (String foundModName : compound2.getKeySet().toArray(new String[0]))
            {
                if (foundModName.contains(partialModName))
                {
                    compound2.removeTag(foundModName);
                    attributesToRecalc.add(attributeName);
                }
            }
        }

        for (String attributeName : attributesToRecalc)
        {
            BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) attribute.calculateTotal(entity);
        }
    }

    public static void removeModsIf(Entity entity, Predicate<BetterAttributeMod> condition)
    {
        NBTTagCompound compound = MCTools.getSubCompoundIfExists(entity.getEntityData(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (String attributeName : compound.getKeySet())
        {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            BetterAttributeMod mod = new BetterAttributeMod();
            for (String key2 : compound2.getKeySet().toArray(new String[0]))
            {
                mod.deserializeNBT(compound2.getCompoundTag(key2));
                if (condition.test(mod))
                {
                    compound2.removeTag(key2);
                    attributesToRecalc.add(attributeName);
                }
            }
        }

        for (String attributeName : attributesToRecalc)
        {
            BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
            if (attribute != null) attribute.calculateTotal(entity);
        }
    }


    public static void removeMods(Entity entity, ItemStack stack, BetterAttributeMod... mods)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (BetterAttributeMod mod : mods)
        {
            NBTTagCompound compound2 = MCTools.getSubCompoundIfExists(compound, mod.betterAttributeName);
            if (compound2 != null && compound2.hasKey(mod.name))
            {
                compound2.removeTag(mod.name);
                attributesToRecalc.add(mod.betterAttributeName);
            }
        }

        if (entity != null)
        {
            for (String attributeName : attributesToRecalc)
            {
                BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                if (attribute != null) attribute.calculateTotal(entity);
            }
        }
    }

    public static void removeModsWithName(Entity entity, ItemStack stack, String modName)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (String attributeName : compound.getKeySet())
        {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            if (compound2.hasKey(modName))
            {
                compound2.removeTag(modName);
                attributesToRecalc.add(attributeName);
            }
        }

        if (entity != null)
        {
            for (String attributeName : attributesToRecalc)
            {
                BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                if (attribute != null) attribute.calculateTotal(entity);
            }
        }
    }

    public static void removeModsWithNameContaining(Entity entity, ItemStack stack, String partialModName, boolean ignored)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (String attributeName : compound.getKeySet())
        {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            for (String foundModName : compound2.getKeySet().toArray(new String[0]))
            {
                if (foundModName.contains(partialModName))
                {
                    compound2.removeTag(foundModName);
                    attributesToRecalc.add(attributeName);
                }
            }
        }

        if (entity != null)
        {
            for (String attributeName : attributesToRecalc)
            {
                BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                if (attribute != null) attribute.calculateTotal(entity);
            }
        }
    }

    public static void removeModsIf(Entity entity, ItemStack stack, Predicate<BetterAttributeMod> condition)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), MODID, "betterAttributeMods");
        if (compound == null) return;

        HashSet<String> attributesToRecalc = new HashSet<>();
        for (String attributeName : compound.getKeySet())
        {
            NBTTagCompound compound2 = compound.getCompoundTag(attributeName);
            BetterAttributeMod mod = new BetterAttributeMod();
            for (String key2 : compound2.getKeySet().toArray(new String[0]))
            {
                mod.deserializeNBT(compound2.getCompoundTag(key2));
                if (condition.test(mod))
                {
                    compound2.removeTag(key2);
                    attributesToRecalc.add(attributeName);
                }
            }
        }

        if (entity != null)
        {
            for (String attributeName : attributesToRecalc)
            {
                BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(attributeName);
                if (attribute != null) attribute.calculateTotal(entity);
            }
        }
    }
}
