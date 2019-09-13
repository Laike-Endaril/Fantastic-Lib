package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CBoolean extends Component
{
    public boolean value;

    public CBoolean set(boolean value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CBoolean write(ByteBuf buf)
    {
        buf.writeBoolean(value);
        return this;
    }

    @Override
    public CBoolean read(ByteBuf buf)
    {
        value = buf.readBoolean();
        return this;
    }

    @Override
    public CBoolean save(FileOutputStream stream) throws IOException
    {
        stream.write(value ? (byte) 1 : (byte) 0);
        return this;
    }

    @Override
    public CBoolean load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[1];
        if (stream.read(bytes) < 1) throw new IOException("Reached end of file while reading!");
        value = bytes[0] == 1;
        return this;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public CBoolean parse(String string)
    {
        value = Boolean.parseBoolean(string);
        return this;
    }

    @Override
    public CBoolean copy()
    {
        return new CBoolean().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        //TODO
        return null;
    }

    @Override
    public CBoolean setFromGUIElement(GUIElement element)
    {
        //TODO
        return this;
    }
}
