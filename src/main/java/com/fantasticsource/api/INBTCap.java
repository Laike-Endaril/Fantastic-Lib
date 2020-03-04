package com.fantasticsource.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.function.Predicate;

public interface INBTCap
{
    ArrayList<Predicate<Entity>> entityPredicates = new ArrayList<>();
    ArrayList<Predicate<ItemStack>> stackPredicates = new ArrayList<>();
    ArrayList<Predicate<World>> worldPredicates = new ArrayList<>();
    ArrayList<Predicate<TileEntity>> tePredicates = new ArrayList<>();


    NBTTagCompound getCompound();

    void setCompound(NBTTagCompound compound);
}
