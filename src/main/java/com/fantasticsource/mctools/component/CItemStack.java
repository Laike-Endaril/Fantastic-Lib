package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.io.InputStream;
import java.io.OutputStream;

public class CItemStack extends Component
{
    public ItemStack value = ItemStack.EMPTY;

    public CItemStack()
    {
    }

    public CItemStack(ItemStack value)
    {
        set(value);
    }

    public CItemStack set(ItemStack value)
    {
        this.value = value;

        return this;
    }

    @Override
    public CItemStack write(ByteBuf buf)
    {
        new CStringUTF8().set(value.serializeNBT().toString()).write(buf);
        return this;
    }

    @Override
    public CItemStack read(ByteBuf buf)
    {
        try
        {
            return set(new ItemStack(JsonToNBT.getTagFromJson(new CStringUTF8().read(buf).value)));
        }
        catch (NBTException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CItemStack save(OutputStream stream)
    {
        new CStringUTF8().set(value.serializeNBT().toString()).save(stream);
        return this;
    }

    @Override
    public CItemStack load(InputStream stream)
    {
        try
        {
            return set(new ItemStack(JsonToNBT.getTagFromJson(new CStringUTF8().load(stream).value)));
        }
        catch (NBTException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
