package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.DataFiles;
import com.fantasticsource.mctools.EntityRenderFixer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.TooltipFixer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FantasticLib.MODID, name = FantasticLib.NAME, version = FantasticLib.VERSION)
public class FantasticLib
{
    public static final String MODID = "fantasticlib";
    public static final String NAME = "Fantastic Lib";
    public static final String VERSION = "1.12.2.009a";

    public FantasticLib()
    {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            if (FantasticConfig.entityRenderFixer) MinecraftForge.EVENT_BUS.register(EntityRenderFixer.class);
            MinecraftForge.EVENT_BUS.register(TooltipFixer.class);
        }
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        if (event.getSide() == Side.CLIENT) Render.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        DataFiles.output();
    }
}
