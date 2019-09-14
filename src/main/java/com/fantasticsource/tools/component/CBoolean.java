package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CBoolean extends Component
{
    public boolean value;

    public CBoolean set(boolean value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CBoolean write(ByteBuf buf)
    {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public CBoolean read(ByteBuf buf)
    {
        value = buf.readBoolean();
        return this;
    }

    @Override
    public CBoolean save(OutputStream stream) throws IOException
    {
        stream.write(value ? (byte) 1 : (byte) 0);
        return this;
    }

    @Override
    public CBoolean load(InputStream stream) throws IOException
    {
        byte[] bytes = new byte[1];
        if (stream.read(bytes) < 1) throw new IOException("Reached end of file while reading!");
        value = bytes[0] == 1;
        return this;
    }
}
