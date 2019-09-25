package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CChar extends Component
{
    public char value;

    public CChar set(char value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CChar write(ByteBuf buf)
    {
        buf.writeChar(value);
        return this;
    }

    @Override
    public CChar read(ByteBuf buf)
    {
        value = buf.readChar();
        return this;
    }

    @Override
    public CChar save(OutputStream stream)
    {
        try
        {
            stream.write(("" + value).getBytes(UTF_8));
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CChar load(InputStream stream)
    {
        try
        {
            byte[] bytes = new byte[2];
            if (stream.read(bytes) < 2) throw new IOException("Reached end of file while reading!");
            value = new String(bytes, UTF_8).charAt(0);
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
