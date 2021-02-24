package com.fantasticsource.tools.component.path;

import com.fantasticsource.mctools.component.NBTSerializableComponent;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class CPath extends NBTSerializableComponent
{
    public ArrayList<CPathTransform> transforms = new ArrayList<>();


    public CPath add(CPath path)
    {
        transforms.add(new CPathTransform(CPathTransform.TYPE_ADD, path));
        return this;
    }

    public CPath mult(CPath path)
    {
        transforms.add(new CPathTransform(CPathTransform.TYPE_MULT, path));
        return this;
    }

    public CPath rotate(CPath axisPath, CPath thetaPath)
    {
        transforms.add(new CPathTransform(CPathTransform.TYPE_ROTATE, axisPath, thetaPath));
        return this;
    }

    public CPath crossProduct(CPath other)
    {
        transforms.add(new CPathTransform(CPathTransform.TYPE_CROSS_PRODUCT, other));
        return this;
    }

    public CPath power(CPath other)
    {
        transforms.add(new CPathTransform(CPathTransform.TYPE_POWER, other));
        return this;
    }


    /**
     * @param time spent on this path
     * @return position at that time, relative to an unknown origin point, or null if invalid (will destroy particles using this path if null)
     */
    protected VectorN getRelativePositionInternal(long time)
    {
        return new VectorN(0, 0, 0);
    }

    public final VectorN getRelativePosition(long time)
    {
        VectorN result = getRelativePositionInternal(time);
        for (CPathTransform transform : transforms) transform.applyTo(result, time);
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
        buf.writeInt(transforms.size());
        for (CPathTransform transform : transforms) transform.write(buf);

        return this;
    }

    @Override
    public CPath read(ByteBuf buf)
    {
        transforms.clear();
        for (int i = buf.readInt(); i > 0; i--) transforms.add(new CPathTransform().read(buf));

        return this;
    }

    @Override
    public CPath save(OutputStream stream)
    {
        new CInt().set(transforms.size()).save(stream);
        for (CPathTransform transform : transforms) transform.save(stream);

        return this;
    }

    @Override
    public CPath load(InputStream stream)
    {
        transforms.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--) transforms.add(new CPathTransform().load(stream));

        return this;
    }


    @Override
    public CPath copy()
    {
        return (CPath) super.copy();
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = new NBTTagCompound();

        if (transforms.size() > 0)
        {
            NBTTagList list = new NBTTagList();
            for (CPathTransform transform : transforms) list.appendTag(transform.serializeNBT());
            compound.setTag("transforms", list);
        }

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        NBTTagCompound compound = (NBTTagCompound) nbt;

        transforms.clear();
        if (compound.hasKey("transforms"))
        {
            NBTTagList list = (NBTTagList) compound.getTag("transforms");
            for (int i = 0; i < list.tagCount(); i++)
            {
                CPathTransform transform = new CPathTransform();
                transform.deserializeNBT(list.getCompoundTagAt(i));
                transforms.add(transform);
            }
        }
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


    public static class CPathTransform extends NBTSerializableComponent
    {
        public static final int
                TYPE_ADD = 0,
                TYPE_MULT = 1,
                TYPE_ROTATE = 2,
                TYPE_CROSS_PRODUCT = 3,
                TYPE_POWER = 4;

        public int type = TYPE_ADD;
        public CPath[] paths;

        public CPathTransform()
        {
        }

        public CPathTransform(int type, CPath... paths)
        {
            this.type = type;
            this.paths = paths;
        }


        public void applyTo(VectorN vectorN, long time)
        {
            switch (type)
            {
                case TYPE_ADD:
                    vectorN.add(paths[0].getRelativePosition(time));
                    break;

                case TYPE_MULT:
                    vectorN.multiply(paths[0].getRelativePosition(time));
                    break;

                case TYPE_ROTATE:
                    vectorN.rotate(paths[0].getRelativePosition(time), paths[1].getRelativePosition(time).values[0]);
                    break;

                case TYPE_CROSS_PRODUCT:
                    vectorN.crossProduct(paths[0].getRelativePosition(time));
                    break;

                case TYPE_POWER:
                    vectorN.power(paths[0].getRelativePosition(time));
                    break;
            }
        }


        @Override
        public CPathTransform write(ByteBuf buf)
        {
            buf.writeInt(type);

            buf.writeInt(paths.length);
            for (CPath path : paths) writeMarked(buf, path);

            return this;
        }

        @Override
        public CPathTransform read(ByteBuf buf)
        {
            CInt ci = new CInt();

            type = ci.read(buf).value;

            paths = new CPath[ci.read(buf).value];
            for (int i = 0; i < paths.length; i++) paths[i] = (CPath) readMarked(buf);

            return this;
        }

        @Override
        public CPathTransform save(OutputStream stream)
        {
            new CInt().set(type).save(stream).set(paths.length).save(stream);
            for (CPath path : paths) saveMarked(stream, path);

            return this;
        }

        @Override
        public CPathTransform load(InputStream stream)
        {
            CInt ci = new CInt();

            type = ci.load(stream).value;

            paths = new CPath[ci.load(stream).value];
            for (int i = 0; i < paths.length; i++) paths[i] = (CPath) loadMarked(stream);

            return this;
        }


        @Override
        public NBTTagCompound serializeNBT()
        {
            NBTTagCompound compound = new NBTTagCompound();

            compound.setInteger("type", type);
            if (paths.length > 0)
            {
                NBTTagList list = new NBTTagList();
                for (CPath path : paths) list.appendTag(serializeMarked(path));
            }

            return compound;
        }

        @Override
        public void deserializeNBT(NBTBase nbt)
        {
            NBTTagCompound compound = (NBTTagCompound) nbt;

            type = compound.getInteger("type");

            if (compound.hasKey("paths"))
            {
                NBTTagList list = (NBTTagList) compound.getTag("paths");
                paths = new CPath[list.tagCount()];
                for (int i = 0; i < paths.length; i++)
                {
                    paths[i] = (CPath) deserializeMarked(list.getCompoundTagAt(i));
                }
            }
            else paths = new CPath[0];
        }
    }
}
