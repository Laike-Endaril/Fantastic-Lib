package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.Vec3d;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

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
    public CVec3D save(FileOutputStream stream) throws IOException
    {
        x.save(stream);
        y.save(stream);
        z.save(stream);
        return this;
    }

    @Override
    public CVec3D load(FileInputStream stream) throws IOException
    {
        x.load(stream);
        y.load(stream);
        z.load(stream);
        return this;
    }

    @Override
    public String toString()
    {
        return x + ", " + y + ", " + z;
    }

    @Override
    public CVec3D parse(String string)
    {
        String[] tokens = string.split(",");
        x.parse(tokens[0].trim());
        y.parse(tokens[1].trim());
        z.parse(tokens[2].trim());
        return this;
    }

    @Override
    public String label()
    {
        return "Position";
    }

    @Override
    public CVec3D copy()
    {
        return new CVec3D().set(x.value, y.value, z.value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        //TODO
        return null;
    }

    @Override
    public CVec3D setFromGUIElement(GUIElement element)
    {
        //TODO
        return this;
    }
}
