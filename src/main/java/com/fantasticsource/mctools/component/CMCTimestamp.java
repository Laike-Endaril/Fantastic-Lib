package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.MCTimestamp;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

public class CMCTimestamp extends Component
{
    public MCTimestamp value;

    public CMCTimestamp set(MCTimestamp value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CMCTimestamp write(ByteBuf buf)
    {
        buf.writeLong(value.getInstant().getEpochSecond());
        buf.writeInt(value.getInstant().getNano());

        buf.writeLong(value.serverTick);
        buf.writeLong(value.clientTick);
        buf.writeLong(value.worldTick);

        return this;
    }

    @Override
    public CMCTimestamp read(ByteBuf buf)
    {
        set(new MCTimestamp(Instant.ofEpochSecond(buf.readLong(), buf.readInt()), buf.readLong(), buf.readLong(), buf.readLong()));

        return this;
    }

    @Override
    public CMCTimestamp save(OutputStream stream)
    {
        new CLong().set(value.getInstant().getEpochSecond()).save(stream);
        new CInt().set(value.getInstant().getNano()).save(stream);

        new CLong().set(value.serverTick).save(stream);
        new CLong().set(value.clientTick).save(stream);
        new CLong().set(value.worldTick).save(stream);

        return this;
    }

    @Override
    public CMCTimestamp load(InputStream stream)
    {
        CLong l = new CLong();
        set(new MCTimestamp(Instant.ofEpochSecond(l.load(stream).value, new CInt().load(stream).value), l.load(stream).value, l.load(stream).value, l.load(stream).value));

        return this;
    }
}
