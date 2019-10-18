package com.fantasticsource.mctools.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class SimpleClientSound implements ISound
{
    public final ResourceLocation LOCATION;
    public final SoundCategory CATEGORY;
    protected Sound sound;
    protected boolean repeat = false;
    protected int repeatDelay = 0;

    public SimpleClientSound(ResourceLocation location, SoundCategory category)
    {
        LOCATION = location;
        CATEGORY = category;
    }

    public SimpleClientSound(ResourceLocation location, SoundCategory category, int repeatDelay)
    {
        LOCATION = location;
        CATEGORY = category;
        repeat = true;
        this.repeatDelay = repeatDelay;
    }

    @Override
    public ResourceLocation getSoundLocation()
    {
        return LOCATION;
    }

    @Nullable
    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        SoundEventAccessor accessor = handler.getAccessor(LOCATION);

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
