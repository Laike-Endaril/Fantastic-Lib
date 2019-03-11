package com.fantasticsource.mctools.potions;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.ArrayList;
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
    public static ArrayList<PotionEffect> parsePotions(String potionList, boolean allMaxDuration)
    {
        String[] potions = potionList.split("&");
        for (int i = 0; i < potions.length; i++) potions[i] = potions[i].trim();
        return parsePotions(potions, allMaxDuration);
    }

    /**
     * Syntax for each is registryname.duration.level
     */
    public static ArrayList<PotionEffect> parsePotions(String[] potionList)
    {
        return parsePotions(potionList, false);
    }

    /**
     * Syntax for each is registryname.duration.level
     */
    public static ArrayList<PotionEffect> parsePotions(String[] potionList, boolean allMaxDuration)
    {
        ArrayList<PotionEffect> result = new ArrayList<>();

        PotionEffect potion;
        for (String string : potionList)
        {
            potion = parsePotion(string, allMaxDuration);
            if (potion != null) result.add(potion);
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
     * Syntax is registryname.duration.level
     */
    public static PotionEffect parsePotion(String potionString, boolean maxDuration)
    {
        String[] tokens;
        int duration, amplifier;
        Potion potion;

        potionString = potionString.trim();
        if (potionString.equals("")) return null;

        tokens = potionString.split(Pattern.quote("."));
        if (tokens.length < 1)
        {
            System.err.println("Not enough arguments for potion effect; should be min of 1");
            return null;
        }
        if (tokens.length > 3)
        {
            System.err.println("Too many arguments for potion effect; should be max of 3");
            return null;
        }

        potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(tokens[0].trim()));
        if (potion == null)
        {
            System.err.println("ResourceLocation for potion \"" + potionString + "\" not found!");
            return null;
        }

        if (maxDuration)
        {
            duration = Integer.MAX_VALUE;
            amplifier = tokens.length > 1 ? Integer.parseInt(tokens[1].trim()) : 0;
            if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1
        }
        else
        {
            duration = tokens.length > 1 ? Integer.parseInt(tokens[1].trim()) : Integer.MAX_VALUE;
            amplifier = tokens.length > 2 ? Integer.parseInt(tokens[2].trim()) : 0;
            if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1
        }

        return new PotionEffect(potion, duration, amplifier, false, true);
    }
}
