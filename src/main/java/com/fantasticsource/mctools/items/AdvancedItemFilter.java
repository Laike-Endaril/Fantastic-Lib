package com.fantasticsource.mctools.items;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.Tools;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

//Supports oredict
//Supports regex for each of domain, item, and meta (including oredict domains and oredict items)
//Caches state and does more efficient checks based on it
public class AdvancedItemFilter
{
    public static final String REGEX_ANY = ".*";

    protected String domainCheck, itemCheck, metaCheck;
    protected boolean domainIsRegex, itemIsRegex, metaIsRegex;
    protected int meta;
    protected LinkedHashMap<String, String> tagsRequired, tagsDisallowed;

    protected int lastCacheOreDictSize = 0;
    protected ArrayList<Integer> matchingOredictIDs = new ArrayList<>();


    public AdvancedItemFilter()
    {
        this(".*", ".*", ".*", new LinkedHashMap<>(), new LinkedHashMap<>());
    }

    public AdvancedItemFilter(@Nullable String domainRegex, @Nullable String itemRegex, @Nullable String metaRegex, @Nullable LinkedHashMap<String, String> tagsRequired, @Nullable LinkedHashMap<String, String> tagsDisallowed)
    {
        set(domainRegex, itemRegex, metaRegex, tagsRequired, tagsDisallowed);
    }


    public void set(String domainRegex, String itemRegex, String metaRegex, LinkedHashMap<String, String> tagsRequired, LinkedHashMap<String, String> tagsDisallowed)
    {
        if (domainCheck == null) domainCheck = REGEX_ANY;
        else
        {
            domainCheck = domainRegex.trim();
            if (domainCheck.isEmpty()) domainCheck = REGEX_ANY;
        }

        if (itemCheck == null) itemCheck = REGEX_ANY;
        else
        {
            itemCheck = itemRegex.trim();
            if (itemCheck.isEmpty()) itemCheck = REGEX_ANY;
        }

        if (metaCheck == null) metaCheck = REGEX_ANY;
        else
        {
            metaCheck = metaRegex.trim();
            if (metaCheck.isEmpty()) metaCheck = REGEX_ANY;
        }

        this.tagsRequired = tagsRequired != null && tagsRequired.size() == 0 ? null : tagsRequired;
        this.tagsDisallowed = tagsDisallowed != null && tagsDisallowed.size() == 0 ? null : tagsDisallowed;

        resetAllCaches();
    }

    public void resetAllCaches()
    {
        domainIsRegex = domainCheck == null || Tools.hasRegexSpecialCharacters(domainCheck);
        itemIsRegex = itemCheck == null || Tools.hasRegexSpecialCharacters(itemCheck);
        metaIsRegex = metaCheck == null || Tools.hasRegexSpecialCharacters(metaCheck);
        if (!metaIsRegex) meta = Integer.parseInt(metaCheck);

        //TODO cache matching items (not stacks)

        //TODO cache matching stacks within last x time?

        //TODO cache size of each valid oredict array (change lastCacheOreDictSize to an array or hashmap of sizes)


        //TODO copy all cache values to clone in clone() method
    }


    public String getDomainRegex()
    {
        return domainCheck;
    }

    public String getItemRegex()
    {
        return itemCheck;
    }

    public String getMetaRegex()
    {
        return metaCheck;
    }

    public LinkedHashMap<String, String> getTagsRequired()
    {
        if (tagsRequired == null) return new LinkedHashMap<>();
        return new LinkedHashMap<>(tagsRequired);
    }

    public LinkedHashMap<String, String> getTagsDisallowed()
    {
        if (tagsDisallowed == null) return new LinkedHashMap<>();
        return new LinkedHashMap<>(tagsDisallowed);
    }


