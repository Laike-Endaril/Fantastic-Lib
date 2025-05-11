package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.Random;
import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathRandomConstant extends CPath
{
    public static final Random random = new Random();

    public VectorN position;


    public CPathRandomConstant()
    {
    }

    public CPathRandomConstant(double... values)
    {
        this(new VectorN(values));
    }

    public CPathRandomConstant(VectorN position)
    {
        this.position = position;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        if (PATH_DATA_STACK.isEmpty()) return position.copy();


        CPathData data = PATH_DATA_STACK.peek();
        if (data == null) return position.copy();


        random.setSeed(data.hashCode());
        VectorN result = position.copy();
        for (int i = 0; i < result.values.length; i++)
        {
            result.values[i] = result.values[i] * random.nextDouble();
        }
        return result;
    }


    @Override
    public CPathRandomConstant write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(position).write(buf);

        return this;
    }

    @Override
    public CPathRandomConstant read(ByteBuf buf)
    {
        super.read(buf);

        position = new CVectorN().read(buf).value;

        return this;
    }

    @Override
    public CPathRandomConstant save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(position).save(stream);

        return this;
    }

    @Override
    public CPathRandomConstant load(InputStream stream)
    {
        super.load(stream);

        position = new CVectorN().load(stream).value;

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        if (position.values.length > 0)
        {
            NBTTagList list = new NBTTagList();
            for (double d : position.values) list.appendTag(new NBTTagDouble(d));
            compound.setTag("position", list);
        }

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        NBTTagCompound compound = (NBTTagCompound) nbt;

        position = new VectorN();
        if (compound.hasKey("position"))
        {
            NBTTagList list = (NBTTagList) compound.getTag("position");
            position.values = new double[list.tagCount()];
            for (int i = 0; i < position.values.length; i++) position.values[i] = list.getDoubleAt(i);
        }
    }
}
