package com.fantasticsource.tiamatrpg.api;

import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.UUID;

public class TiamatRPGAPI
{
    private static LinkedHashMap<UUID, ITiamatPlayerInventory> tiamatServerInventories = null;

    public static ITiamatPlayerInventory getTiamatPlayerInventory(EntityPlayer player)
    {
        if (tiamatServerInventories == null)
        {
            try
            {
                for (Field field : Class.forName("com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory").getDeclaredFields())
                {
                    if (field.getName().equals("tiamatServerInventories"))
                    {
                        tiamatServerInventories = (LinkedHashMap<UUID, ITiamatPlayerInventory>) field.get(null);
                    }
                }
            }
            catch (ClassNotFoundException | IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }

        if (tiamatServerInventories == null) return null;

        return tiamatServerInventories.getOrDefault(player.getPersistentID(), null);
    }
}
