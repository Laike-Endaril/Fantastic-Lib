package com.fantasticsource.mctools.ecs.component;

import com.fantasticsource.mctools.ecs.ECSEntity;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CInt extends Component
{
    public int value;

    public CInt(ECSEntity entity)
    {
        super(entity);
    }

    public CInt set(int value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeInt(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readInt();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        stream.write(new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value});
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[4];
        if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
        value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = Integer.parseInt(string);
    }

    @Override
    public CInt copy()
    {
        return new CInt(entity).set(value);
    }
}
