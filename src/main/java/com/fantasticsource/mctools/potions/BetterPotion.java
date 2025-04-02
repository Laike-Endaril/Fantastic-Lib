package com.fantasticsource.mctools.potions;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class BetterPotion extends Potion
{
    private final static ArrayList<BetterPotion> POTIONS_TO_ADD = new ArrayList<>();

    private static boolean initialized = false;

    public static void init()
    {
        if (initialized) return;

        MinecraftForge.EVENT_BUS.register(BetterPotion.class);
        initialized = true;
    }

    protected final ResourceLocation texture;
    protected final boolean isInstant;

    public BetterPotion(ResourceLocation name, ResourceLocation texture, boolean isBad, boolean isInstant, int liquidColor)
    {
        super(isBad, liquidColor);
        if (!isBad) setBeneficial();

        setRegistryName(name);
        setPotionName(name.getResourceDomain() + "." + name.getResourcePath());

        this.texture = texture;
        this.isInstant = isInstant;

        POTIONS_TO_ADD.add(this);

        init();
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


    @SubscribeEvent
    public static void registerPotions(RegistryEvent.Register<Potion> event)
    {
        IForgeRegistry<Potion> registry = event.getRegistry();
        for (BetterPotion potion : POTIONS_TO_ADD) registry.register(potion);
    }
}
