package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathSinuous extends CPath
{
    public CPath highPointOffsetPath;
    public double thetaPerSec, thetaOffset;


    public CPathSinuous()
    {
    }

    public CPathSinuous(CPath highPointOffsetPath, double thetaPerSec)
    {
        this(highPointOffsetPath, thetaPerSec, 0);
    }

    public CPathSinuous(CPath highPointOffsetPath, double thetaPerSec, double thetaOffset)
    {
        this.highPointOffsetPath = highPointOffsetPath;
        this.thetaPerSec = thetaPerSec;
        this.thetaOffset = thetaOffset;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        double normalizedScalar = TrigLookupTable.TRIG_TABLE_1024.sin(Math.PI * 2 * (thetaOffset + thetaPerSec * time / 1000));
        return highPointOffsetPath.getRelativePosition(time).scale(normalizedScalar);
    }


    @Override
    public CPathSinuous write(ByteBuf buf)
    {
        super.write(buf);

        writeMarked(buf, highPointOffsetPath);
        buf.writeDouble(thetaPerSec);
        buf.writeDouble(thetaOffset);

        return this;
    }

    @Override
    public CPathSinuous read(ByteBuf buf)
    {
        super.read(buf);

        highPointOffsetPath = (CPath) readMarked(buf);
        thetaPerSec = buf.readDouble();
        thetaOffset = buf.readDouble();

        return this;
    }

    @Override
    public CPathSinuous save(OutputStream stream)
    {
        super.save(stream);

        saveMarked(stream, highPointOffsetPath);
        new CDouble().set(thetaPerSec).save(stream).set(thetaOffset).save(stream);

        return this;
    }

    @Override
    public CPathSinuous load(InputStream stream)
    {
        super.load(stream);

        CDouble cd = new CDouble();

        highPointOffsetPath = (CPath) loadMarked(stream);
        thetaPerSec = cd.load(stream).value;
        thetaOffset = cd.load(stream).value;

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        compound.setTag("highPointOffsetPath", serializeMarked(highPointOffsetPath));

        compound.setDouble("thetaPerSec", thetaPerSec);
        compound.setDouble("thetaOffset", thetaOffset);

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        NBTTagCompound compound = (NBTTagCompound) nbt;

        highPointOffsetPath = (CPath) deserializeMarked(compound.getCompoundTag("highPointOffsetPath"));
    }
}
