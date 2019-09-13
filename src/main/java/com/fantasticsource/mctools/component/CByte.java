package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CByte extends Component
{
    public byte value;

    public CByte set(byte value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CByte write(ByteBuf buf)
    {
        buf.writeByte(value);
        return this;
    }

    @Override
    public CByte read(ByteBuf buf)
    {
        value = buf.readByte();
        return this;
    }

    @Override
    public CByte save(FileOutputStream stream) throws IOException
    {
        stream.write(value);
        return this;
    }

    @Override
    public CByte load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[1];
        if (stream.read(bytes) < 1) throw new IOException("Reached end of file while reading!");
        value = bytes[0];
        return this;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public CByte parse(String string)
    {
        value = Byte.parseByte(string);
        return this;
    }

    @Override
    public CByte copy()
    {
        return new CByte().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        //TODO
        return null;
    }

    @Override
    public CByte setFromGUIElement(GUIElement element)
    {
        //TODO
        return this;
    }
}
