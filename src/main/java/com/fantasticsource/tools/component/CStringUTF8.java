package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CStringUTF8 extends Component
{
    public String value;

    public CStringUTF8 set(String value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CStringUTF8 write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, value);
        return this;
    }

    @Override
    public CStringUTF8 read(ByteBuf buf)
    {
        value = ByteBufUtils.readUTF8String(buf);
        return this;
    }

    @Override
    public CStringUTF8 save(OutputStream stream)
    {
        try
        {
            byte[] bytes = value.getBytes(UTF_8);
            int length = bytes.length;
            stream.write(new byte[]{(byte) (length >>> 24), (byte) (length >>> 16), (byte) (length >>> 8), (byte) length});
            stream.write(bytes);
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CStringUTF8 load(InputStream stream)
    {
        try
        {
            byte[] bytes = new byte[4];
            if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
            int length = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);

            bytes = new byte[length];
            if (stream.read(bytes) < length) throw new IOException("Reached end of file while reading!");
            value = new String(bytes, UTF_8);
            return this;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
