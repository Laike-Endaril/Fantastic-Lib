package com.fantasticsource.mctools.potions;

import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;

public class BetterPotionType extends PotionType
{
    private static final ArrayList<BetterPotionType> POTION_TYPES_TO_ADD = new ArrayList<>();

    private static boolean initialized = false;

    public static void init()
    {
        if (initialized) return;

        MinecraftForge.EVENT_BUS.register(BetterPotionType.class);
        initialized = true;
    }


    protected PotionType recipeBasePotion;
    protected ItemStack recipeAddedItem;

    public BetterPotionType(PotionType recipeBasePotion, ItemStack recipeAddedItem, PotionEffect potionEffect)
    {
        this(potionEffect.getEffectName().replaceAll("(.*)\\..*", "$1"), potionEffect.getEffectName().replaceAll(".*\\.(.*)", "$1"), recipeBasePotion, recipeAddedItem, potionEffect);
    }

    public BetterPotionType(String modID, String name, PotionType recipeBasePotion, ItemStack recipeAddedItem, PotionEffect... potionEffects)
    {
        super(modID + "." + name, potionEffects);

        this.recipeBasePotion = recipeBasePotion;
        this.recipeAddedItem = recipeAddedItem;

        setRegistryName(modID, name);

        POTION_TYPES_TO_ADD.add(this);

        init();
    }


    @SubscribeEvent
    public static void registerPotionTypes(RegistryEvent.Register<PotionType> event)
    {
        IForgeRegistry<PotionType> registry = event.getRegistry();
        for (BetterPotionType potionType : POTION_TYPES_TO_ADD)
        {
            registry.register(potionType);
            if (potionType.recipeBasePotion != null && potionType.recipeAddedItem != null) BrewingRecipeRegistry.addRecipe(new BetterBrewingRecipe(potionType));
        }
    }
}
