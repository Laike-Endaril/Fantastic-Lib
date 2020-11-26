package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathLinear extends CPath
{
    public VectorN direction;

    public CPathLinear(VectorN direction)
    {
        this.direction = direction;
    }

    @Override
    protected void tickInternal()
    {
        vector.add(direction).multiply(multiplier.vector);
    }


    @Override
    public CPathLinear write(ByteBuf buf)
    {
        super.write(buf);

        new CVectorN().set(direction).write(buf);

        return this;
    }

    @Override
    public CPathLinear read(ByteBuf buf)
    {
        super.read(buf);

        direction = new CVectorN().read(buf).value;

        return this;
    }

    @Override
    public CPathLinear save(OutputStream stream)
    {
        super.save(stream);

        new CVectorN().set(direction).save(stream);

        return this;
    }

    @Override
    public CPathLinear load(InputStream stream)
    {
        super.load(stream);

        direction = new CVectorN().load(stream).value;

        return this;
    }

}
