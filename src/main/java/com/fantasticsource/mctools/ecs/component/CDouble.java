package com.fantasticsource.mctools.ecs.component;

import com.fantasticsource.mctools.ecs.ECSEntity;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CDouble extends Component
{
    public double value;

    public CDouble(ECSEntity entity)
    {
        super(entity);
    }

    public CDouble set(double value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeDouble(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readDouble();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        long l = Double.doubleToRawLongBits(value);
        stream.write(new byte[]{(byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32), (byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l});
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[8];
        if (stream.read(bytes) < 8) throw new IOException("Reached end of file while reading!");
        int upper = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        int lower = ((bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) | ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
        value = Double.longBitsToDouble(((upper & 0xffffffffL) << 32) | (lower & 0xffffffffL));
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = Double.parseDouble(string);
    }

    @Override
    public CDouble copy()
    {
        return new CDouble(entity).set(value);
    }
}
