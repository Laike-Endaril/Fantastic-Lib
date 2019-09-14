package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.*;

public abstract class Component
{
    public static Component writeMarked(ByteBuf buf, Component component)
    {
        new CStringUTF8().set(component.getClass().getName()).write(buf);
        component.write(buf);
        return component;
    }

    public static Component readMarked(ByteBuf buf)
    {
        try
        {
            return ((Component) Class.forName(new CStringUTF8().read(buf).value).newInstance()).read(buf);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static Component saveMarked(FileOutputStream stream, Component component) throws IOException
    {
        new CStringUTF8().set(component.getClass().getName()).save(stream);
        component.save(stream);
        return component;
    }

    public static Component loadMarked(FileInputStream stream) throws IOException
    {
        try
        {
            return ((Component) Class.forName(new CStringUTF8().load(stream).value).newInstance()).load(stream);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public abstract Component write(ByteBuf buf);

    public abstract Component read(ByteBuf buf);

    public abstract Component save(OutputStream stream) throws IOException;

    public abstract Component load(InputStream stream) throws IOException;

    public final Component copy() throws IOException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10240);
        save(os);
        return load(new ByteArrayInputStream(os.toByteArray()));
    }
}
