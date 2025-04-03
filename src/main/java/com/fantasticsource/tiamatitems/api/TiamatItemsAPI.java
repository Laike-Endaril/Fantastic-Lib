package com.fantasticsource.tiamatitems.api;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class TiamatItemsAPI
{
    private static Class tiamatItemsAPI = ReflectionTool.getClassByName("com.fantasticsource.tiamatitems.apinatives.TiamatItemsNatives");
    private static ITiamatItemsNatives tiamatItemsAPIMethods = tiamatItemsAPI == null ? null : (ITiamatItemsNatives) ReflectionTool.get(tiamatItemsAPI, "NATIVES", null);


    public static boolean isUsable(ItemStack stack)
    {
        return tiamatItemsAPIMethods == null || tiamatItemsAPIMethods.isUsable(stack);
    }

    public static ArrayList<IPartSlot> getPartSlots(ItemStack stack)
    {
        return tiamatItemsAPIMethods == null ? null : tiamatItemsAPIMethods.getPartSlots(stack);
    }
}
