package com.fantasticsource.mctools.blocks;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class RegistryRegexBlockFilter
{
    public String domainRegex, blockRegex, metaRegex;
    public int lastCacheOreDictSize = 0;
    public ArrayList<Integer> matchingOredictIDs = new ArrayList<>();


    public RegistryRegexBlockFilter()
    {
    }

    public RegistryRegexBlockFilter(String domainRegex, String blockRegex, String metaRegex)
    {
        this.domainRegex = domainRegex;
        this.blockRegex = blockRegex;
        this.metaRegex = metaRegex;
    }


    /**
     * Syntax is domain:block:meta
     * All of these are optional except block
     * Supports oredict
     * For domain, block, and meta, regex can be used, but each missing token is set to default
     * Ie. to match all blocks, it is .*:.*:.* not just .* (which would only match all vanilla blocks with 0 meta)
     * <p>
     * Examples...
     * diamond_block
     * sponge:1
     */
    public static RegistryRegexBlockFilter getInstance(String blockStateString)
    {
        RegistryRegexBlockFilter result = new RegistryRegexBlockFilter();

        //Registry regex
        String[] regexTokens = blockStateString.trim().split(":");
        if (regexTokens.length == 0 || regexTokens.length > 3)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.badBlockName", blockStateString.trim()));
            return null;
        }
        if (regexTokens.length == 1)
        {
            result.domainRegex = "minecraft";
            result.blockRegex = regexTokens[0].trim();
            result.metaRegex = "0";
        }
        else if (regexTokens.length == 2)
        {
            if (Pattern.matches(".*[a-zA-Z].*", regexTokens[1]))
            {
                result.domainRegex = regexTokens[0];
                result.blockRegex = regexTokens[1];
                result.metaRegex = ".*";
            }
            else if (Pattern.matches(".*[0-9].*", regexTokens[1]))
            {
                result.domainRegex = ".*";
                result.blockRegex = regexTokens[0];
                result.metaRegex = regexTokens[1];
            }
            else
            {
                result.domainRegex = regexTokens[0];
                result.blockRegex = regexTokens[1];
                result.metaRegex = ".*";
            }
        }
        else
        {
            result.domainRegex = regexTokens[0].trim();
            result.blockRegex = regexTokens[1].trim();
            result.metaRegex = regexTokens[2].trim();
        }

        if (result.domainRegex.equals("")) result.domainRegex = ".*";
        if (result.blockRegex.equals("")) result.blockRegex = ".*";
        if (result.metaRegex.equals("")) result.metaRegex = ".*";


        return result;
    }


    public boolean matches(IBlockState state)
    {
        //Domain, block, and meta
        if (!Pattern.matches(metaRegex, "" + state.getBlock().getMetaFromState(state))) return false; //Quickest check first

        ResourceLocation resourceLocation = state.getBlock().getRegistryName();
        if (!Pattern.matches(domainRegex, resourceLocation.getResourceDomain()) || !Pattern.matches(blockRegex, resourceLocation.getResourcePath()))
        {
            //Oredict checks
            if (state.getBlock() != Blocks.AIR && Pattern.matches(domainRegex, "ore"))
            {
                //Add any missing oreDict IDs to cache
                String[] oreDictNames = OreDictionary.getOreNames();
                for (int i = lastCacheOreDictSize; i < oreDictNames.length; i++)
                {
                    if (Pattern.matches(blockRegex, oreDictNames[i])) matchingOredictIDs.add(i);
                }

                //Check matching oreDict entries
                boolean found = false;
                for (int oreDictID : OreDictionary.getOreIDs(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state))))
                {
                    if (matchingOredictIDs.contains(oreDictID))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found) return false;
            }
            else return false;
        }


        //Passed all filters
        return true;
    }


    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (!(obj instanceof RegistryRegexBlockFilter)) return false;
        if (obj.getClass() != getClass()) return obj.equals(this);

        RegistryRegexBlockFilter other = (RegistryRegexBlockFilter) obj;
        return domainRegex.equals(other.domainRegex) && blockRegex.equals(other.blockRegex) && metaRegex.equals(other.metaRegex);
    }
}
