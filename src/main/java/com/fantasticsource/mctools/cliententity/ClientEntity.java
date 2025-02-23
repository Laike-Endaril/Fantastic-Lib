package com.fantasticsource.mctools.cliententity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientEntity extends Entity
{
    public float rotationYawHead;

    public ClientEntity(World worldIn)
    {
        super(worldIn);
        setEntityId(-1);
    }

    @Override
    protected void entityInit()
    {
    }

    @Override
    public void setRotationYawHead(float rotationYawHead)
    {
        this.rotationYawHead = rotationYawHead;
    }

    @Override
    public float getRotationYawHead()
    {
        return rotationYawHead;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
    }
}
