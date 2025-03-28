package com.fantasticsource.mctools.items;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class RegistryRegexItemFilter
{
    public String registryRegex;
    public LinkedHashMap<String, String> tagsRequired = new LinkedHashMap<>();
    public LinkedHashMap<String, String> tagsDisallowed = new LinkedHashMap<>();


    public RegistryRegexItemFilter()
    {
    }

    public RegistryRegexItemFilter(String registryRegex, LinkedHashMap<String, String> tagsRequired, LinkedHashMap<String, String> tagsDisallowed)
    {
        this.registryRegex = registryRegex;
        this.tagsRequired = tagsRequired;
        this.tagsDisallowed = tagsDisallowed;
    }


    /**
     * Syntax is domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2
     * All of these are optional except item
     * For domain, item, and meta, regex can be used
     * <p>
     * Each nbt value requires a key, but not necessarily a value (if no value is specified, it just checks if the key exists)
     * Each NBT entry can be negated by starting it with a !
     * eg...
     * !generic.attackDamage
     * !generic.attackDamage = 4
     * <p>
     * Examples...
     * diamond_sword
     * dye:0
     * tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material
     */
    public static RegistryRegexItemFilter getInstance(String itemStackString)
    {
        RegistryRegexItemFilter result = new RegistryRegexItemFilter();

        String[] registryAndNBT = itemStackString.trim().split(Pattern.quote(">"));

        if (registryAndNBT.length == 0)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.notEnoughItemFilterArgs", itemStackString));
            return null;
        }
        if (registryAndNBT.length > 2)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.tooManyItemFilterArgs", itemStackString));
            return null;
        }


        //Registry regex
        result.registryRegex = registryAndNBT[0].trim();


        //NBT
        if (registryAndNBT.length > 1)
        {
            String nbt = registryAndNBT[1].trim();
            for (Map.Entry<ResourceLocation, Enchantment> entry : ForgeRegistries.ENCHANTMENTS.getEntries())
            {
                nbt = nbt.replaceAll(entry.getKey().toString(), "id:" + Enchantment.getEnchantmentID(entry.getValue()) + "s");
            }

            String[] tags = nbt.split(Pattern.quote("&"));
            for (String tag : tags)
            {
                tag = tag.trim();
                if (tag.equals("")) continue;

                String[] keyValue = tag.split(Pattern.quote("="));
                if (keyValue.length > 2)
                {
                    System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.tooManyNBTValues", itemStackString));
                    return null;
                }

                String key = keyValue[0].trim();
                if (!key.equals(""))
                {
                    LinkedHashMap<String, String> map;
                    if (key.charAt(0) == '!')
                    {
                        key = key.substring(1);
                        map = result.tagsDisallowed;
                    }
                    else map = result.tagsRequired;

                    map.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
                }
            }
        }

        return result;
    }


    public boolean matches(ItemStack stack)
    {
        //Domain, item, and meta
        String domainNameMeta = stack.getItem().getRegistryName() + ":" + stack.getMetadata();
        if (!Pattern.matches(registryRegex, domainNameMeta)) return false;


        //Disallowed NBT
        NBTTagCompound compound = stack.getTagCompound();

        if (compound != null)
        {
            for (Map.Entry<String, String> entry : tagsDisallowed.entrySet())
            {
                if (checkNBT(compound, entry.getKey().split(":", -1), entry.getValue())) return false;
            }
        }


        //Required NBT
        Set<Map.Entry<String, String>> entrySet = tagsRequired.entrySet();
        if (entrySet.size() > 0)
        {
            if (compound == null) return false;

            for (Map.Entry<String, String> entry : entrySet)
            {
                if (!checkNBT(compound, entry.getKey().split(":", -1), entry.getValue())) return false;
            }
        }


        //Passed all filters
        return true;
    }

    private boolean checkNBT(NBTBase base, String[] keymap, String value)
    {
        if (keymap == null || keymap.length == 0)
        {
            return checkValue(base, value);
        }


        String key = keymap[0].trim();

        if (key.equals(""))
        {
            if (base instanceof NBTTagList)
            {
                for (NBTBase base1 : (NBTTagList) base)
                {
                    if (checkNBT(base1, Arrays.copyOfRange(keymap, 1, keymap.length), value)) return true;
                }
            }

            return false;
        }

        if (base instanceof NBTTagCompound)
        {
            NBTTagCompound compound = (NBTTagCompound) base;
            if (!compound.hasKey(key)) return false;

            return checkNBT(compound.getTag(key), Arrays.copyOfRange(keymap, 1, keymap.length), value);
        }

        return false;
    }

    private boolean checkValue(NBTBase base, String value)
    {
        if (value == null || value.trim().equals(base.toString())) return true;


        value = value.replace(";", ",");
        if (value.equals(base.toString())) return true;

        String[] newValues = value.split(",");
        if (newValues.length < 2 && !value.contains(":")) return false;

        for (String newValue : newValues)
        {
            String[] tokens = newValue.split(":");
            if (!checkNBT(base, Arrays.copyOf(tokens, tokens.length - 1), tokens[tokens.length - 1])) return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof RegistryRegexItemFilter)) return false;
        if (obj.getClass() != getClass()) return obj.equals(this);

        RegistryRegexItemFilter other = (RegistryRegexItemFilter) obj;
        if (!registryRegex.equals(other.registryRegex)) return false;

        if (tagsRequired.size() != other.tagsRequired.size()) return false;
        if (tagsDisallowed.size() != other.tagsDisallowed.size()) return false;

        for (Map.Entry<String, String> entry : tagsRequired.entrySet()) if (!entry.getValue().equals(other.tagsRequired.get(entry.getKey()))) return false;
        for (Map.Entry<String, String> entry : tagsDisallowed.entrySet()) if (!entry.getValue().equals(other.tagsDisallowed.get(entry.getKey()))) return false;

        return true;
    }
}
