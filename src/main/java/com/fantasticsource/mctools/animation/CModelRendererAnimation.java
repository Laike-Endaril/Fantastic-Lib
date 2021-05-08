package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CModelRendererAnimation extends Component
{
    public CPath xPath = null, yPath = null, zPath = null, xRotPath = null, yRotPath = null, zRotPath = null, xScalePath = null, yScalePath = null, zScalePath = null;


    @Override
    public CModelRendererAnimation write(ByteBuf buf)
    {
        writeMarkedOrNull(buf, xPath);
        writeMarkedOrNull(buf, yPath);
        writeMarkedOrNull(buf, zPath);
        writeMarkedOrNull(buf, xRotPath);
        writeMarkedOrNull(buf, yRotPath);
        writeMarkedOrNull(buf, zRotPath);
        writeMarkedOrNull(buf, xScalePath);
        writeMarkedOrNull(buf, yScalePath);
        writeMarkedOrNull(buf, zScalePath);

        return this;
    }

    @Override
    public CModelRendererAnimation read(ByteBuf buf)
    {
        xPath = (CPath) readMarkedOrNull(buf);
        yPath = (CPath) readMarkedOrNull(buf);
        zPath = (CPath) readMarkedOrNull(buf);
        xRotPath = (CPath) readMarkedOrNull(buf);
        yRotPath = (CPath) readMarkedOrNull(buf);
        zRotPath = (CPath) readMarkedOrNull(buf);
        xScalePath = (CPath) readMarkedOrNull(buf);
        yScalePath = (CPath) readMarkedOrNull(buf);
        zScalePath = (CPath) readMarkedOrNull(buf);

        return this;
    }

    @Override
    public CModelRendererAnimation save(OutputStream stream)
    {
        saveMarkedOrNull(stream, xPath);
        saveMarkedOrNull(stream, yPath);
        saveMarkedOrNull(stream, zPath);
        saveMarkedOrNull(stream, xRotPath);
        saveMarkedOrNull(stream, yRotPath);
        saveMarkedOrNull(stream, zRotPath);
        saveMarkedOrNull(stream, xScalePath);
        saveMarkedOrNull(stream, yScalePath);
        saveMarkedOrNull(stream, zScalePath);

        return this;
    }

    @Override
    public CModelRendererAnimation load(InputStream stream)
    {
        xPath = (CPath) loadMarkedOrNull(stream);
        yPath = (CPath) loadMarkedOrNull(stream);
        zPath = (CPath) loadMarkedOrNull(stream);
        xRotPath = (CPath) loadMarkedOrNull(stream);
        yRotPath = (CPath) loadMarkedOrNull(stream);
        zRotPath = (CPath) loadMarkedOrNull(stream);
        xScalePath = (CPath) loadMarkedOrNull(stream);
        yScalePath = (CPath) loadMarkedOrNull(stream);
        zScalePath = (CPath) loadMarkedOrNull(stream);

        return this;
    }
}
