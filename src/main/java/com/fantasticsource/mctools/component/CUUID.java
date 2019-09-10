package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.guielements.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CUUID extends Component
{
    public UUID value;

    public CUUID(Component holder)
    {
        super(holder);
    }

    public CUUID set(UUID value)
    {
        this.value = value;
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        buf.writeLong(value.getMostSignificantBits());
        buf.writeLong(value.getLeastSignificantBits());
    }

    @Override
    public void read(ByteBuf buf)
    {
        value = new UUID(buf.readLong(), buf.readLong());
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        long l = value.getMostSignificantBits();
        stream.write(new byte[]{(byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32), (byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l});
        l = value.getLeastSignificantBits();
        stream.write(new byte[]{(byte) (l >>> 56), (byte) (l >>> 48), (byte) (l >>> 40), (byte) (l >>> 32), (byte) (l >>> 24), (byte) (l >>> 16), (byte) (l >>> 8), (byte) l});
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        byte[] bytes = new byte[16];
        if (stream.read(bytes) < 16) throw new IOException("Reached end of file while reading!");
        int upper = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        int lower = ((bytes[4] & 0xFF) << 24) | ((bytes[5] & 0xFF) << 16) | ((bytes[6] & 0xFF) << 8) | (bytes[7] & 0xFF);
        long mostSig = ((upper & 0xffffffffL) << 32) | (lower & 0xffffffffL);
        upper = ((bytes[8] & 0xFF) << 24) | ((bytes[9] & 0xFF) << 16) | ((bytes[10] & 0xFF) << 8) | (bytes[11] & 0xFF);
        lower = ((bytes[12] & 0xFF) << 24) | ((bytes[13] & 0xFF) << 16) | ((bytes[14] & 0xFF) << 8) | (bytes[15] & 0xFF);
        value = new UUID(mostSig, ((upper & 0xffffffffL) << 32) | (lower & 0xffffffffL));
    }

    @Override
    public String toString()
    {
        return "" + value;
    }

    @Override
    public void parse(String string)
    {
        value = UUID.fromString(string);
    }

    @Override
    public CUUID copy()
    {
        return new CUUID(holder).set(new UUID(value.getMostSignificantBits(), value.getLeastSignificantBits()));
    }

    @Override
    public GUIElement getGUIElement()
    {
        //TODO
        return null;
    }
}
