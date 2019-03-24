package com.fantasticsource.mctools.attributes;

import net.minecraft.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;

public class AttributeMods
{
    /**
     * Syntax is attribute = amount @ operation, attribute = amount @ operation, attribute = amount @ operation...
     */
    public static ArrayList<AttributeModifier> parseMods(String modList)
    {
        String[] modifiers = modList.split("&");
        for (int i = 0; i < modifiers.length; i++) modifiers[i] = modifiers[i].trim();
        return parseMods(modifiers);
    }

    /**
     * Syntax for each is attribute = amount @ operation
     */
    public static ArrayList<AttributeModifier> parseMods(String[] modList)
    {
        ArrayList<AttributeModifier> result = new ArrayList<>();

        AttributeModifier modifier;
        for (String string : modList)
        {
            modifier = parseMod(string);
            if (modifier != null) result.add(modifier);
        }

        return result;
    }

    /**
     * Syntax is attribute = amount @ operation
     */
    public static AttributeModifier parseMod(String modString)
    {
        String[] tokens = modString.split("=");
        if (tokens.length < 2 || tokens.length > 3)
        {
            System.err.println("Malformed attribute modifier string: " + modString);
            return null;
        }

        String[] tokens2 = tokens[1].split("@");
        if (tokens2.length > 2)
        {
            System.err.println("Malformed attribute modifier string: " + modString);
            return null;
        }

        try
        {
            return new AttributeModifier(tokens[0].trim(), Double.parseDouble(tokens2[0].trim()), tokens2.length > 1 ? Integer.parseInt(tokens2[1].trim()) : 0);
        }
        catch (NumberFormatException e)
        {
            System.err.println("Malformed attribute modifier string: " + modString);
            return null;
        }
    }
}
