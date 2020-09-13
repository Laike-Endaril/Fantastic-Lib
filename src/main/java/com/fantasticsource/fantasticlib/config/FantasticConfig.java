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

    @Config.Name("GUI Settings")
    @Config.LangKey(MODID + ".config.guiSettings")
    public static GUIConfig guiSettings = new GUIConfig();
}
