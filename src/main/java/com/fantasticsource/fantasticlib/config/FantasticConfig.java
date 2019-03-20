package com.fantasticsource.fantasticlib.config;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraftforge.common.config.Config;

@Config(modid = FantasticLib.MODID)
public class FantasticConfig
{
    @Config.Name("Use Entity Render Fixer")
    @Config.LangKey(FantasticLib.MODID + ".config.entityRenderFixer")
    @Config.RequiresMcRestart
    public static boolean entityRenderFixer = false;
}
