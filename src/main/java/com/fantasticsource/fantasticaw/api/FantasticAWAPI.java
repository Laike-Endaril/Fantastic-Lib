package com.fantasticsource.fantasticaw.api;

import net.minecraft.entity.Entity;

import java.lang.reflect.Field;

public class FantasticAWAPI
{
    private static IFantasticAWNatives fantasticAWAPIMethods = null;

    static
    {
        try
        {
            for (Field field : Class.forName("com.fantasticsource.fantasticaw.apinatives.FantasticAWAPI").getDeclaredFields())
            {
                if (field.getName().equals("NATIVES"))
                {
                    fantasticAWAPIMethods = (IFantasticAWNatives) field.get(null);
                }
            }
        }
        catch (ClassNotFoundException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


    public static void refreshRenderModes(Entity entity)
    {
        if (fantasticAWAPIMethods != null) fantasticAWAPIMethods.refreshRenderModes(entity);
    }
}
