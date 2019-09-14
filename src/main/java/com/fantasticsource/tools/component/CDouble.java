package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CDouble extends Component
{
    public double value;

    public CDouble set(double value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CDouble write(ByteBuf buf)
    {
        buf.writeDouble(value);
        return this;
    }

    @Override
    public CDouble read(ByteBuf buf)
    {
        value = buf.readDouble();
        return this;
    }

    @Override
    public CDouble save(OutputStream stream) throws IOException
    {
        long l = Double.doubleToRawLongBits(value);
        stream.write(new byte[]{(byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32), (byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l});
        return this;
    }

    @Override
    public CDouble load(InputStream stream) throws IOException
    {
        byte[] bytes = new byte[8];
        if (stream.read(bytes) < 8) throw new IOException("Reached end of file while reading!");
        int upper = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        int lower = ((bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) | ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
        value = Double.longBitsToDouble(((upper & 0xffffffffL) << 32) | (lower & 0xffffffffL));
        return this;
    }
}
