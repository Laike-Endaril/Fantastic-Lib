package com.fantasticsource.mctools;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraftforge.client.model.ModelLoader;

import java.util.HashMap;
import java.util.Map;

public class ModelReplacer
{
    public static void replaceModel(Block block, String newModelResourceLocation)
    {
        if (Minecraft.getMinecraft().entityRenderer != null) throw new IllegalStateException("ModelReplacer.replaceModel() called too late");
        ModelLoader.setCustomStateMapper(block, new RedirectedStateMapper(newModelResourceLocation));
    }

    public static class RedirectedStateMapper implements IStateMapper
    {
        String modelSourceRegistryName;

        public RedirectedStateMapper(String modelSourceRegistryName)
        {
            this.modelSourceRegistryName = modelSourceRegistryName;
        }

        @Override
        public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block)
        {
            HashMap<IBlockState, ModelResourceLocation> result = new HashMap<>();
            result.put(block.getDefaultState(), new ModelResourceLocation(modelSourceRegistryName));
            return result;
        }
    }
}
