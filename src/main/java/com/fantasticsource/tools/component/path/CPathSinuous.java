package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathSinuous extends CPath
{
    public double thetaPerSec, thetaOffset;
    public VectorN amplitude;


    public CPathSinuous()
    {
    }

    public CPathSinuous(double thetaPerSec, VectorN amplitude)
    {
        this(thetaPerSec, 0, amplitude);
    }

    public CPathSinuous(double thetaPerSec, double thetaOffset, double... values)
    {
        this(thetaPerSec, thetaOffset, new VectorN(values));
    }

    public CPathSinuous(double thetaPerSec, double thetaOffset, VectorN amplitude)
    {
        this.thetaPerSec = thetaPerSec;
        this.thetaOffset = thetaOffset;
        this.amplitude = amplitude;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        double normalizedScalar = TrigLookupTable.TRIG_TABLE_1048576.sin(Math.PI * 2 * (thetaOffset + thetaPerSec * time / 1000));
        return amplitude.copy().scale(normalizedScalar);
    }


    @Override
    public CPathSinuous write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(amplitude).write(buf);
        buf.writeDouble(thetaPerSec);
        buf.writeDouble(thetaOffset);

        return this;
    }

    @Override
    public CPathSinuous read(ByteBuf buf)
    {
        super.read(buf);

        amplitude = new CVectorN().read(buf).value;
        thetaPerSec = buf.readDouble();
        thetaOffset = buf.readDouble();

        return this;
    }

    @Override
    public CPathSinuous save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(amplitude).save(stream);
        new CDouble().set(thetaPerSec).save(stream).set(thetaOffset).save(stream);

        return this;
    }

    @Override
    public CPathSinuous load(InputStream stream)
    {
        super.load(stream);

        CDouble cd = new CDouble();

        amplitude = new CVectorN().load(stream).value;
        thetaPerSec = cd.load(stream).value;
        thetaOffset = cd.load(stream).value;

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        for (int i = 0; i < amplitude.values.length; i++) compound.setDouble("amplitude" + i, amplitude.values[i]);
        compound.setDouble("n", amplitude.values.length);
        compound.setDouble("thetaPerSec", thetaPerSec);
        compound.setDouble("thetaOffset", thetaOffset);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        NBTTagCompound compound = (NBTTagCompound) nbt;

        int n = compound.getInteger("n");
        double[] values = new double[n];
        for (int i = 0; i < n; i++) values[i] = compound.getDouble("amplitude" + i);
    }
}
