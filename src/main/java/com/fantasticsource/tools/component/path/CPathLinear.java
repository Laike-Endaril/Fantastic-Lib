package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathLinear extends CPath
{
    public VectorN offset, motionPerSecond;


    public CPathLinear()
    {
    }

    public CPathLinear(VectorN motionPerSecond)
    {
        this(motionPerSecond.copy().scale(0), motionPerSecond);
    }

    public CPathLinear(VectorN offset, VectorN motionPerSecond)
    {
        this.offset = offset;
        this.motionPerSecond = motionPerSecond;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        return motionPerSecond.copy().scale((double) time / 1000).add(offset);
    }


    @Override
    public CPathLinear write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(offset).write(buf).set(motionPerSecond).write(buf);

        return this;
    }

    @Override
    public CPathLinear read(ByteBuf buf)
    {
        super.read(buf);

        CVectorN cVec = new CVectorN();
        offset = cVec.read(buf).value;
        motionPerSecond = cVec.read(buf).value;

        return this;
    }

    @Override
    public CPathLinear save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(offset).save(stream).set(motionPerSecond).save(stream);

        return this;
    }

    @Override
    public CPathLinear load(InputStream stream)
    {
        super.load(stream);

        CVectorN cVec = new CVectorN();
        offset = cVec.load(stream).value;
        motionPerSecond = cVec.load(stream).value;

        return this;
    }
}
