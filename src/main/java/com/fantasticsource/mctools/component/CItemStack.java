package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class CItemStack extends Component
{
    public ItemStack stack = ItemStack.EMPTY;

    @Override
    public CItemStack write(ByteBuf buf)
    {
        new CStringUTF8().set(stack.serializeNBT().toString()).write(buf);
        return this;
    }

    @Override
    public CItemStack read(ByteBuf buf)
    {
        try
        {
            stack = new ItemStack(JsonToNBT.getTagFromJson(new CStringUTF8().read(buf).value));
            return this;
        }
        catch (NBTException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public CItemStack save(OutputStream stream) throws IOException
    {
        new CStringUTF8().set(stack.serializeNBT().toString()).save(stream);
        return this;
    }

    @Override
    public CItemStack load(InputStream stream) throws IOException
    {
        try
        {
            stack = new ItemStack(JsonToNBT.getTagFromJson(new CStringUTF8().load(stream).value));
            return this;
        }
        catch (NBTException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
