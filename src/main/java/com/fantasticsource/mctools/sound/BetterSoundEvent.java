package com.fantasticsource.mctools.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class BetterSoundEvent extends SoundEvent
{
    public BetterSoundEvent(ResourceLocation soundName)
    {
        super(soundName);
        setRegistryName(soundName);
    }
}
