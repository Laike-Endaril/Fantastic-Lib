package com.fantasticsource.mctools.potions;

import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public class FantasticPotionEffect extends PotionEffect
{
    protected int interval = 0;

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
}
