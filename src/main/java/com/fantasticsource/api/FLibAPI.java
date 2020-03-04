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


    static void attachNBTCapToEntityIf(Predicate<Entity> predicate)
    {
        INBTCap.entityPredicates.add(predicate);
    }

    void attachNBTCapToStackIf(Predicate<ItemStack> predicate)
    {
        INBTCap.stackPredicates.add(predicate);
    }

    void attachNBTCapToWorldIf(Predicate<World> predicate)
    {
        INBTCap.worldPredicates.add(predicate);
    }

    void attachNBTCapToTEIf(Predicate<TileEntity> predicate)
    {
        INBTCap.tePredicates.add(predicate);
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
