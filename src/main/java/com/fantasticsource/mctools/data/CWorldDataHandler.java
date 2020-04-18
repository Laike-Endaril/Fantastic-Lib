package com.fantasticsource.mctools.data;

import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

public abstract class CWorldDataHandler extends CDataHandler
{
    public static void load(FMLServerStartingEvent event)
    {
        CDataHandler.load(CWorldDataHandler.class);
    }

    public static void clear(FMLServerStoppedEvent event)
    {
        CDataHandler.clear(CWorldDataHandler.class);
    }
}
