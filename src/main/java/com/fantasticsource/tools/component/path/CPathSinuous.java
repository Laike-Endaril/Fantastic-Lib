package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.component.CDouble;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathSinuous extends CPath
{
    public CPath centerPath, highPointOffsetPath;
    public double normalizedProgressPerSec, normalizedProgressOffset;


    public CPathSinuous()
    {
    }

    public CPathSinuous(CPath highPointOffsetPath, double normalizedProgressPerSec)
    {
        this(new CPathConstant(highPointOffsetPath.getRelativePositionInternal(0).scale(0)), highPointOffsetPath, normalizedProgressPerSec, 0);
    }

    public CPathSinuous(CPath highPointOffsetPath, double normalizedProgressPerSec, double normalizedProgressOffset)
    {
        this(new CPathConstant(highPointOffsetPath.getRelativePositionInternal(0).scale(0)), highPointOffsetPath, normalizedProgressPerSec, normalizedProgressOffset);
    }

    public CPathSinuous(CPath centerPath, CPath highPointOffsetPath, double normalizedProgressPerSec)
    {
        this(centerPath, highPointOffsetPath, normalizedProgressPerSec, 0);
    }

    public CPathSinuous(CPath centerPath, CPath highPointOffsetPath, double normalizedProgressPerSec, double normalizedProgressOffset)
    {
        this.centerPath = centerPath;
        this.highPointOffsetPath = highPointOffsetPath;
        this.normalizedProgressPerSec = normalizedProgressPerSec;
        this.normalizedProgressOffset = normalizedProgressOffset;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        double normalizedScalar = TrigLookupTable.TRIG_TABLE_1024.sin(Math.PI * 2 * (normalizedProgressOffset + normalizedProgressPerSec * time / 1000));
        return highPointOffsetPath.getRelativePositionInternal(time).scale(normalizedScalar).add(centerPath.getRelativePositionInternal(time));
    }


    @Override
    public CPathSinuous write(ByteBuf buf)
    {
        super.write(buf);

        writeMarked(buf, centerPath);
        writeMarked(buf, highPointOffsetPath);
        buf.writeDouble(normalizedProgressPerSec);
        buf.writeDouble(normalizedProgressOffset);

        return this;
    }

    @Override
    public CPathSinuous read(ByteBuf buf)
    {
        super.read(buf);

        centerPath = (CPath) readMarked(buf);
        highPointOffsetPath = (CPath) readMarked(buf);
        normalizedProgressPerSec = buf.readDouble();
        normalizedProgressOffset = buf.readDouble();

        return this;
    }

    @Override
    public CPathSinuous save(OutputStream stream)
    {
        super.save(stream);

        saveMarked(stream, centerPath);
        saveMarked(stream, highPointOffsetPath);
        new CDouble().set(normalizedProgressPerSec).save(stream).set(normalizedProgressOffset).save(stream);

        return this;
    }

    @Override
    public CPathSinuous load(InputStream stream)
    {
        super.load(stream);

        CDouble cd = new CDouble();

        centerPath = (CPath) loadMarked(stream);
        highPointOffsetPath = (CPath) loadMarked(stream);
        normalizedProgressPerSec = cd.load(stream).value;
        normalizedProgressOffset = cd.load(stream).value;

        return this;
    }
}
