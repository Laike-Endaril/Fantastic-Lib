package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathAxial extends CPath
{
    public CPath axis, radiusTheta;

    public CPathAxial(CPath radiusTheta)
    {
        this(new CPath(new VectorN(0, 0, 1)), radiusTheta);
    }

    public CPathAxial(CPath axis, CPath radiusTheta)
    {
        this.axis = axis;
        this.radiusTheta = radiusTheta;
    }

    @Override
    protected void tickInternal()
    {
        axis.tick();
        radiusTheta.tick();

        //TODO vector = ???
    }


    @Override
    public CPathAxial write(ByteBuf buf)
    {
        super.write(buf);

        writeMarked(buf, axis);
        writeMarked(buf, radiusTheta);

        return this;
    }

    @Override
    public CPathAxial read(ByteBuf buf)
    {
        super.read(buf);

        axis = (CPath) readMarked(buf);
        radiusTheta = (CPath) readMarked(buf);

        return this;
    }

    @Override
    public CPathAxial save(OutputStream stream)
    {
        super.save(stream);

        saveMarked(stream, axis);
        saveMarked(stream, radiusTheta);

        return this;
    }

    @Override
    public CPathAxial load(InputStream stream)
    {
        super.load(stream);

        axis = (CPath) loadMarked(stream);
        radiusTheta = (CPath) loadMarked(stream);

        return this;
    }
}
