package com.fantasticsource.fantasticaw.api;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;

public class FantasticAWAPI
{
    private static IFantasticAWNatives fantasticAWAPIMethods = null;

    static
    {
        try
        {
            fantasticAWAPIMethods = (IFantasticAWNatives) ReflectionTool.get(ReflectionTool.getClassByName("com.fantasticsource.fantasticaw.apinatives.FantasticAWAPI"), "NATIVES", null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void refreshRenderModes(Entity entity)
    {
        if (fantasticAWAPIMethods != null) fantasticAWAPIMethods.refreshRenderModes(entity);
    }
}
