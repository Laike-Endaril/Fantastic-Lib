package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CFloat extends Component
{
    public float value;

    public CFloat(Component holder)
    {
        super(holder);
    }

    public CFloat set(float value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeFloat(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readFloat();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        int i = Float.floatToRawIntBits(value);
        stream.write(new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i});
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[4];
        if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
        value = Float.intBitsToFloat(((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF));
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = Float.parseFloat(string);
    }

    @Override
    public CFloat copy()
    {
        return new CFloat(holder).set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        //TODO
        return null;
    }

    @Override
    public void setFromGUIElement(GUIElement element)
    {
        //TODO
    }
}
