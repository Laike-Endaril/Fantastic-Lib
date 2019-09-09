package com.fantasticsource.mctools.ecs;

import com.fantasticsource.mctools.ecs.component.Component;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class ECSEntity
{
    public ECSEntity parent;

    private LinkedHashMap<Class<? extends Component>, Component> components = new LinkedHashMap<>();

    public ECSEntity(ECSEntity parent, Component... components)
    {
        this.parent = parent;
        for (Component component : components) this.components.put(component.getClass(), component);
    }

    public Component put(Component component)
    {
        return components.put(component.getClass(), component);
    }

    public <T extends Component> T get(Class<T> componentClass)
    {
        return (T) components.get(componentClass);
    }

    public boolean has(Class<? extends Component> componentClass)
    {
        return components.containsKey(componentClass);
    }

    public void write(ByteBuf buf)
    {
        byte[] bytes;
        buf.writeInt(components.size());
        for (Map.Entry<Class<? extends Component>, Component> entry : components.entrySet())
        {
            bytes = entry.getKey().getName().getBytes(UTF_8);
            buf.writeInt(bytes.length);
            buf.writeBytes(bytes);

            entry.getValue().write(buf);
        }
    }

    public void read(ByteBuf buf) throws ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Component component;
        int length;
        for (int i = buf.readInt(); i > 0; i--)
        {
            length = buf.readInt();
            component = (Component) Class.forName(buf.toString(buf.readerIndex(), length, UTF_8)).newInstance();
            component.read(buf);
            put(component);
        }
    }

    public void save(FileOutputStream stream) throws IOException
    {
        byte[] bytes;
        int i = components.size();
        stream.write(new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i});
        for (Map.Entry<Class<? extends Component>, Component> entry : components.entrySet())
        {
            bytes = entry.getKey().getName().getBytes(UTF_8);
            i = bytes.length;
            stream.write(new byte[]{(byte) (i >>> 24), (byte) (i >>> 16), (byte) (i >>> 8), (byte) i});
            stream.write(bytes);

            entry.getValue().save(stream);
        }
    }

    public void load(FileInputStream stream) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException
    {
        Component component;
        int length;
        byte[] bytes = new byte[4], bytes2;

        if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
        int i = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);
        for (; i > 0; i--)
        {
            if (stream.read(bytes) < 4) throw new IOException("Reached end of file while reading!");
            length = ((bytes[0] & 0xFF) << 24) | ((bytes[1] & 0xFF) << 16) | ((bytes[2] & 0xFF) << 8) | (bytes[3] & 0xFF);

            bytes2 = new byte[length];
            if (stream.read(bytes2) < length) throw new IOException("Reached end of file while reading!");
            component = (Component) Class.forName(new String(bytes, UTF_8)).newInstance();
            component.load(stream);
            put(component);
        }
    }

    public ECSEntity copy()
    {
        ECSEntity result = new ECSEntity(parent);
        for (Component component : components.values()) component.copyTo(result);
        return result;
    }
}
