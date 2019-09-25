package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CByte extends Component
{
    public byte value;

    public CByte set(byte value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CByte write(ByteBuf buf)
    {
        buf.writeByte(value);
        return this;
    }

    @Override
    public CByte read(ByteBuf buf)
    {
        value = buf.readByte();
        return this;
    }

    @Override
    public CByte save(OutputStream stream)
    {
        try
        {
            stream.write(value);
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CByte load(InputStream stream)
    {
        try
        {
            byte[] bytes = new byte[1];
            if (stream.read(bytes) < 1) throw new IOException("Reached end of file while reading!");
            value = bytes[0];
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
