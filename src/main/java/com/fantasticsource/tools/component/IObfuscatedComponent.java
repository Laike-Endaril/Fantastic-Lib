package com.fantasticsource.tools.component;

import io.netty.buffer.ByteBuf;

public interface IObfuscatedComponent
{
    public static <T extends Component & IObfuscatedComponent> T writeMarkedObf(ByteBuf buf, T component)
    {
        new CStringUTF8().set(component.getClass().getName()).write(buf);
        component.writeObf(buf);
        return component;
    }

    public static Component readMarkedObf(ByteBuf buf)
    {
        try
        {
            return ((Component & IObfuscatedComponent) Class.forName(new CStringUTF8().read(buf).value).newInstance()).readObf(buf);
        }
        catch (InstantiationException | IllegalAccessException | ClassNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    Component writeObf(ByteBuf buf);

    Component readObf(ByteBuf buf);
}
