package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CVec3D extends Component
{
    public CDouble x = new CDouble(), y = new CDouble(), z = new CDouble();

    public double getX()
    {
        return x.value;
    }

    public double getY()
    {
        return y.value;
    }

    public double getZ()
    {
        return z.value;
    }

    public Vec3d get()
    {
        return new Vec3d(x.value, y.value, z.value);
    }

    public CVec3D set(double x, double y, double z)
    {
        this.x.value = x;
        this.y.value = y;
        this.z.value = z;
        return this;
    }

    @Override
    public CVec3D write(ByteBuf buf)
    {
        x.write(buf);
        y.write(buf);
        z.write(buf);
        return this;
    }

    @Override
    public CVec3D read(ByteBuf buf)
    {
        x.read(buf);
        y.read(buf);
        z.read(buf);
        return this;
    }

    @Override
    public CVec3D save(OutputStream stream) throws IOException
    {
        x.save(stream);
        y.save(stream);
        z.save(stream);
        return this;
    }

    @Override
    public CVec3D load(InputStream stream) throws IOException
    {
        x.load(stream);
        y.load(stream);
        z.load(stream);
        return this;
    }
}
