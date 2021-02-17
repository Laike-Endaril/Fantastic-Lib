package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathSinuous extends CPath
{
    public VectorN center, highPoint;
    public double normalizedProgressPerSec, normalizedProgressOffset;

    public CPathSinuous(VectorN center, VectorN highPoint, double normalizedProgressPerSec)
    {
        this(center, highPoint, normalizedProgressPerSec, 0);
    }

    public CPathSinuous(VectorN center, VectorN highPoint, double normalizedProgressPerSec, double normalizedProgressOffset)
    {
        this.center = center;
        this.highPoint = highPoint;
        this.normalizedProgressPerSec = normalizedProgressPerSec;
        this.normalizedProgressOffset = normalizedProgressOffset;
    }


    @Override
    public VectorN getRelativePosition(long time)
    {
        double normalizedScalar = TrigLookupTable.TRIG_TABLE_1024.sin(Math.PI * 2 * (normalizedProgressOffset + normalizedProgressPerSec * time / 1000));
        return highPoint.copy().scale(normalizedScalar).add(center);
    }


    @Override
    public CPathSinuous write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(center).write(buf).set(highPoint).write(buf);
        buf.writeDouble(normalizedProgressPerSec);
        buf.writeDouble(normalizedProgressOffset);

        return this;
    }

    @Override
    public CPathSinuous read(ByteBuf buf)
    {
        super.read(buf);

        CVectorN cVec = new CVectorN();

        center = cVec.read(buf).value;
        highPoint = cVec.read(buf).value;
        normalizedProgressPerSec = buf.readDouble();
        normalizedProgressOffset = buf.readDouble();

        return this;
    }

    @Override
    public CPathSinuous save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(center).save(stream).set(highPoint).save(stream);
        new CDouble().set(normalizedProgressPerSec).save(stream).set(normalizedProgressOffset).save(stream);

        return this;
    }

    @Override
    public CPathSinuous load(InputStream stream)
    {
        super.load(stream);

        CVectorN cVec = new CVectorN();
        CDouble cd = new CDouble();

        center = cVec.load(stream).value;
        highPoint = cVec.load(stream).value;
        normalizedProgressPerSec = cd.load(stream).value;
        normalizedProgressOffset = cd.load(stream).value;

        return this;
    }
}
