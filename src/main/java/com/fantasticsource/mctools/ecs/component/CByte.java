package com.fantasticsource.mctools.ecs.component;

import com.fantasticsource.mctools.ecs.ECSEntity;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CByte extends Component
{
    public byte value;

    public CByte(ECSEntity entity)
    {
        super(entity);
    }

    public CByte set(byte value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeByte(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readByte();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        stream.write(value);
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[1];
        if (stream.read(bytes) < 1) throw new IOException("Reached end of file while reading!");
        value = bytes[0];
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = Byte.parseByte(string);
    }

    @Override
    public CByte copy()
    {
        return new CByte(entity).set(value);
    }
}
