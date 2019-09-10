package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.guielements.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CStringUTF8 extends Component
{
    public String value;

    public CStringUTF8(Component holder)
    {
        super(holder);
    }

    public CStringUTF8 set(String value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        byte[] bytes = value.getBytes(UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public void read(ByteBuf buf)
    {
        int length = buf.readInt();
        value = buf.toString(buf.readerIndex(), length, UTF_8);
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        byte[] bytes = value.getBytes(UTF_8);
        int length = bytes.length;
        stream.write(new byte[]{(byte) (length >>> 24), (byte) (length >>> 16), (byte) (length >>> 8), (byte) length});
        stream.write(bytes);
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[4];
        if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
        int length = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);

        bytes = new byte[length];
        if (stream.read(bytes) < length) throw new IOException("Reached end of file while reading!");
        value = new String(bytes, UTF_8);
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public void parse(String string)
    {
        value = string;
    }

    @Override
    public CStringUTF8 copy()
    {
        return new CStringUTF8(holder).set(value);
    }

    @Override
    public GUIElement getGUIElement()
    {
        //TODO
        return null;
    }
}
