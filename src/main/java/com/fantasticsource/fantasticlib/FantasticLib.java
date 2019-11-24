package com.fantasticsource.fantasticlib;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.*;
import com.fantasticsource.mctools.gui.screen.TestGUI;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = FantasticLib.MODID, name = FantasticLib.NAME, version = FantasticLib.VERSION, acceptableRemoteVersions = "*")
public class FantasticLib
{
    public static final String MODID = "fantasticlib";
    public static final String NAME = "Fantastic Lib";
    public static final String VERSION = "1.12.2.027";


    public static boolean isClient = false;
    private static boolean debugGui;

    static
    {
        try
        {
            debugGui = ReflectionTool.getField(ItemStack.class, "stackSize") != null;
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    public FantasticLib()
    {
        MinecraftForge.EVENT_BUS.register(FantasticLib.class);

        Network.init();

        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
        {
            //Physical client
            isClient = true;
            if (FantasticConfig.entityRenderFixer) MinecraftForge.EVENT_BUS.register(EntityRenderFixer.class);
            MinecraftForge.EVENT_BUS.register(TooltipFixer.class);
            if (debugGui) MinecraftForge.EVENT_BUS.register(TestGUI.class);
        }

        MinecraftForge.EVENT_BUS.register(PlayerData.class);
    }


    @SubscribeEvent
    public static void saveConfig(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if (event.getModID().equals(MODID)) ConfigManager.sync(MODID, Config.Type.INSTANCE);
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
        if (Loader.isModLoaded("betterportals")) Compat.betterportals = true;
        if (Loader.isModLoaded("smoothfont")) Compat.smoothfont = true;
    }
}
