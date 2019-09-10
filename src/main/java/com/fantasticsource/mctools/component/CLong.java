package com.fantasticsource.mctools.component;

import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CLong extends Component
{
    public long value;

    public CLong(Component holder)
    {
        super(holder);
    }

    public CLong set(long value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeLong(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readLong();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        stream.write(new byte[]{(byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32), (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value});
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[8];
        if (stream.read(bytes) < 8) throw new IOException("Reached end of file while reading!");
        int upper = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        int lower = ((bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) | ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
        value = ((upper & 0xffffffffL) << 32) | (lower & 0xffffffffL);
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = Long.parseLong(string);
    }

    @Override
    public CLong copy()
    {
        return new CLong(holder).set(value);
    }
}
