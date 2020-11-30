package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.io.OutputStream;

public class CResourceLocation extends Component
{
    public ResourceLocation value;

    public CResourceLocation()
    {
    }

    public CResourceLocation(ResourceLocation value)
    {
        set(value);
    }

    public CResourceLocation set(ResourceLocation value)
    {
        this.value = value;

        return this;
    }

    @Override
    public CResourceLocation write(ByteBuf buf)
    {
        new CStringUTF8().set(value.getResourceDomain()).write(buf).set(value.getResourcePath()).write(buf);

        return this;
    }

    @Override
    public CResourceLocation read(ByteBuf buf)
    {
        return set(new ResourceLocation(new CStringUTF8().read(buf).value, new CStringUTF8().read(buf).value));
    }

    @Override
    public CResourceLocation save(OutputStream stream)
    {
        new CStringUTF8().set(value.getResourceDomain()).save(stream).set(value.getResourcePath()).save(stream);

        return this;
    }

    @Override
    public CResourceLocation load(InputStream stream)
    {
        return set(new ResourceLocation(new CStringUTF8().load(stream).value, new CStringUTF8().load(stream).value));
    }
}
