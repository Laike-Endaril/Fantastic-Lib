package com.fantasticsource.tiamatinventory.api;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.UUID;

public class TiamatInventoryAPI
{
    private static Field clientInventoryField;
    private static LinkedHashMap<UUID, ITiamatPlayerInventory> tiamatServerInventories = null;

    static
    {
        try
        {
            Class c = ReflectionTool.getClassByName("com.fantasticsource.tiamatinventory.inventory.TiamatPlayerInventory");
            clientInventoryField = ReflectionTool.getField(c, "tiamatClientInventory");
            tiamatServerInventories = (LinkedHashMap<UUID, ITiamatPlayerInventory>) ReflectionTool.get(c, "tiamatServerInventories", null);
        }
        catch (Exception e)
        {
            if (Loader.isModLoaded("tiamatinventory")) e.printStackTrace();
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
