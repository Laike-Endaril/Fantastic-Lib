package com.fantasticsource.tiamatinventory.api;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.player.EntityPlayer;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.UUID;

public class TiamatInventoryAPI
{
    private static Field clientInventoryField = null;
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
                else if (field.getName().equals("tiamatClientInventory"))
                {
                    clientInventoryField = field;
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
        if (player.world.isRemote)
        {
            return clientInventoryField == null ? null : (ITiamatPlayerInventory) ReflectionTool.get(clientInventoryField, null);
        }

        if (tiamatServerInventories == null) return null;

        return tiamatServerInventories.getOrDefault(player.getPersistentID(), null);
    }
}
