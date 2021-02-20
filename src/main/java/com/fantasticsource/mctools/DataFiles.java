package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class DataFiles
{
    private static String modDir = Loader.instance().getConfigDir().getAbsolutePath() + File.separator + FantasticLib.MODID + File.separator;
    private static String referenceDir = modDir + "reference" + File.separator;

    public static void output()
    {
        File file = new File(modDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir);
        if (!file.exists()) file.mkdir();

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(referenceDir + "entities.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.ENTITIES.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "items.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.ITEMS.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "blocks.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.BLOCKS.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "blockstates.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.BLOCKS.getKeys())
            {
                Block block = ForgeRegistries.BLOCKS.getValue(resourceLocation);

                ArrayList<IBlockState> states = new ArrayList<>();
                for (int i = 0; i < 16; i++)
                {
                    IBlockState state;
                    try
                    {
                        state = block.getStateFromMeta(i);
                    }
                    catch (Exception e)
                    {
                        continue;
                    }
                    if (!states.contains(state))
                    {
                        states.add(state);
                        writer.write(resourceLocation.toString() + ":" + i + "\r\n");
                    }
                }
            }
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "potions.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.POTIONS.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "potion_types.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.POTION_TYPES.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "recipes.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.RECIPES.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "biomes.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.BIOMES.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "enchantments.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.ENCHANTMENTS.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "sounds.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.SOUND_EVENTS.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "villager_professions.txt")));
            for (ResourceLocation resourceLocation : ForgeRegistries.VILLAGER_PROFESSIONS.getKeys()) writer.write(resourceLocation.toString() + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "oredict.txt")));
            for (String string : OreDictionary.getOreNames()) writer.write(string + "\r\n");
            writer.close();

            writer = new BufferedWriter(new FileWriter(new File(referenceDir + "slottings.txt")));
            for (String string : Slottings.availableSlottings()) writer.write(string + "\r\n");
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
