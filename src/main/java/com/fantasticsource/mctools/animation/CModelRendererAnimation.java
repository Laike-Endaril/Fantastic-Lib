package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CModelRendererAnimation extends Component
{
    public CPath xRotPath = null, yRotPath = null, zRotPath = null;


    @Override
    public CModelRendererAnimation write(ByteBuf buf)
    {
        writeMarkedOrNull(buf, xRotPath);
        writeMarkedOrNull(buf, yRotPath);
        writeMarkedOrNull(buf, zRotPath);

        return this;
    }

    @Override
    public CModelRendererAnimation read(ByteBuf buf)
    {
        xRotPath = (CPath) readMarkedOrNull(buf);
        yRotPath = (CPath) readMarkedOrNull(buf);
        zRotPath = (CPath) readMarkedOrNull(buf);

        return this;
    }

    @Override
    public CModelRendererAnimation save(OutputStream stream)
    {
        saveMarkedOrNull(stream, xRotPath);
        saveMarkedOrNull(stream, yRotPath);
        saveMarkedOrNull(stream, zRotPath);

        return this;
    }

    @Override
    public CModelRendererAnimation load(InputStream stream)
    {
        xRotPath = (CPath) loadMarkedOrNull(stream);
        yRotPath = (CPath) loadMarkedOrNull(stream);
        zRotPath = (CPath) loadMarkedOrNull(stream);

        return this;
    }
}
