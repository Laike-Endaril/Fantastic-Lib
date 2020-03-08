package com.fantasticsource.api;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.function.Predicate;

public class FLibAPI
{
    @CapabilityInject(INBTCap.class)
    public static final Capability<INBTCap> NBT_CAP = null;


    public static void attachNBTCapToEntityIf(String modid, Predicate<Entity> predicate)
    {
        INBTCap.registeredModIDs.add(modid);
        INBTCap.entityPredicates.put(modid, predicate);
    }

    public static void attachNBTCapToStackIf(String modid, Predicate<ItemStack> predicate)
    {
        INBTCap.registeredModIDs.add(modid);
        INBTCap.stackPredicates.put(modid, predicate);
    }

    public static void attachNBTCapToWorldIf(String modid, Predicate<World> predicate)
    {
        INBTCap.registeredModIDs.add(modid);
        INBTCap.worldPredicates.put(modid, predicate);
    }

    public static void attachNBTCapToTEIf(String modid, Predicate<TileEntity> predicate)
    {
        INBTCap.registeredModIDs.add(modid);
        INBTCap.tePredicates.put(modid, predicate);
    }


    public static INBTCap getNBTCap(Entity entity)
    {
        return entity.getCapability(NBT_CAP, null);
    }

    public static INBTCap getNBTCap(ItemStack stack)
    {
        return stack.getCapability(NBT_CAP, null);
    }

    public static INBTCap getNBTCap(World world)
    {
        return world.getCapability(NBT_CAP, null);
    }

    public static INBTCap getNBTCap(TileEntity te)
    {
        return te.getCapability(NBT_CAP, null);
    }
}
