package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.component.*;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

public class CPath extends Component
{
    public static final Field
            PARTICLE_MOTION_X_FIELD = ReflectionTool.getField(Particle.class, "field_187129_i", "motionX"),
            PARTICLE_MOTION_Y_FIELD = ReflectionTool.getField(Particle.class, "field_187130_j", "motionY"),
            PARTICLE_MOTION_Z_FIELD = ReflectionTool.getField(Particle.class, "field_187131_k", "motionZ");

    public long currentTick = 0;
    public VectorN vector, vectorPrev, vectorDelta = new VectorN(0, 0, 0);
    public CPath multiplier = null;
    public ArrayList<CPath> combinedPaths = new ArrayList<>();
    public ArrayList<Entity> affectedEntities = new ArrayList<>();
    public ArrayList<Particle> affectedParticles = new ArrayList<>();


    public CPath()
    {
        this(new VectorN(0, 0, 0));
    }

    public CPath(VectorN origin)
    {
        vector = origin;
        vectorPrev = vector;
    }

    /**
     * Generally leave this unedited and call it once "per tick", whatever and whenever your tick is
     * After calling this, apply it to something by reading the values stored in public fields vector, vectorPrev, and vectorDelta
     * Left this non-final in case there is a good reason to override it at some point
     */
    public void tick()
    {
        vectorPrev = vector.copy();
        currentTick++;
        tickInternal();

        if (multiplier != null)
        {
            multiplier.tick();
            vector.multiply(multiplier.vector);
        }

        for (CPath combinedPath : combinedPaths)
        {
            combinedPath.tick();
            vector.add(combinedPath.vectorDelta);
        }

        vectorDelta = vector.copy().subtract(vectorPrev);

        affectedEntities.removeIf(entity ->
        {
            if (entity.world.loadedEntityList.contains(entity))
            {
                //TODO haven't found a way of producing nice motion for entities yet
                entity.motionX += vectorDelta.values[0];
                entity.motionY += vectorDelta.values[1];
                entity.motionZ += vectorDelta.values[2];
//                entity.setPosition(entity.posX + vectorDelta.values[0], entity.posY + vectorDelta.values[1], entity.posZ + vectorDelta.values[2]);
                return false;
            }
            else return true;
        });

        affectedParticles.removeIf(particle ->
        {
            if (particle.isAlive())
            {
                //TODO this shouldn't be choppy when running on the client tick...but it is.  Need to figure out why
                particle.move(vectorDelta.values[0], vectorDelta.values[1], vectorDelta.values[2]);

                //This works fine for accelerative
//                ReflectionTool.set(PARTICLE_MOTION_X_FIELD, particle, (double) ReflectionTool.get(PARTICLE_MOTION_X_FIELD, particle) + vectorDelta.values[0]);
//                ReflectionTool.set(PARTICLE_MOTION_Y_FIELD, particle, (double) ReflectionTool.get(PARTICLE_MOTION_Y_FIELD, particle) + vectorDelta.values[1]);
//                ReflectionTool.set(PARTICLE_MOTION_Z_FIELD, particle, (double) ReflectionTool.get(PARTICLE_MOTION_Z_FIELD, particle) + vectorDelta.values[2]);
                return false;
            }
            else return true;
        });
    }

    /**
     * This method is meant to be overridden in subclasses to produce different path shapes/patterns
     * As a result of this method, the "vector" field should be updated to represent an absolute position based on the current value of "tick"
     * The "multiplier" field is automatically applied to this afterward in the "tick()" method above
     * See CPathLinear for an example override
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
        buf.writeLong(currentTick);

        new CVectorN().set(vector).write(buf).set(vectorPrev).write(buf).set(vectorDelta).write(buf);

        buf.writeBoolean(multiplier != null);
        if (multiplier != null) writeMarked(buf, multiplier);

        buf.writeInt(combinedPaths.size());
        for (CPath combinedPath : combinedPaths) writeMarked(buf, combinedPath);

        return this;
    }

    @Override
    public CPath read(ByteBuf buf)
    {
        currentTick = buf.readLong();

        CVectorN cvec = new CVectorN();
        vector = cvec.read(buf).value;
        vectorPrev = cvec.read(buf).value;
        vectorDelta = cvec.read(buf).value;

        multiplier = buf.readBoolean() ? (CPath) readMarked(buf) : null;

        combinedPaths.clear();
        for (int i = buf.readInt(); i > 0; i--) combinedPaths.add((CPath) readMarked(buf));

        return this;
    }

    @Override
    public CPath save(OutputStream stream)
    {
        new CLong().set(currentTick).save(stream);

        new CVectorN().set(vector).save(stream).set(vectorPrev).save(stream).set(vectorDelta).save(stream);

        new CBoolean().set(multiplier != null).save(stream);
        if (multiplier != null) saveMarked(stream, multiplier);

        new CInt().set(combinedPaths.size()).save(stream);
        for (CPath combinedPath : combinedPaths) saveMarked(stream, combinedPath);

        return this;
    }

    @Override
    public CPath load(InputStream stream)
    {
        currentTick = new CLong().load(stream).value;

        CVectorN cvec = new CVectorN();
        vector = cvec.load(stream).value;
        vectorPrev = cvec.load(stream).value;
        vectorDelta = cvec.load(stream).value;

        multiplier = new CBoolean().load(stream).value ? (CPath) loadMarked(stream) : null;

        combinedPaths.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) combinedPaths.add((CPath) loadMarked(stream));

        return this;
    }
}
