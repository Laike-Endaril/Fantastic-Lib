package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

import java.io.InputStream;
import java.io.OutputStream;
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
    public CArrayList write(ByteBuf buf)
    {
        new CInt().set(value.size()).write(buf);
        for (Component component : value)
        {
            new CStringUTF8().set(component.getClass().getName()).write(buf);
            component.write(buf);
        }
        return this;
    }

    @Override
    public CArrayList read(ByteBuf buf)
    {
        value.clear();
        for (int i = new CInt().read(buf).value; i > 0; i--)
        {
            String classname = new CStringUTF8().read(buf).value;
            try
            {
                value.add(((Component) Class.forName(classname).newInstance()).read(buf));
            }
            catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return this;
    }

    @Override
    public CArrayList save(OutputStream stream)
    {
        new CInt().set(value.size()).save(stream);
        for (Component component : value)
        {
            new CStringUTF8().set(component.getClass().getName()).save(stream);
            component.save(stream);
        }
        return this;
    }

    @Override
    public CArrayList load(InputStream stream)
    {
        value.clear();
        for (int i = new CInt().load(stream).value; i > 0; i--)
        {
            String classname = new CStringUTF8().load(stream).value;
            try
            {
                value.add(((Component) Class.forName(classname).newInstance()).load(stream));
            }
            catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
            {
                e.printStackTrace();
            }
        }
        return this;
    }
}
