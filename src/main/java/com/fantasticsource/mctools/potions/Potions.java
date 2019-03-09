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
     * Syntax is registryname.duration.level & registrlyname.duration.level & registrlyname.duration.level...
     */
    public static ArrayList<PotionEffect> parsePotions(String potionList)
    {
        String[] potions = potionList.split("&");
        for (int i = 0; i < potions.length; i++) potions[i] = potions[i].trim();
        return parsePotions(potions);
    }

    /**
     * Syntax for each is registryname.duration.level
     */
    public static ArrayList<PotionEffect> parsePotions(String[] potionList)
    {
        ArrayList<PotionEffect> result = new ArrayList<>();

        PotionEffect potion;
        for (String string : potionList)
        {
            potion = parsePotion(string);
            if (potion != null) result.add(potion);
        }

        return result;
    }

    /**
     * Syntax is registryname.duration.level
     */
    public static PotionEffect parsePotion(String potionString)
    {
        String[] tokens;
        int duration, amplifier;
        Potion potion;

        potionString = potionString.trim();
        if (potionString.equals("")) return null;

        tokens = potionString.split(Pattern.quote("."));
        if (tokens.length <= 0) return null;
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

        duration = tokens.length > 1 ? Integer.parseInt(tokens[1].trim()) : 0;
        amplifier = tokens.length > 2 ? Integer.parseInt(tokens[2].trim()) : 0;
        if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1

        return new PotionEffect(potion, duration, amplifier, false, true);
    }
}
