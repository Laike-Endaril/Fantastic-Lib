package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FantasticLib.MODID, name = FantasticLib.NAME, version = FantasticLib.VERSION, acceptableRemoteVersions = "*")
public class FantasticLib
{
    public static final String MODID = "fantasticlib";
    public static final String NAME = "Fantastic Lib";
    public static final String VERSION = "1.12.2.021b";

    public FantasticLib()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            if (FantasticConfig.entityRenderFixer) MinecraftForge.EVENT_BUS.register(EntityRenderFixer.class);
            MinecraftForge.EVENT_BUS.register(TooltipFixer.class);
        }

        MinecraftForge.EVENT_BUS.register(PlayerData.class);
    }


    @EventHandler
    public static void serverStart(FMLServerAboutToStartEvent event)
    {
        MCTools.serverStart(event);
    }

    @EventHandler
    public static void serverStop(FMLServerStoppedEvent event)
    {
        MCTools.serverStop(event);
    }


    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        PlayerData.load();

        if (event.getSide() == Side.CLIENT) Render.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        DataFiles.output();
    }
}
