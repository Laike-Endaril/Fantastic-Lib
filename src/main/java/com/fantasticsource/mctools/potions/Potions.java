package com.fantasticsource.mctools.potions;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Pattern;

public class Potions
{
    /**
     * Syntax is registryname.duration.level & registryname.duration.level & registryname.duration.level...
     */
    public static ArrayList<PotionEffect> parsePotions(String potionList)
    {
        return parsePotions(potionList, false);
    }

    /**
     * Syntax is registryname.duration.level & registryname.duration.level & registryname.duration.level...
     */
    public static ArrayList<PotionEffect> parsePotions(String[] potionList)
    {
        return parsePotions(potionList, false);
    }

    /**
     * Syntax if ampFirst is true is registryname.level.duration & registryname.level.duration & registryname.level.duration...
     * Syntax if ampFirst is false is registryname.duration.level & registryname.duration.level & registryname.duration.level...
     */
    public static ArrayList<PotionEffect> parsePotions(String potionList, boolean ampFirst)
    {
        String[] potions = potionList.split("&");
        for (int i = 0; i < potions.length; i++) potions[i] = potions[i].trim();
        return parsePotions(potions, ampFirst);
    }

    /**
     * If ampFirst is true, syntax for each is registryname.level.duration
     * If ampFirst is false, syntax for each is registryname.duration.level
     */
    public static ArrayList<PotionEffect> parsePotions(String[] potionList, boolean ampFirst)
    {
        ArrayList<PotionEffect> result = new ArrayList<>();

        PotionEffect potion;
        for (String string : potionList)
        {
            potion = parsePotion(string, ampFirst);
            if (potion == null) continue;
            result.add(potion);
        }

        return result;
    }

    /**
     * Syntax is registryname.duration.level
     */
    public static PotionEffect parsePotion(String potionString)
    {
        return parsePotion(potionString, false);
    }

    /**
     * If ampFirst is true, syntax is registryname.level.duration
     * If ampFirst is false, syntax is registryname.duration.level
     */
    public static PotionEffect parsePotion(String potionString, boolean ampFirst)
    {
        potionString = potionString.trim();
        if (potionString.equals("")) return null;

        if (!potionString.contains(":")) potionString = "minecraft:" + potionString;

        String regString = "";
        Potion potion = null;
        for (Map.Entry<ResourceLocation, Potion> entry : ForgeRegistries.POTIONS.getEntries())
        {
            String testString = entry.getKey().toString();

            boolean match = testString.equals(potionString);
            if (!match) match = testString.length() < potionString.length() && potionString.substring(0, testString.length()).equals(testString);

            if (match && testString.length() > regString.length())
            {
                regString = testString;
                potion = entry.getValue();
            }
        }
        if (regString.equals(""))
        {
            System.err.println(I18n.format(FantasticLib.MODID + ".error.potionNotFound", potionString));
            return null;
        }

        potionString = potionString.replace(regString, "").trim().replace(".", "");
        String[] tokens = potionString.equals("") ? new String[0] : potionString.replace(regString, "").split(Pattern.quote("."));
        if (tokens.length > 2)
        {
            System.err.println(I18n.format(FantasticLib.MODID + ".error.tooManyPotionArgs", potionString));
            return null;
        }

        int duration = Integer.MAX_VALUE, amplifier = 0;
        if (ampFirst)
        {
            if (tokens.length > 0)
            {
                String ampStr = tokens[0].trim();
                if (ampStr.equals("*")) amplifier = Integer.MAX_VALUE;
                else
                {
                    try
                    {
                        amplifier = Integer.parseInt(tokens[0].trim());
                        if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for strength 2 instead of typing 1
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println(I18n.format(FantasticLib.MODID + ".error.potionAmpNotNumber", potionString));
                        return null;
                    }
                }
            }

            if (tokens.length > 1)
            {
                String durStr = tokens[1].trim();
                if (durStr.equals("*")) duration = Integer.MAX_VALUE;
                else
                {
                    try
                    {
                        duration = Integer.parseInt(tokens[1].trim());
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println(I18n.format(FantasticLib.MODID + ".error.potionDurNotNumber", potionString));
                        return null;
                    }
                }
            }
        }
        else
        {
            if (tokens.length > 0)
            {
                String durStr = tokens[0].trim();
                if (durStr.equals("*")) duration = Integer.MAX_VALUE;
                else
                {
                    try
                    {
                        duration = Integer.parseInt(tokens[0].trim());
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println(I18n.format(FantasticLib.MODID + ".error.potionDurNotNumber", potionString));
                        return null;
                    }
                }
            }

            if (tokens.length > 1)
            {
                String ampStr = tokens[1].trim();
                if (ampStr.equals("*")) amplifier = Integer.MAX_VALUE;
                else
                {
                    try
                    {
                        amplifier = Integer.parseInt(tokens[1].trim());
                        if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for strength 2 instead of typing 1
                    }
                    catch (NumberFormatException e)
                    {
                        System.err.println(I18n.format(FantasticLib.MODID + ".error.potionAmpNotNumber", potionString));
                        return null;
                    }
                }
            }
        }

        return new PotionEffect(potion, duration, amplifier, false, true);
    }
}
