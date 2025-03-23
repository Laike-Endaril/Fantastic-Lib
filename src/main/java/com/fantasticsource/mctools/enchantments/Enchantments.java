package com.fantasticsource.mctools.enchantments;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Enchantments
{
    /**
     * Syntax is registryname.level & registryname.level & registryname.level...
     */
    public static HashMap<Enchantment, Integer> parseEnchantments(String enchantmentList)
    {
        String[] enchantments = enchantmentList.split("&");
        for (int i = 0; i < enchantments.length; i++) enchantments[i] = enchantments[i].trim();
        return parseEnchantments(enchantments);
    }

    /**
     * Syntax for each is registryname.level
     */
    public static HashMap<Enchantment, Integer> parseEnchantments(String[] enchantmentList)
    {
        HashMap<Enchantment, Integer> result = new HashMap<>();

        Pair<Enchantment, Integer> enchantment;
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
    public static Pair<Enchantment, Integer> parseEnchantment(String enchantmentString)
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
        if (tokens.length > 1)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.tooManyEnchantmentArgs", enchantmentString));
            return null;
        }

        int level = 1;
        if (tokens.length > 0)
        {
            String levelStr = tokens[0].trim();
            if (levelStr.equals("*")) level = enchantment.getMaxLevel();
            else
            {
                try
                {
                    level = Integer.parseInt(tokens[0].trim());
                }
                catch (NumberFormatException e)
                {
                    System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.enchantmentLevelNotNumber", enchantmentString));
                    return null;
                }
            }
        }

        return new Pair<>(enchantment, level);
    }
}
