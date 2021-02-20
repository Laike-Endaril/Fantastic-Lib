package com.fantasticsource.fantasticlib.config;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

@Config(modid = MODID)
public class FantasticConfig
{
    @Config.Name("Use Entity Render Fixer")
    @Config.LangKey(MODID + ".config.entityRenderFixer")
    @Config.Comment(
            {
                    "If enabled, runs some code to reset certain openGL settings after each entity renders",
                    "Only use this if you need it to fix a graphical issue"
            })
    @Config.RequiresMcRestart
    public static boolean entityRenderFixer = false;

    @Config.Name("Use Inventory Desync Fixer")
    @Config.LangKey(MODID + ".config.inventoryDesyncFixer")
    @Config.Comment(
            {
                    "If enabled, runs some code to synchronize inventory to client when it changes server-side"
            })
    public static boolean inventoryDesyncFixer = false;

    @Config.Name("Preview Render Mode Overrides")
    @Config.LangKey(MODID + ".config.previewRenderModeOverrides")
    @Config.Comment(
            {
                    "Overrides for render modes when previewing AW skins on items",
                    "Syntax is...",
                    "Slotting, Channel@Mode, Channel@Mode, etc",
                    "Valid entries for 'Slotting' can be found in config/fantasticlib/reference/slottings.txt",
                    "Valid channels and modes depend on the AW skins on the server (ask server admin)",
                    "Eg. if Tiamat Inventory is installed and a server has valid shoulder skins for it...",
                    "Tiamat Shoulders, ShoulderL@On, ShoulderR@On, ShoulderLControl@On, ShoulderRControl@On"
            })
    public static String[] previewRenderModeOverrides = new String[0];

    @Config.Name("GUI Settings")
    @Config.LangKey(MODID + ".config.guiSettings")
    public static GUIConfig guiSettings = new GUIConfig();

    @Config.Name("Raytrace Settings")
    @Config.LangKey(MODID + ".config.raytraceSettings")
    public static RaytraceConfig raytraceSettings = new RaytraceConfig();
}
