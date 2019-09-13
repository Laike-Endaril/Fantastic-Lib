package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.GUIChar;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class CChar extends Component
{
    public char value;

    public CChar set(char value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeChar(value);
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = buf.readChar();
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        stream.write(("" + value).getBytes(UTF_8));
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[2];
        if (stream.read(bytes) < 2) throw new IOException("Reached end of file while reading!");
        value = new String(bytes, UTF_8).charAt(0);
    }

    @Override
    public void parse(String string)
    {
        value = string.charAt(0);
    }

    @Override
    public CChar copy()
    {
        return new CChar().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        return new GUIChar(screen, value);
    }

    @Override
    public void setFromGUIElement(GUIElement element)
    {
        value = ((GUIChar) element).value;
    }
}
