package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import io.netty.buffer.ByteBuf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CArrayList extends Component
{
    public ArrayList<Component> value = new ArrayList<>();

    public CArrayList set(ArrayList<Component> value)
    {
        this.value.clear();
        this.value.addAll(value);
        return this;
    }

    @Override
    public void write(ByteBuf buf)
    {
        for (Component component : value) component.write(buf);
    }

    @Override
    public void read(ByteBuf buf)
    {
        for (Component component : value) component.read(buf);
    }

    @Override
    public void save(FileOutputStream stream) throws IOException
    {
        for (Component component : value) component.save(stream);
    }

    @Override
    public void load(FileInputStream stream) throws IOException
    {
        for (Component component : value) component.load(stream);
    }

    @Override
    public void parse(String string)
    {
    }

    @Override
    public Component copy()
    {
        return new CArrayList().set(value);
    }

    @Override
    public GUIElement getGUIElement(GUIScreen screen)
    {
        GUIScrollView result = new GUIScrollView(screen, 1, 1);
        for (Component component : value) result.add(component.getGUIElement(screen));
        return result;
    }

    @Override
    public void setFromGUIElement(GUIElement element)
    {
    }
}
