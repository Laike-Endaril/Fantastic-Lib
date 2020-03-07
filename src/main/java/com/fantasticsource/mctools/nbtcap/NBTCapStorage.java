package com.fantasticsource.mctools.nbtcap;

import com.fantasticsource.api.INBTCap;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class NBTCapStorage implements Capability.IStorage<INBTCap>
{
    @Nullable
    @Override
    public NBTBase writeNBT(Capability<INBTCap> capability, INBTCap instance, EnumFacing side)
    {
        NBTTagCompound compound = new NBTTagCompound();
        for (String modid : instance.getRegisteredModIDs())
        {
            compound.setTag(modid, instance.getCompound(modid));
        }

        return compound;
    }

    @Override
    public void readNBT(Capability<INBTCap> capability, INBTCap instance, EnumFacing side, NBTBase nbt)
    {
        NBTTagCompound compound = (NBTTagCompound) nbt;
        for (String modid : compound.getKeySet())
        {
            instance.setCompound(modid, compound.getCompoundTag(modid));
        }
    }
}
