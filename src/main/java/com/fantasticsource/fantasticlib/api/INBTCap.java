package com.fantasticsource.fantasticlib.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.function.Predicate;

public interface INBTCap
{
    LinkedHashMap<String, Predicate<Entity>> entityPredicates = new LinkedHashMap<>();
    LinkedHashMap<String, Predicate<ItemStack>> stackPredicates = new LinkedHashMap<>();
    LinkedHashMap<String, Predicate<World>> worldPredicates = new LinkedHashMap<>();
    LinkedHashMap<String, Predicate<TileEntity>> tePredicates = new LinkedHashMap<>();
    LinkedHashMap<String, Predicate<Chunk>> chunkPredicates = new LinkedHashMap<>();
    LinkedHashMap<String, Predicate<Village>> villagePredicates = new LinkedHashMap<>();

    HashSet<String> registeredModIDs = new HashSet<>();


    String[] getRegisteredModIDs();

    NBTTagCompound getCompound(String modid);

    void setCompound(String modid, NBTTagCompound compound);
}
