package net.minecraftforge.common.config;

public class ConfigHack
{
    public static Configuration getConfiguration(String modid)
    {
        return getConfiguration(modid, null);
    }

    public static Configuration getConfiguration(String modid, String name)
    {
        return ConfigManager.getConfiguration(modid, name);
    }
}
