package com.fantasticsource.tiamatinventory.api;

import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.UUID;

public class TiamatInventoryAPI
{
    private static LinkedHashMap<UUID, ITiamatPlayerInventory> tiamatServerInventories = null;

    static
    {
        try
        {
            for (Field field : Class.forName("com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory").getDeclaredFields())
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

    public static ITiamatPlayerInventory getTiamatPlayerInventory(EntityPlayer player)
    {
        if (tiamatServerInventories == null) return null;

        return tiamatServerInventories.getOrDefault(player.getPersistentID(), null);
    }
}
