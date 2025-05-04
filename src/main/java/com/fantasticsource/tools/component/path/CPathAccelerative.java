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

public class CPathAccelerative extends CPath
{
    public VectorN motionPerSecondPerSecond;


    public CPathAccelerative()
    {
    }

    public CPathAccelerative(double... values)
    {
        this(new VectorN(values));
    }

    public CPathAccelerative(VectorN motionPerSecondPerSecond)
    {
        this.motionPerSecondPerSecond = motionPerSecondPerSecond;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        double t2 = (double) time / 1000;
        t2 *= t2;
        return motionPerSecondPerSecond.copy().scale(0.5 * t2);
    }


    @Override
    public CPathAccelerative write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(motionPerSecondPerSecond).write(buf);

        return this;
    }

    @Override
    public CPathAccelerative read(ByteBuf buf)
    {
        super.read(buf);

        motionPerSecondPerSecond = new CVectorN().read(buf).value;

        return this;
    }

    @Override
    public CPathAccelerative save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(motionPerSecondPerSecond).save(stream);

        return this;
    }

    @Override
    public CPathAccelerative load(InputStream stream)
    {
        super.load(stream);

        motionPerSecondPerSecond = new CVectorN().load(stream).value;

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        if (motionPerSecondPerSecond.values.length > 0)
        {
            NBTTagList list = new NBTTagList();
            for (double d : motionPerSecondPerSecond.values) list.appendTag(new NBTTagDouble(d));
            compound.setTag("motionPerSecondPerSecond", list);
        }

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        NBTTagCompound compound = (NBTTagCompound) nbt;

        motionPerSecondPerSecond = new VectorN();
        if (compound.hasKey("motionPerSecondPerSecond"))
        {
            NBTTagList list = (NBTTagList) compound.getTag("motionPerSecondPerSecond");
            motionPerSecondPerSecond.values = new double[list.tagCount()];
            for (int i = 0; i < motionPerSecondPerSecond.values.length; i++) motionPerSecondPerSecond.values[i] = list.getDoubleAt(i);
        }
    }
}
