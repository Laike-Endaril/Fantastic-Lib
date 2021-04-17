package com.fantasticsource.mctools.betterattributes;

import com.fantasticsource.fantasticlib.api.ABetterAttribute;
import com.fantasticsource.mctools.MCTools;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;

public class ConvertedAttribute extends ABetterAttribute
{
    public final IAttribute mcAttribute;

    public ConvertedAttribute(IAttribute mcAttribute, boolean isGood)
    {
        super(mcAttribute.getName(), isGood, mcAttribute.getDefaultValue(), false);
        this.mcAttribute = mcAttribute;
    }

    @Override
    public double calculateTotalAmount(Entity entity)
    {
        if (entity instanceof EntityLivingBase) return MCTools.getAttribute((EntityLivingBase) entity, mcAttribute);
        return defaultBaseAmount;
    }
}
