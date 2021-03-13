package com.fantasticsource.mctools.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BetterPotion extends Potion
{
    private final ResourceLocation texture;
    private final boolean isInstant;

    protected BetterPotion(ResourceLocation name, ResourceLocation texture, boolean isBad, boolean isInstant, int liquidColor)
    {
        super(isBad, liquidColor);
        if (!isBad) setBeneficial();

        setRegistryName(name);
        setPotionName(name.getResourceDomain() + "." + name.getResourcePath());

        this.texture = texture;
        this.isInstant = isInstant;
    }


    @Override
    public boolean isInstant()
    {
        return isInstant;
    }


    @SideOnly(Side.CLIENT)
    @Override
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc)
    {
        if (mc.currentScreen != null)
        {
            mc.getTextureManager().bindTexture(texture);
            Gui.drawModalRectWithCustomSizedTexture(x + 6, y + 7, 0, 0, 18, 18, 18, 18);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha)
    {
        mc.getTextureManager().bindTexture(texture);
        Gui.drawModalRectWithCustomSizedTexture(x + 3, y + 3, 0, 0, 18, 18, 18, 18);
    }
}
