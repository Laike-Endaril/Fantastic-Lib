package com.fantasticsource.mctools.blocks;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.Tools;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;

import static com.fantasticsource.tools.Tools.REGEX_ANY;

//Supports oredict
//Supports regex for each of domain, block, and meta (including oredict domains and oredict blocks)
//Caches state and does more efficient checks based on it
public class AdvancedBlockFilter
{
    protected String domainCheck, blockCheck, metaCheck;
    protected boolean domainIsRegex, blockIsRegex, metaIsRegex;
    protected int meta;

    protected int lastCacheOreDictSize = 0;
    protected ArrayList<Integer> matchingOredictIDs = new ArrayList<>();
    protected LinkedHashMap<Block, Boolean> cachedBlockResults = new LinkedHashMap<>();


    public AdvancedBlockFilter()
    {
        this(".*", ".*", ".*");
    }

    public AdvancedBlockFilter(@Nullable String domainRegex, @Nullable String blockRegex, @Nullable String metaRegex)
    {
        set(domainRegex, blockRegex, metaRegex);
    }


    public void set(String domainRegex, String blockRegex, String metaRegex)
    {
        if (domainCheck == null) domainCheck = REGEX_ANY;
        else
        {
            domainCheck = domainRegex.trim();
            if (domainCheck.isEmpty()) domainCheck = REGEX_ANY;
        }

        if (blockCheck == null) blockCheck = REGEX_ANY;
        else
        {
            blockCheck = blockRegex.trim();
            if (blockCheck.isEmpty()) blockCheck = REGEX_ANY;
        }

        if (metaCheck == null) metaCheck = REGEX_ANY;
        else
        {
            metaCheck = metaRegex.trim();
            if (metaCheck.isEmpty()) metaCheck = REGEX_ANY;
        }

        resetAllCaches();
    }

    public void resetAllCaches()
    {
        domainIsRegex = domainCheck == null || Tools.hasRegexSpecialCharacters(domainCheck);
        blockIsRegex = blockCheck == null || Tools.hasRegexSpecialCharacters(blockCheck);
        metaIsRegex = metaCheck == null || Tools.hasRegexSpecialCharacters(metaCheck);
        if (!metaIsRegex) meta = Integer.parseInt(metaCheck);


        cachedBlockResults.clear();

        //TODO If further improvements are necessary, check the list below
        //TODO If any further improvements are made, make sure to copy all cache values to clone in clone() method

        //TODO cache matching stacks within last x time?
        //TODO cache size of each valid oredict array (change lastCacheOreDictSize to an array or hashmap of sizes)
    }


    public String getDomainRegex()
    {
        return domainCheck;
    }

    public String getBlockRegex()
    {
        return blockCheck;
    }

    public String getMetaRegex()
    {
        return metaCheck;
    }


