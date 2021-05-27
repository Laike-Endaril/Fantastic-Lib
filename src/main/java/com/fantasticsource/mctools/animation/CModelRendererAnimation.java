package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;

public class CModelRendererAnimation extends Component
{
    public CPath.CPathData xPath = new CPath.CPathData(), yPath = new CPath.CPathData(), zPath = new CPath.CPathData(), xRotPath = new CPath.CPathData(), yRotPath = new CPath.CPathData(), zRotPath = new CPath.CPathData(), xScalePath = new CPath.CPathData(), yScalePath = new CPath.CPathData(), zScalePath = new CPath.CPathData();


    public CPath.CPathData[] getAllData()
    {
        return new CPath.CPathData[]{xPath, yPath, zPath, xRotPath, yRotPath, zRotPath, xScalePath, yScalePath, zScalePath};
    }


    @Override
    public CModelRendererAnimation write(ByteBuf buf)
    {
        for (CPath.CPathData data : getAllData()) writeMarkedOrNull(buf, data);
        return this;
    }

    @Override
    public CModelRendererAnimation read(ByteBuf buf)
    {
        xPath = (CPath.CPathData) readMarkedOrNull(buf);
        yPath = (CPath.CPathData) readMarkedOrNull(buf);
        zPath = (CPath.CPathData) readMarkedOrNull(buf);
        xRotPath = (CPath.CPathData) readMarkedOrNull(buf);
        yRotPath = (CPath.CPathData) readMarkedOrNull(buf);
        zRotPath = (CPath.CPathData) readMarkedOrNull(buf);
        xScalePath = (CPath.CPathData) readMarkedOrNull(buf);
        yScalePath = (CPath.CPathData) readMarkedOrNull(buf);
        zScalePath = (CPath.CPathData) readMarkedOrNull(buf);

        return this;
    }

    @Override
    public CModelRendererAnimation save(OutputStream stream)
    {
        for (CPath.CPathData data : getAllData()) saveMarkedOrNull(stream, data);
        return this;
    }

    @Override
    public CModelRendererAnimation load(InputStream stream)
    {
        xPath = (CPath.CPathData) loadMarkedOrNull(stream);
        yPath = (CPath.CPathData) loadMarkedOrNull(stream);
        zPath = (CPath.CPathData) loadMarkedOrNull(stream);
        xRotPath = (CPath.CPathData) loadMarkedOrNull(stream);
        yRotPath = (CPath.CPathData) loadMarkedOrNull(stream);
        zRotPath = (CPath.CPathData) loadMarkedOrNull(stream);
        xScalePath = (CPath.CPathData) loadMarkedOrNull(stream);
        yScalePath = (CPath.CPathData) loadMarkedOrNull(stream);
        zScalePath = (CPath.CPathData) loadMarkedOrNull(stream);

        return this;
    }
}
