package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathLinear extends CPath
{
    public VectorN motionPerSecond;


    public CPathLinear()
    {
    }

    public CPathLinear(VectorN motionPerSecond)
    {
        this.motionPerSecond = motionPerSecond;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        return motionPerSecond.copy().scale((double) time / 1000);
    }


    @Override
    public CPathLinear write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(motionPerSecond).write(buf);

        return this;
    }

    @Override
    public CPathLinear read(ByteBuf buf)
    {
        super.read(buf);

        motionPerSecond = new CVectorN().read(buf).value;

        return this;
    }

    @Override
    public CPathLinear save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(motionPerSecond).save(stream);

        return this;
    }

    @Override
    public CPathLinear load(InputStream stream)
    {
        super.load(stream);

        motionPerSecond = new CVectorN().load(stream).value;

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        if (motionPerSecond.values.length > 0)
        {
            NBTTagList list = new NBTTagList();
            for (double d : motionPerSecond.values) list.appendTag(new NBTTagDouble(d));
            compound.setTag("motionPerSecond", list);
        }

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        NBTTagCompound compound = (NBTTagCompound) nbt;

        motionPerSecond = new VectorN();
        if (compound.hasKey("motionPerSecond"))
        {
            NBTTagList list = (NBTTagList) compound.getTag("motionPerSecond");
            motionPerSecond.values = new double[list.tagCount()];
            for (int i = 0; i < motionPerSecond.values.length; i++) motionPerSecond.values[i] = list.getDoubleAt(i);
        }
    }
}
