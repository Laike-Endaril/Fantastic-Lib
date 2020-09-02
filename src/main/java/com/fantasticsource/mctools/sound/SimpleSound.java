package com.fantasticsource.mctools.sound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@SideOnly(Side.CLIENT)
public class SimpleSound implements ISound
{
    public final ResourceLocation rl;
    public final SoundCategory category;
    protected Sound sound;
    protected boolean repeat;
    protected int repeatDelay;
    protected final Entity following;
    protected final float x, y, z;

    public SimpleSound(ResourceLocation rl, SoundCategory category)
    {
        this(rl, category, 0);
    }

    public SimpleSound(ResourceLocation rl, SoundCategory category, @Nonnull Entity following)
    {
        this(rl, category, 0, following);
    }

    public SimpleSound(ResourceLocation rl, SoundCategory category, float x, float y, float z)
    {
        this(rl, category, 0, x, y, z);
    }

    public SimpleSound(ResourceLocation rl, SoundCategory category, int repeatDelay)
    {
        this(rl, category, repeatDelay, Minecraft.getMinecraft().player);
    }

    public SimpleSound(ResourceLocation rl, SoundCategory category, int repeatDelay, @Nonnull Entity following)
    {
        x = 0;
        y = 0;
        z = 0;

        this.rl = rl;
        this.category = category;
        this.repeatDelay = repeatDelay;
        this.following = following;

        repeat = repeatDelay > 0;
    }

    public SimpleSound(ResourceLocation rl, SoundCategory category, int repeatDelay, float x, float y, float z)
    {
        following = null;

        this.rl = rl;
        this.category = category;
        this.repeatDelay = repeatDelay;

        this.x = x;
        this.y = y;
        this.z = z;

        repeat = repeatDelay > 0;
    }

    @SideOnly(Side.CLIENT)
    public static void play(ResourceLocation rl)
    {
        Minecraft.getMinecraft().getSoundHandler().playSound(new SimpleSound(rl, SoundCategory.MASTER));
    }

    @Override
    public ResourceLocation getSoundLocation()
    {
        return rl;
    }

    @Nullable
    @Override
    public SoundEventAccessor createAccessor(SoundHandler handler)
    {
        SoundEventAccessor accessor = handler.getAccessor(rl);

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
        return category;
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
        return following == null ? x : (float) following.posX;
    }

    @Override
    public float getYPosF()
    {
        return following == null ? y : (float) following.posY;
    }

    @Override
    public float getZPosF()
    {
        return following == null ? z : (float) following.posZ;
    }

    @Override
    public AttenuationType getAttenuationType()
    {
        return AttenuationType.LINEAR;
    }
}
