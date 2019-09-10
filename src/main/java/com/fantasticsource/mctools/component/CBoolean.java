package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.guielements.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CBoolean extends Component
{
    public boolean value;

    public CBoolean(Component holder)
    {
        super(holder);
    }

    public CBoolean set(boolean value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeBoolean(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readBoolean();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        stream.write(value ? (byte) 1 : (byte) 0);
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[1];
        if (stream.read(bytes) < 1) throw new IOException("Reached end of file while reading!");
        value = bytes[0] == 1;
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = Boolean.parseBoolean(string);
    }

    @Override
    public CBoolean copy()
    {
        return new CBoolean(holder).set(value);
    }

    @Override
    public GUIElement getGUIElement()
    {
        //TODO
        return null;
    }
}
