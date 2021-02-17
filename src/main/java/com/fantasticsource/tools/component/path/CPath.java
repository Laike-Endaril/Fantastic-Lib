package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public abstract class CPath extends Component
{
    public CPath multiplier = null;
    public ArrayList<CPath> combinedPaths = new ArrayList<>();


    /**
     * This is a convenience method for combining paths.
     * In theory, if you have a path that goes straight up (+y) and combine it into a path that spirals out (x, z) or vice-versa, you'll get a path that spirals out while moving up
     * Actual results may depend on contents of method overrides in subclasses
     */
    public void combineAdditive(CPath... paths)
    {
        Collections.addAll(combinedPaths, paths);
    }


    /**
     * @param time spent on this path
     * @return position at that time, relative to an unknown origin point, or null if invalid (will destroy particles using this path if null)
     */
    public abstract VectorN getRelativePosition(long time);

    public final VectorN getPosition(long timeMillis, VectorN origin)
    {
        VectorN relative = getRelativePosition(timeMillis);
        if (relative == null) return null;

        return origin.copy().add(relative);
    }


    @Override
    public CPath write(ByteBuf buf)
    {
        buf.writeBoolean(multiplier != null);
        if (multiplier != null) writeMarked(buf, multiplier);

        buf.writeInt(combinedPaths.size());
        for (CPath combinedPath : combinedPaths) writeMarked(buf, combinedPath);

        return this;
    }

    @Override
    public CPath read(ByteBuf buf)
    {
        multiplier = buf.readBoolean() ? (CPath) readMarked(buf) : null;

        combinedPaths.clear();
        for (int i = buf.readInt(); i > 0; i--) combinedPaths.add((CPath) readMarked(buf));

        return this;
    }

    @Override
    public CPath save(OutputStream stream)
    {
        new CBoolean().set(multiplier != null).save(stream);
        if (multiplier != null) saveMarked(stream, multiplier);

        new CInt().set(combinedPaths.size()).save(stream);
        for (CPath combinedPath : combinedPaths) saveMarked(stream, combinedPath);

        return this;
    }

    @Override
    public CPath load(InputStream stream)
    {
        multiplier = new CBoolean().load(stream).value ? (CPath) loadMarked(stream) : null;

        combinedPaths.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) combinedPaths.add((CPath) loadMarked(stream));

        return this;
    }


    public static class PathData
    {
        public CPath path;
        public long startMillis;

        public PathData(CPath path, VectorN origin)
        {
            this(path, origin, System.currentTimeMillis());
        }

        public PathData(CPath path, VectorN origin, long startMillis)
        {
            this.path = path;
            this.startMillis = startMillis;
        }

        public VectorN getRelativePosition()
        {
            return path.getRelativePosition(System.currentTimeMillis() - startMillis);
        }
    }
}
