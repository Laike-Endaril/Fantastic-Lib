package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.guielements.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class CVec3D extends Component
{
    public CDouble x, y, z;

    public CVec3D(Component holder)
    {
        super(holder);
        x = new CDouble(holder);
        y = new CDouble(holder);
        z = new CDouble(holder);
    }

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
    public void write(ByteBuf buf)
    {
        x.write(buf);
        y.write(buf);
        z.write(buf);
    }

    @Override
    public void read(ByteBuf buf)
    {
        x.read(buf);
        y.read(buf);
        z.read(buf);
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        x.save(stream);
        y.save(stream);
        z.save(stream);
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        x.load(stream);
        y.load(stream);
        z.load(stream);
    }

    @Override
    public String toString()
    {
        return x + ", " + y + ", " + z;
    }

    @Override
    public void parse(String string)
    {
        String[] tokens = string.split(",");
        x.parse(tokens[0].trim());
        y.parse(tokens[1].trim());
        z.parse(tokens[2].trim());
    }

    @Override
    public String label()
    {
        return "Position";
    }

    @Override
    public CVec3D copy()
    {
        return new CVec3D(holder).set(x.value, y.value, z.value);
    }

    @Override
    public GUIElement getGUIElement()
    {
        //TODO
        return null;
    }

    @Override
    public void setFromGUIElement(GUIElement element)
    {
        //TODO
    }
}
