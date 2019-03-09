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
     * Syntax is registrlyname.duration.level & registrlyname.duration.level & registrlyname.duration.level...
     */
    public static ArrayList<PotionEffect> parsePotions(String potionList)
    {
        String[] potions = potionList.split("&");
        for (int i = 0; i < potions.length; i++) potions[i] = potions[i].trim();
        return parsePotions(potions);
    }

    /**
     * Syntax for each is registrlyname.duration.level
     */
    public static ArrayList<PotionEffect> parsePotions(String[] potionList)
    {
        String[] tokens;
        int duration, amplifier;
        Potion potion;
        ArrayList<PotionEffect> result = new ArrayList<>();

        for (String string : potionList)
        {
            string = string.trim();
            if (!string.equals(""))
            {
                tokens = string.split(Pattern.quote("."));
                if (tokens.length > 3)
                {
                    System.err.println("Too many arguments for potion effect; should be max of 3");
                    continue;
                }

                duration = 0;
                amplifier = 0;

                if (tokens.length > 0)
                {
                    potion = ForgeRegistries.POTIONS.getValue(new ResourceLocation(tokens[0].trim()));

                    if (potion == null)
                    {
                        System.err.println("ResourceLocation for potion \"" + string + "\" not found!");
                        continue;
                    }

                    if (tokens.length > 1) duration = Integer.parseInt(tokens[1].trim());
                    if (tokens.length > 2) amplifier = Integer.parseInt(tokens[2].trim());
                    if (amplifier > 0) amplifier--; //Makes it so ppl can just type 2 for stength 2 instead of typing 1

                    result.add(new PotionEffect(potion, duration, amplifier, false, true));
                }
            }
        }

        return result;
    }
}
