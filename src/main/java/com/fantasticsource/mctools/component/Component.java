package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Component
{
    public Component holder;

    public Component()
    {
        this(null);
    }

    public Component(Component holder)
    {
        this.holder = holder;
    }

    public abstract void write(ByteBuf buf);

    public abstract void read(ByteBuf buf);

    public abstract void save(FileOutputStream stream) throws IOException;

    public abstract void load(FileInputStream stream) throws IOException;

    /**
     * Correlates to toString(), for use in GUI editing
     */
    public abstract void parse(String string);

    public abstract Component copy();

    public void copyTo(Component holder)
    {
        Component c = copy();
        c.holder = holder;
    }

    /**
     * The label for this component when editing it via GUI
     */
    public String label()
    {
        return getClass().getSimpleName();
    }

    public abstract GUIElement getGUIElement(GUIScreen screen);

    public abstract void setFromGUIElement(GUIElement element);
}
