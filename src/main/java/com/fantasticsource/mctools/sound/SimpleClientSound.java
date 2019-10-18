package com.fantasticsource.mctools.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class SimpleClientSound implements ISound
{
    public final ResourceLocation RL;
    public final SoundCategory CATEGORY;
    protected Sound sound;
    protected boolean repeat = false;
    protected int repeatDelay = 0;

    public SimpleClientSound(IForgeRegistry<SoundEvent> registry, ResourceLocation rl, SoundCategory category)
    {
        RL = rl;
        CATEGORY = category;

        registry.register(new SoundEvent(rl).setRegistryName(rl));
    }

    public SimpleClientSound(IForgeRegistry<SoundEvent> registry, ResourceLocation rl, SoundCategory category, int repeatDelay)
    {
        RL = rl;
        CATEGORY = category;
        repeat = true;
        this.repeatDelay = repeatDelay;

        registry.register(new SoundEvent(rl).setRegistryName(rl));
    }

    @Override
    public ResourceLocation getSoundLocation()
    {
        return RL;
    }

    @Nullable
    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        SoundEventAccessor accessor = handler.getAccessor(RL);

        if (accessor == null)
        {
            sound = SoundHandler.MISSING_SOUND;
        }
        else
        {
            sound = accessor.cloneEntry();
        }

        return accessor;
    }

    @Override
    public Sound getSound()
    {
        return sound;
    }

    @Override
    public SoundCategory getCategory()
    {
        return CATEGORY;
    }

    @Override
    public boolean canRepeat()
    {
        return repeat;
    }

    @Override
    public int getRepeatDelay()
    {
        return repeatDelay;
    }

    @Override
    public float getVolume()
    {
        return sound.getVolume();
    }

    @Override
    public float getPitch()
    {
        return sound.getPitch();
    }

    @Override
    public float getXPosF()
    {
        return (float) Minecraft.getMinecraft().player.posX;
    }

    @Override
    public float getYPosF()
    {
        return (float) Minecraft.getMinecraft().player.posY;
    }

    @Override
    public float getZPosF()
    {
        return (float) Minecraft.getMinecraft().player.posZ;
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.NONE;
    }
}