    /**
     * Syntax is domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2
     * All of these are optional except item
     * Supports oredict
     * For domain, item, and meta, regex can be used, but each missing token is set to default
     * Ie. to match all items, it is .*:.*:.* not just .* (which would only match all vanilla items with 0 meta)
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
    public static AdvancedItemFilter getInstance(String itemStackString)
    {
        AdvancedItemFilter result = new AdvancedItemFilter();

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
        String[] regexTokens = registryAndNBT[0].trim().split(":");
        if (regexTokens.length == 0 || regexTokens.length > 3)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.badItemName", registryAndNBT[0].trim()));
            return null;
        }
        if (regexTokens.length == 1)
        {
            result.domainCheck = ".*";
            result.itemCheck = regexTokens[0].trim();
            result.metaCheck = ".*";
        }
        else if (regexTokens.length == 2)
        {
            if (Tools.regexMatches(".*[a-zA-Z].*", regexTokens[1]))
            {
                result.domainCheck = regexTokens[0];
                result.itemCheck = regexTokens[1];
                result.metaCheck = ".*";
            }
            else if (Tools.regexMatches(".*[0-9].*", regexTokens[1]))
            {
                result.domainCheck = ".*";
                result.itemCheck = regexTokens[0];
                result.metaCheck = regexTokens[1];
            }
            else
            {
                result.domainCheck = regexTokens[0];
                result.itemCheck = regexTokens[1];
                result.metaCheck = ".*";
            }
        }
        else
        {
            result.domainCheck = regexTokens[0].trim();
            result.itemCheck = regexTokens[1].trim();
            result.metaCheck = regexTokens[2].trim();
        }

        if (result.domainCheck.equals("")) result.domainCheck = ".*";
        if (result.itemCheck.equals("")) result.itemCheck = ".*";
        if (result.metaCheck.equals("")) result.metaCheck = ".*";


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

        result.set(result.domainCheck, result.itemCheck, result.metaCheck, result.tagsRequired, result.tagsDisallowed);
        return result;
    }


    public boolean matches(ItemStack stack)
    {
        //Meta
        if (!checkMeta(stack.getMetadata())) return false;

        //Domain, item
        ResourceLocation resourceLocation = stack.getItem().getRegistryName();
        if (!checkDomain(resourceLocation.getResourceDomain()) || !checkItem(resourceLocation.getResourcePath()))
        {
            //Oredict checks
            if (stack.isEmpty() || !checkDomain("ore")) return false;


            //Add any missing oreDict IDs to cache
            String[] oreDictNames = OreDictionary.getOreNames();
            for (int i = lastCacheOreDictSize; i < oreDictNames.length; i++)
            {
                if (checkItem(oreDictNames[i])) matchingOredictIDs.add(i);
            }

            //Check matching oreDict entries
            boolean found = false;
            for (int oreDictID : OreDictionary.getOreIDs(stack))
            {
                if (matchingOredictIDs.contains(oreDictID))
                {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }


        //Disallowed NBT
        NBTTagCompound compound = stack.getTagCompound();

        if (compound != null && tagsDisallowed != null)
        {
            for (Map.Entry<String, String> entry : tagsDisallowed.entrySet())
            {
                if (checkNBT(compound, entry.getKey().split(":", -1), entry.getValue())) return false;
            }
        }


        //Required NBT
        if (tagsRequired != null && tagsRequired.size() > 0)
        {
            if (compound == null) return false;


            for (Map.Entry<String, String> entry : tagsRequired.entrySet())
            {
                if (!checkNBT(compound, entry.getKey().split(":", -1), entry.getValue())) return false;
            }
        }


        //Passed all filters
        return true;
    }

    protected boolean checkDomain(String domain)
    {
        if (domainIsRegex) return Tools.regexMatches(domainCheck, domain);
        return domain.equals(domainCheck);
    }

    protected boolean checkItem(String item)
    {
        if (itemIsRegex) return Tools.regexMatches(itemCheck, item);
        return item.equals(itemCheck);
    }

    protected boolean checkMeta(int stackMeta)
    {
        if (metaIsRegex) return Tools.regexMatches(metaCheck, "" + stackMeta);
        return stackMeta == meta;
    }


    protected boolean checkNBT(NBTBase base, String[] keymap, String value)
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

    protected boolean checkValue(NBTBase base, String value)
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
        if (!(obj instanceof AdvancedItemFilter)) return false;
        if (obj.getClass() != getClass()) return obj.equals(this);

        AdvancedItemFilter other = (AdvancedItemFilter) obj;
        if (!domainCheck.equals(other.domainCheck) || !itemCheck.equals(other.itemCheck) || !metaCheck.equals(other.metaCheck)) return false;

        if (tagsRequired.size() != other.tagsRequired.size()) return false;
        if (tagsDisallowed.size() != other.tagsDisallowed.size()) return false;

        for (Map.Entry<String, String> entry : tagsRequired.entrySet()) if (!entry.getValue().equals(other.tagsRequired.get(entry.getKey()))) return false;
        for (Map.Entry<String, String> entry : tagsDisallowed.entrySet()) if (!entry.getValue().equals(other.tagsDisallowed.get(entry.getKey()))) return false;

        return true;
    }


    public AdvancedItemFilter clone()
    {
        AdvancedItemFilter other = new AdvancedItemFilter();

        other.domainCheck = domainCheck;
        other.itemCheck = itemCheck;
        other.metaCheck = metaCheck;

        for (Map.Entry<String, String> entry : tagsRequired.entrySet()) other.tagsRequired.put(entry.getKey(), entry.getValue());
        for (Map.Entry<String, String> entry : tagsDisallowed.entrySet()) other.tagsDisallowed.put(entry.getKey(), entry.getValue());

        other.lastCacheOreDictSize = lastCacheOreDictSize;
        other.matchingOredictIDs.addAll(matchingOredictIDs); //This is probably actually copying memory addresses, but the values of the Integer objects never change anyway (the objects get replaced instead)


        other.domainIsRegex = domainIsRegex;
        other.itemIsRegex = itemIsRegex;
        other.metaIsRegex = metaIsRegex;

        //TODO copy all cache values to clone


        return other;
    }

    @Override
    public String toString()
    {
        String result = domainCheck + ":" + itemCheck + ":" + metaCheck;

        int i = 0;
        for (Map.Entry<String, String> entry : tagsRequired.entrySet())
        {
            result += i++ == 0 ? " > " : " & ";
            result += entry.getKey();
            if (entry.getValue() != null) result += " = " + entry.getValue();
        }

        for (Map.Entry<String, String> entry : tagsDisallowed.entrySet())
        {
            result += i++ == 0 ? " > " : " & ";
            result += "!" + entry.getKey();
            if (entry.getValue() != null) result += " = " + entry.getValue();
        }

        return result;
    }
}
