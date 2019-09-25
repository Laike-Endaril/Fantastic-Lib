package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CInt extends Component
{
    public int value;

    public CInt set(int value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CInt write(ByteBuf buf)
    {
        buf.writeInt(value);
        return this;
    }

    @Override
    public CInt read(ByteBuf buf)
    {
        value = buf.readInt();
        return this;
    }

    @Override
    public CInt save(OutputStream stream)
    {
        try
        {
            stream.write(new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value});
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CInt load(InputStream stream)
    {
        try
        {
            byte[] bytes = new byte[4];
            if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
            value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
