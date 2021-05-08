package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.*;

public abstract class Component
{
    public static <T extends Component> T writeMarked(ByteBuf buf, T component)
    {
        new CStringUTF8().set(component.getClass().getName()).write(buf);
        component.write(buf);
        return component;
    }

    public static <T extends Component> T writeMarkedOrNull(ByteBuf buf, T component)
    {
        if (component == null) buf.writeBoolean(false);
        else
        {
            buf.writeBoolean(true);
            writeMarked(buf, component);
        }
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

    public static Component readMarkedOrNull(ByteBuf buf)
    {
        if (buf.readBoolean()) return readMarked(buf);
        return null;
    }

    public static <T extends Component> T saveMarked(OutputStream stream, T component)
    {
        new CStringUTF8().set(component.getClass().getName()).save(stream);
        component.save(stream);
        return component;
    }

    public static <T extends Component> T saveMarkedOrNull(OutputStream stream, T component)
    {
        if (component == null) new CBoolean().set(false).save(stream);
        else
        {
            new CBoolean().set(true).save(stream);
            saveMarked(stream, component);
        }
        return component;
    }

    public static Component loadMarked(InputStream stream)
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

    public static Component loadMarkedOrNull(InputStream stream)
    {
        if (new CBoolean().load(stream).value) return loadMarked(stream);
        return null;
    }

    public static <T extends Component> T writeTextMarked(BufferedWriter writer, T component)
    {
        try
        {
            writer.write(component.getClass().getName() + "\r\n");
            component.writeText(writer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return component;
    }

    public static Component readTextMarked(BufferedReader reader)
    {
        try
        {
            return ((Component) Class.forName(reader.readLine()).newInstance()).readText(reader);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public abstract Component write(ByteBuf buf);

    public abstract Component read(ByteBuf buf);

    public abstract Component save(OutputStream stream);

    public abstract Component load(InputStream stream);

    public Component writeText(BufferedWriter writer)
    {
        return this;
    }

    public Component readText(BufferedReader reader)
    {
        return this;
    }


    public Component copy()
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream(10240);
        saveMarked(os, this);
        return loadMarked(new ByteArrayInputStream(os.toByteArray()));
    }


    public void onClientSync()
    {
    }
}
