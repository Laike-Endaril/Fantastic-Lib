package com.fantasticsource.mctools.enchantments;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Enchantments
{
    /**
     * Syntax is registryname.mode.level & registryname.mode.level & registryname.mode.level...
     * If mode is omitted, it defaults to 0
     * If level is omitted, it defaults to 1
     * If there is exactly one number, it is assumed to be level, not mode
     * <p>
     * Suggested numbering of modes:
     * 0: Vanilla enchantment behavior (note: vanilla does limit to max level when combining, even if an enchantment book is higher than max)
     * 1: Vanilla enchantment behavior, but without limits
     * 2: Overwrite level directly when applied instead of combining in any way
     * 3: Add, with limits of 0 -> max level; can be used to subtract levels if level is negative
     * 4: Add, without limits; can be used to subtract levels if level is negative
     */
    public static LinkedHashMap<Pair<Enchantment, Integer>, Integer> parseEnchantments(String enchantmentList)
    {
        String[] enchantments = enchantmentList.split("&");
        for (int i = 0; i < enchantments.length; i++) enchantments[i] = enchantments[i].trim();
        return parseEnchantments(enchantments);
    }

    /**
     * Syntax for each is registryname.level
     */
    public static LinkedHashMap<Pair<Enchantment, Integer>, Integer> parseEnchantments(String[] enchantmentList)
    {
        LinkedHashMap<Pair<Enchantment, Integer>, Integer> result = new LinkedHashMap<>();

        Pair<Pair<Enchantment, Integer>, Integer> enchantment;
        for (String string : enchantmentList)
        {
            enchantment = parseEnchantment(string);
            if (enchantment == null) continue;
            result.put(enchantment.getKey(), enchantment.getValue());
        }

        return result;
    }

    /**
     * Syntax is registryname.level
     */
    public static Pair<Pair<Enchantment, Integer>, Integer> parseEnchantment(String enchantmentString)
    {
        enchantmentString = enchantmentString.trim();
        if (enchantmentString.equals("")) return null;


        if (!enchantmentString.contains(":")) enchantmentString = "minecraft:" + enchantmentString;

        String regString = "";
        Enchantment enchantment = null;
        for (Map.Entry<ResourceLocation, Enchantment> entry : ForgeRegistries.ENCHANTMENTS.getEntries())
        {
            String testString = entry.getKey().toString();

            boolean match = testString.equals(enchantmentString);
            if (!match) match = testString.length() < enchantmentString.length() && enchantmentString.substring(0, testString.length()).equals(testString);

            if (match && testString.length() > regString.length())
            {
                regString = testString;
                enchantment = entry.getValue();
            }
        }
        if (regString.equals(""))
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.enchantmentNotFound", enchantmentString));
            return null;
        }


        enchantmentString = enchantmentString.replaceFirst(regString, "").replaceFirst("[.]", "").trim();
        String[] tokens = enchantmentString.equals("") ? new String[0] : enchantmentString.split(Pattern.quote("."));
        if (tokens.length > 2)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.tooManyEnchantmentArgs", enchantmentString));
            return null;
        }


        int mode = 0;
        int level = 1;

        if (tokens.length > 1)
        {
            try
            {
                mode = Integer.parseInt(tokens[0].trim());
            }
            catch (NumberFormatException e)
            {
                System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.enchantmentModeNotNumber", enchantmentString));
                return null;
            }


            String s = tokens[1].trim();
            if (s.equals("*")) level = enchantment.getMaxLevel();
            else
            {
                try
                {
                    level = Integer.parseInt(s);
                }
                catch (NumberFormatException e)
                {
                    System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.enchantmentLevelNotNumber", enchantmentString));
                    return null;
                }
            }
        }
        else if (tokens.length > 0)
        {
            String s = tokens[0].trim();
            if (s.equals("*")) level = enchantment.getMaxLevel();
            else
            {
                try
                {
                    level = Integer.parseInt(s);
                }
                catch (NumberFormatException e)
                {
                    System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.enchantmentLevelNotNumber", enchantmentString));
                    return null;
                }
            }
        }

        return new Pair<>(new Pair<>(enchantment, mode), level);
    }
}
