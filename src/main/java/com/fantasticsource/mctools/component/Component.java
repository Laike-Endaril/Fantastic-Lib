package com.fantasticsource.mctools.component;

import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public abstract class Component
{
    public Component holder = null;
    public ArrayList<Component> subComponents = new ArrayList<>();

    public Component(Component holder)
    {
        if (holder != null)
        {
            this.holder = holder;
            holder.subComponents.add(this);
        }
    }

    public void write(ByteBuf buf)
    {
        for (Component component : subComponents) component.write(buf);
    }

    public void read(ByteBuf buf)
    {
        for (Component component : subComponents) component.read(buf);
    }

    public void save(FileOutputStream stream) throws IOException
    {
        for (Component component : subComponents) component.save(stream);
    }

    public void load(FileInputStream stream) throws IOException
    {
        for (Component component : subComponents) component.load(stream);
    }

    /**
     * Correlates to toString(), for use in GUI editing
     */
    public void parse(String string)
    {
        for (Component component : subComponents) component.parse(string);
    }

    public Component copy()
    {
        for (Component component : subComponents) component.copy();
        return this;
    }

    public final void copyTo(Component holder)
    {
        Component c = copy();
        c.holder = holder;
        holder.subComponents.add(c);
    }

    /**
     * The label for this component when editing it via GUI
     */
    public String label()
    {
        return getClass().getSimpleName();
    }
}
