package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;

public class ConvertedAttribute extends BetterAttribute
{
    public final IAttribute mcAttribute;

    public ConvertedAttribute(IAttribute mcAttribute)
    {
        super(mcAttribute.getName(), mcAttribute.getDefaultValue());
        canUseTotalAmountCaching = false;
        this.mcAttribute = mcAttribute;
    }

    @Override
    public double getBaseAmount(Entity entity)
    {
        if (entity instanceof EntityLivingBase) return MCTools.getAttribute((EntityLivingBase) entity, mcAttribute);
        return defaultBaseAmount;
    }
}
