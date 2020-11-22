package com.fantasticsource.tiamatitems.api;

import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class TiamatItemsAPI
{
    private static ITiamatItemsNatives tiamatItemsAPIMethods = null;

    static
    {
        try
        {
            for (Field field : Class.forName("com.fantasticsource.tiamatitems.apinatives.TiamatItemsNatives").getDeclaredFields())
            {
                if (field.getName().equals("NATIVES"))
                {
                    tiamatItemsAPIMethods = (ITiamatItemsNatives) field.get(null);
                }
            }
        }
        catch (ClassNotFoundException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


    public static ArrayList<IPartSlot> getPartSlots(ItemStack stack)
    {
        return tiamatItemsAPIMethods == null ? null : tiamatItemsAPIMethods.getPartSlots(stack);
    }
}
