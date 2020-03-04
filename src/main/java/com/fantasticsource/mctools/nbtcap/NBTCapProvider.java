package com.fantasticsource.mctools.nbtcap;

import com.fantasticsource.api.INBTCap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import static com.fantasticsource.api.FLibAPI.NBT_CAP;

public class NBTCapProvider implements ICapabilitySerializable<NBTTagCompound>
{
    protected INBTCap instance = new NBTCap();

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == NBT_CAP;
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        return capability == NBT_CAP ? NBT_CAP.cast(instance) : null;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        return (NBTTagCompound) NBT_CAP.getStorage().writeNBT(NBT_CAP, instance, null);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        NBT_CAP.getStorage().readNBT(NBT_CAP, instance, null, nbt);
    }
}
