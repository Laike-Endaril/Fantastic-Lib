package com.fantasticsource.mctools.data;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;

public abstract class CModpackDataHandler extends CDataHandler
{
    public static void load(FMLInitializationEvent event)
    {
        CDataHandler.load(CModpackDataHandler.class);
    }

    //No clear event, because modpack data just clears when you close the application
}
