package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class CItemFilter extends Component
{
    public ItemFilter value = null;

    public CItemFilter set(ItemFilter value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CItemFilter write(ByteBuf buf)
    {
        buf.writeBoolean(value.itemStack != null);
        if (value.itemStack != null) new CItemStack(value.itemStack).write(buf);

        buf.writeInt(value.tagsRequired.size());
        for (Map.Entry<String, String> entry : value.tagsRequired.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }

        buf.writeInt(value.tagsDisallowed.size());
        for (Map.Entry<String, String> entry : value.tagsDisallowed.entrySet())
        {
            ByteBufUtils.writeUTF8String(buf, entry.getKey());
            ByteBufUtils.writeUTF8String(buf, entry.getValue());
        }

        return this;
    }

    @Override
    public CItemFilter read(ByteBuf buf)
    {
        value = new ItemFilter();

        if (buf.readBoolean()) value.itemStack = new CItemStack().read(buf).value;

        for (int i = buf.readInt(); i > 0; i--)
        {
            value.tagsRequired.put(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
        }

        for (int i = buf.readInt(); i > 0; i--)
        {
            value.tagsDisallowed.put(ByteBufUtils.readUTF8String(buf), ByteBufUtils.readUTF8String(buf));
        }

        return this;
    }

    @Override
    public CItemFilter save(OutputStream stream)
    {
        CInt ci = new CInt();
        CStringUTF8 cs = new CStringUTF8();

        new CBoolean().set(value.itemStack != null).save(stream);
        if (value.itemStack != null) new CItemStack().set(value.itemStack).save(stream);

        ci.set(value.tagsRequired.size()).save(stream);
        for (Map.Entry<String, String> entry : value.tagsRequired.entrySet())
        {
            cs.set(entry.getKey()).save(stream).set(entry.getValue()).save(stream);
        }

        ci.set(value.tagsDisallowed.size()).save(stream);
        for (Map.Entry<String, String> entry : value.tagsDisallowed.entrySet())
        {
            cs.set(entry.getKey()).save(stream).set(entry.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CItemFilter load(InputStream stream)
    {
        CInt ci = new CInt();
        CStringUTF8 cs = new CStringUTF8();

        value = new ItemFilter();

        if (new CBoolean().load(stream).value) value.itemStack = new CItemStack().load(stream).value;

        for (int i = ci.load(stream).value; i > 0; i--)
        {
            value.tagsRequired.put(cs.load(stream).value, cs.load(stream).value);
        }

        for (int i = ci.load(stream).value; i > 0; i--)
        {
            value.tagsDisallowed.put(cs.load(stream).value, cs.load(stream).value);
        }

        return this;
    }
}
