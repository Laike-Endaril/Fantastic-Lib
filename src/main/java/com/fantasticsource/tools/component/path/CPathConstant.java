package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathConstant extends CPath
{
    public VectorN position;

    public CPathConstant(VectorN position)
    {
        this.position = position;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        return position.copy();
    }


    @Override
    public CPathConstant write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(position).write(buf);

        return this;
    }

    @Override
    public CPathConstant read(ByteBuf buf)
    {
        super.read(buf);

        position = new CVectorN().read(buf).value;

        return this;
    }

    @Override
    public CPathConstant save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(position).save(stream);

        return this;
    }

    @Override
    public CPathConstant load(InputStream stream)
    {
        super.load(stream);

        position = new CVectorN().load(stream).value;

        return this;
    }
}
