package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class CPath extends Component
{
    public long tick = 0;
    public VectorN vector = new VectorN(0, 0, 0), vectorPrev = new VectorN(0, 0, 0), vectorDelta = new VectorN(0, 0, 0);
    public CPath multiplier = new CPath();
    public ArrayList<CPath> combinedPaths = new ArrayList<>();


    /**
     * Generally leave this unedited and call it once "per tick", whatever and whenever your tick is
     * After calling this, apply it to something by reading the values stored in "vector"
     * Left this non-final in case there is a good reason to override it at some point
     */
    public void tick()
    {
        multiplier.tick();
        tick++;
        vectorPrev = vector.copy();

        tickInternal();

        for (CPath combinedPath : combinedPaths)
        {
            combinedPath.tick();
            vector.add(combinedPath.vectorDelta);
        }

        vectorDelta = vector.copy().subtract(vectorPrev);
    }

    /**
     * This method is meant to be overridden in subclasses to produce different path shapes/patterns
     * eg. vector = new VectorN(tick, tick, tick).multiply(multiplier.vector);
     */
    protected void tickInternal()
    {
    }

    /**
     * This is a convenience method for combining paths.
     * In theory, if you have a path that goes straight up (+y) and combine it into a path that spirals out (x, z) or vice-versa, you'll get a path that spirals out while moving up
     * Actual results may depend on contents of method overrides in subclasses
     */
    public void combineAdditive(CPath... paths)
    {
        Collections.addAll(combinedPaths, paths);
    }


    @Override
    public CPath write(ByteBuf buf)
    {
        buf.writeLong(tick);

        new CVectorN().set(vector).write(buf).set(vectorPrev).write(buf).set(vectorDelta).write(buf);

        writeMarked(buf, multiplier);

        buf.writeInt(combinedPaths.size());
        for (CPath combinedPath : combinedPaths) writeMarked(buf, combinedPath);

        return this;
    }

    @Override
    public CPath read(ByteBuf buf)
    {
        tick = buf.readLong();

        CVectorN cvec = new CVectorN();
        vector = cvec.read(buf).value;
        vectorPrev = cvec.read(buf).value;
        vectorDelta = cvec.read(buf).value;

        multiplier = (CPath) readMarked(buf);

        combinedPaths.clear();
        for (int i = buf.readInt(); i > 0; i--) combinedPaths.add((CPath) readMarked(buf));

        return this;
    }

    @Override
    public CPath save(OutputStream stream)
    {
        new CLong().set(tick).save(stream);

        new CVectorN().set(vector).save(stream).set(vectorPrev).save(stream).set(vectorDelta).save(stream);

        saveMarked(stream, multiplier);

        new CInt().set(combinedPaths.size()).save(stream);
        for (CPath combinedPath : combinedPaths) saveMarked(stream, combinedPath);

        return this;
    }

    @Override
    public CPath load(InputStream stream)
    {
        tick = new CLong().load(stream).value;

        CVectorN cvec = new CVectorN();
        vector = cvec.load(stream).value;
        vectorPrev = cvec.load(stream).value;
        vectorDelta = cvec.load(stream).value;

        multiplier = (CPath) loadMarked(stream);

        combinedPaths.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) combinedPaths.add((CPath) loadMarked(stream));

        return this;
    }
}
