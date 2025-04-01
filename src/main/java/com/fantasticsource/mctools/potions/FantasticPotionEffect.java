package com.fantasticsource.mctools.potions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class FantasticPotionEffect extends PotionEffect
{
    //Found in SPacketEntityEffect, when transferring potion effects from server to client
    public static final int MAX_DURATION_THRESHOLD = 32767;

    public int interval = 0;

    public FantasticPotionEffect(Potion potionIn)
    {
        super(potionIn);
    }

    public FantasticPotionEffect(Potion potionIn, int durationIn)
    {
        super(potionIn, durationIn);
    }

    public FantasticPotionEffect(Potion potionIn, int durationIn, int amplifierIn)
    {
        super(potionIn, durationIn, amplifierIn);
    }

    public FantasticPotionEffect(Potion potionIn, int durationIn, int amplifierIn, boolean ambientIn, boolean showParticlesIn)
    {
        super(potionIn, durationIn, amplifierIn, ambientIn, showParticlesIn);
    }

    public FantasticPotionEffect(PotionEffect other)
    {
        super(other);
    }

    public FantasticPotionEffect setInterval(int interval)
    {
        this.interval = interval;
        return this;
    }


    @Override
    public void combine(PotionEffect other)
    {
        //isPotionDurationMax, setPotionDurationMax(), and getPotionDurationMax() are client-side-only
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && getIsPotionDurationMax() && (other.getAmplifier() > getAmplifier()))
        {
            setPotionDurationMax(false);
            super.combine(other);
            if (getDuration() >= MAX_DURATION_THRESHOLD) setPotionDurationMax(true);
        }
        else super.combine(other);
    }

    @Override
    public boolean onUpdate(EntityLivingBase entityIn)
    {
        if (getDuration() >= 32767)
        {
            //Skip decrementation if we're supposed to be infinite
            if (getPotion().isReady(getDuration(), getAmplifier())) performEffect(entityIn);
            return true;
        }
        else return super.onUpdate(entityIn);
    }

    @Override
    public boolean equals(Object obj)
    {
        return super.equals(obj) && interval == ((FantasticPotionEffect) obj).interval;
    }
}
