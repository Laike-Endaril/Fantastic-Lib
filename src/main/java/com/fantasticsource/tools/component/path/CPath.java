package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.Pair;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public abstract class CPath extends Component
{
    public ArrayList<Pair<CPath, Boolean>> combinedPaths = new ArrayList<>();


    public CPath combine(CPath path, boolean multiplicative)
    {
        combinedPaths.add(new Pair<>(path, multiplicative));
        return this;
    }


    /**
     * @param time spent on this path
     * @return position at that time, relative to an unknown origin point, or null if invalid (will destroy particles using this path if null)
     */
    protected abstract VectorN getRelativePositionInternal(long time);

    public final VectorN getRelativePosition(long time)
    {
        VectorN result = getRelativePositionInternal(time);
        for (Pair<CPath, Boolean> pair : combinedPaths)
        {
            if (pair.getValue()) result.multiply(pair.getKey().getRelativePosition(time));
            else result.add(pair.getKey().getRelativePosition(time));
        }
        return result;
    }

    public final VectorN getPosition(long timeMillis, VectorN origin)
    {
        VectorN relative = getRelativePosition(timeMillis);
        if (relative == null) return null;

        return origin.copy().add(relative);
    }


    @Override
    public CPath write(ByteBuf buf)
    {
        buf.writeInt(combinedPaths.size());
        for (Pair<CPath, Boolean> pair : combinedPaths)
        {
            writeMarked(buf, pair.getKey());
            buf.writeBoolean(pair.getValue());
        }

        return this;
    }

    @Override
    public CPath read(ByteBuf buf)
    {
        combinedPaths.clear();
        for (int i = buf.readInt(); i > 0; i--) combinedPaths.add(new Pair<>((CPath) readMarked(buf), buf.readBoolean()));

        return this;
    }

    @Override
    public CPath save(OutputStream stream)
    {
        CBoolean cb = new CBoolean();

        new CInt().set(combinedPaths.size()).save(stream);
        for (Pair<CPath, Boolean> pair : combinedPaths)
        {
            saveMarked(stream, pair.getKey());
            cb.set(pair.getValue()).save(stream);
        }

        return this;
    }

    @Override
    public CPath load(InputStream stream)
    {
        CBoolean cb = new CBoolean();

        combinedPaths.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) combinedPaths.add(new Pair<>((CPath) loadMarked(stream), cb.load(stream).value));

        return this;
    }


    public static class PathData
    {
        public CPath path;
        public long startMillis;

        public PathData(CPath path)
        {
            this(path, System.currentTimeMillis());
        }

        public PathData(CPath path, long startMillis)
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
