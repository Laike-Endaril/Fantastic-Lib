package com.fantasticsource.fantasticlib;

import com.fantasticsource.mctools.MCTools;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FantasticLib.MODID, name = FantasticLib.NAME, version = FantasticLib.VERSION)
public class FantasticLib
{
    public static final String MODID = "fantasticlib";
    public static final String NAME = "Fantastic Lib";
    public static final String VERSION = "1.12.2.001a";

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (event.getSide() == Side.CLIENT) MCTools.clientInit();
    }
}
