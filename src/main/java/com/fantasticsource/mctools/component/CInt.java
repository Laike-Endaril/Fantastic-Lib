package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public CInt save(FileOutputStream stream) throws IOException
    {
        stream.write(new byte[]{(byte) (value >>> 24), (byte) (value >>> 16), (byte) (value >>> 8), (byte) value});
        return this;
    }

    @Override
    public CInt load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[4];
        if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
        value = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        return this;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public CInt parse(String string)
    {
        value = Integer.parseInt(string);
        return this;
    }

    @Override
    public CInt copy()
    {
        return new CInt().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        //TODO
        return null;
    }

    @Override
    public CInt setFromGUIElement(GUIElement element)
    {
        //TODO
        return this;
    }
}
