package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CStringUTF8 extends Component
{
    public String value;

    public CStringUTF8 set(String value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CStringUTF8 write(ByteBuf buf)
    {
        byte[] bytes = value.getBytes(UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
        return this;
    }

    @Override
    public CStringUTF8 read(ByteBuf buf)
    {
        int length = buf.readInt();
        value = buf.toString(buf.readerIndex(), length, UTF_8);
        return this;
    }

    @Override
    public CStringUTF8 save(FileOutputStream stream) throws IOException
    {
        byte[] bytes = value.getBytes(UTF_8);
        int length = bytes.length;
        stream.write(new byte[]{(byte) (length >>> 24), (byte) (length >>> 16), (byte) (length >>> 8), (byte) length});
        stream.write(bytes);
        return this;
    }

    @Override
    public CStringUTF8 load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[4];
        if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
        int length = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);

        bytes = new byte[length];
        if (stream.read(bytes) < length) throw new IOException("Reached end of file while reading!");
        value = new String(bytes, UTF_8);
        return this;
    }

    @Override
    public String toString()
    {
        return value;
    }

    @Override
    public CStringUTF8 parse(String string)
    {
        value = string;
        return this;
    }

    @Override
    public CStringUTF8 copy()
    {
        return new CStringUTF8().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        return new GUIText(screen, value);
    }

    @Override
    public CStringUTF8 setFromGUIElement(GUIElement element)
    {
        value = ((GUIText) element).text;
        return this;
    }
}
