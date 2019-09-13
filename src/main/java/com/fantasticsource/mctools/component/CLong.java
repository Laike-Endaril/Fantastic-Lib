package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CLong extends Component
{
    public long value;

    public CLong set(long value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CLong write(ByteBuf buf)
    {
        buf.writeLong(value);
        return this;
    }

    @Override
    public CLong read(ByteBuf buf)
    {
        value = buf.readLong();
        return this;
    }

    @Override
    public CLong save(FileOutputStream stream) throws IOException
    {
        stream.write(new byte[]{(byte) (value >>> 56), (byte) (value >>> 48), (byte) (value >>> 40), (byte) (value >>> 32), (byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value});
        return this;
    }

    @Override
    public CLong load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[8];
        if (stream.read(bytes) < 8) throw new IOException("Reached end of file while reading!");
        int upper = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        int lower = ((bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) | ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
        value = ((upper & 0xffffffffL) << 32) | (lower & 0xffffffffL);
        return this;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public CLong parse(String string)
    {
        value = Long.parseLong(string);
        return this;
    }

    @Override
    public CLong copy()
    {
        return new CLong().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        //TODO
        return null;
    }

    @Override
    public CLong setFromGUIElement(GUIElement element)
    {
        //TODO
        return this;
    }
}
