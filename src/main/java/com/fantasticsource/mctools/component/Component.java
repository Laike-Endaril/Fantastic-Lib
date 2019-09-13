package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class Component
{
    public abstract Component write(ByteBuf buf);

    public abstract Component read(ByteBuf buf);

    public abstract Component save(FileOutputStream stream) throws IOException;

    public abstract Component load(FileInputStream stream) throws IOException;

    /**
     * Correlates to toString(), for use in GUI editing
     */
    public abstract Component parse(String string);

    public abstract Component copy();

    /**
     * The label for this component when editing it via GUI
     */
    public String label()
    {
        return getClass().getSimpleName();
    }

    public abstract GUIElement getGUIElement(GUIScreen screen);

    public abstract Component setFromGUIElement(GUIElement element);
}