    /**
     * Syntax is domain:block:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2
     * All of these are optional except block
     * Supports oredict
     * For domain, block, and meta, regex can be used, but each missing token is set to default
     * Ie. to match all blocks, it is .*:.*:.* not just .* (which would only match all vanilla blocks with 0 meta)
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
    public static AdvancedBlockFilter getInstance(String blockStateString)
    {
        AdvancedBlockFilter result = new AdvancedBlockFilter();

        //Registry regex
        String[] regexTokens = blockStateString.trim().split(":");
        if (regexTokens.length == 0 || regexTokens.length > 3)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.badBlockName", blockStateString.trim()));
            return null;
        }
        if (regexTokens.length == 1)
        {
            result.domainCheck = ".*";
            result.blockCheck = regexTokens[0].trim();
            result.metaCheck = ".*";
        }
        else if (regexTokens.length == 2)
        {
            if (Tools.regexMatches(".*[a-zA-Z].*", regexTokens[1]))
            {
                result.domainCheck = regexTokens[0];
                result.blockCheck = regexTokens[1];
                result.metaCheck = ".*";
            }
            else if (Tools.regexMatches(".*[0-9].*", regexTokens[1]))
            {
                result.domainCheck = ".*";
                result.blockCheck = regexTokens[0];
                result.metaCheck = regexTokens[1];
            }
            else
            {
                result.domainCheck = regexTokens[0];
                result.blockCheck = regexTokens[1];
                result.metaCheck = ".*";
            }
        }
        else
        {
            result.domainCheck = regexTokens[0].trim();
            result.blockCheck = regexTokens[1].trim();
            result.metaCheck = regexTokens[2].trim();
        }

        if (result.domainCheck.equals("")) result.domainCheck = ".*";
        if (result.blockCheck.equals("")) result.blockCheck = ".*";
        if (result.metaCheck.equals("")) result.metaCheck = ".*";


        result.set(result.domainCheck, result.blockCheck, result.metaCheck);
        return result;
    }


    public boolean matches(IBlockState state)
    {
        if (state == null) return false;


        //Block (Domain, name)
        Block block = state.getBlock();
        int stateMeta = block.getMetaFromState(state);
        Boolean cachedBlockCheck = cachedBlockResults.get(block);
        if (cachedBlockCheck == null)
        {
            ResourceLocation resourceLocation = block.getRegistryName();
            if (resourceLocation == null)
            {
                cachedBlockResults.put(block, false);
                return false;
            }


            if (!checkDomain(resourceLocation.getResourceDomain()) || !checkBlock(resourceLocation.getResourcePath()))
            {
                //Oredict checks
                if (block == Blocks.AIR || !checkDomain("ore"))
                {
                    cachedBlockResults.put(block, false);
                    return false;
                }


                //Add any missing oreDict IDs to cache
                String[] oreDictNames = OreDictionary.getOreNames();
                for (int i = lastCacheOreDictSize; i < oreDictNames.length; i++)
                {
                    if (checkBlock(oreDictNames[i])) matchingOredictIDs.add(i);
                }

                //Check matching oreDict entries
                boolean found = false;
                ItemStack stack = new ItemStack(state.getBlock(), 1, stateMeta);
                if (!stack.isEmpty())
                {
                    for (int oreDictID : OreDictionary.getOreIDs(stack))
                    {
                        if (matchingOredictIDs.contains(oreDictID))
                        {
                            found = true;
                            break;
                        }
                    }
                }
                if (!found)
                {
                    cachedBlockResults.put(block, false);
                    return false;
                }
            }
            cachedBlockResults.put(block, true);
        }
        else if (!cachedBlockCheck) return false;


        //Meta
        if (!checkMeta(stateMeta)) return false;


        //Passed all filters
        return true;
    }

    protected boolean checkDomain(String domain)
    {
        if (domainIsRegex) return Tools.regexMatches(domainCheck, domain);
        return domainCheck.equals(domain);
    }

    protected boolean checkBlock(String block)
    {
        if (blockIsRegex) return Tools.regexMatches(blockCheck, block);
        return blockCheck.equals(block);
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
        if (!(obj instanceof AdvancedBlockFilter)) return false;
        if (obj.getClass() != getClass()) return obj.equals(this);

        AdvancedBlockFilter other = (AdvancedBlockFilter) obj;
        if (!domainCheck.equals(other.domainCheck) || !blockCheck.equals(other.blockCheck) || !metaCheck.equals(other.metaCheck)) return false;

        return true;
    }


    public AdvancedBlockFilter clone()
    {
        AdvancedBlockFilter other = new AdvancedBlockFilter();

        other.domainCheck = domainCheck;
        other.blockCheck = blockCheck;
        other.metaCheck = metaCheck;

        other.lastCacheOreDictSize = lastCacheOreDictSize;
        other.matchingOredictIDs.addAll(matchingOredictIDs); //This is probably actually copying memory addresses, but the values of the Integer objects never change anyway (the objects get replaced instead)


        other.domainIsRegex = domainIsRegex;
        other.blockIsRegex = blockIsRegex;
        other.metaIsRegex = metaIsRegex;

        other.cachedBlockResults.putAll(cachedBlockResults);

        //TODO copy all cache values to clone


        return other;
    }

    @Override
    public String toString()
    {
        return domainCheck + ":" + blockCheck + ":" + metaCheck;
    }
}
