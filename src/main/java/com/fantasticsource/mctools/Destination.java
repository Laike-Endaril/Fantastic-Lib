package com.fantasticsource.mctools;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class Destination
{
    public Integer dimension;
    public double x, y, z;
    public Float yaw, pitch;


    public Destination(Entity entity)
    {
        this(entity.world.provider.getDimension(), entity.posX, entity.posY, entity.posZ, entity.getRotationYawHead(), entity.rotationPitch);
    }

    public Destination(BlockPos pos)
    {
        this(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
    }

    public Destination(World world, float yaw, float pitch)
    {
        this(world.provider.getDimension(), world.getWorldInfo().getSpawnX() + 0.5, world.getWorldInfo().getSpawnY(), world.getWorldInfo().getSpawnZ() + 0.5, yaw, pitch);
    }

    public Destination(double x, double y, double z)
    {
        this(x, y, z, null, null);
    }

    public Destination(double x, double y, double z, Float yaw, Float pitch)
    {
        this(null, x, y, z, yaw, pitch);
    }

    public Destination(Integer dimension, double x, double y, double z, Float yaw, Float pitch)
    {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public Destination(NBTTagCompound compound)
    {
        this(compound.getDouble("x"), compound.getDouble("y"), compound.getDouble("z"));
        if (compound.hasKey("dim")) setDimension(compound.getInteger("dim"));
        if (compound.hasKey("yaw")) setRotation(compound.getFloat("yaw"), compound.getFloat("pitch"));
    }

    public void addNBTTo(NBTTagCompound compound)
    {
        if (dimension != null) compound.setTag("dim", new NBTTagInt(dimension));
        compound.setTag("x", new NBTTagDouble(x));
        compound.setTag("y", new NBTTagDouble(y));
        compound.setTag("z", new NBTTagDouble(z));
        if (yaw != null) compound.setTag("yaw", new NBTTagDouble(yaw));
        if (pitch != null) compound.setTag("pitch", new NBTTagDouble(pitch));
    }

    public Destination setDimension(Integer dimension)
    {
        this.dimension = dimension;
        return this;
    }

    public Destination setPosition(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public Destination setPosition(BlockPos pos)
    {
        return setPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public Destination setRotation(Float yaw, Float pitch)
    {
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }


    @Override
    public String toString()
    {
        return dimension + ", (" + x + ", " + y + ", " + z + "), (" + yaw + ", " + pitch + ")";
    }
}
