package com.fantasticsource.mctools.component.path;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathEntityPitch extends CPath
{
    public Entity entity;


    public CPathEntityPitch()
    {
    }

    public CPathEntityPitch(Entity entity)
    {
        this.entity = entity;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        if (entity == null) return null;

        return new VectorN(Tools.degtorad(entity.rotationPitch), 0, 0);
    }


    @Override
    public CPathEntityPitch write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(entity.getEntityId());

        return this;
    }

    @Override
    public CPathEntityPitch read(ByteBuf buf)
    {
        super.read(buf);

        entity = MCTools.getValidEntityByID(buf.readInt());

        return this;
    }

    @Override
    public CPathEntityPitch save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(entity.getEntityId()).save(stream);

        return this;
    }

    @Override
    public CPathEntityPitch load(InputStream stream)
    {
        super.load(stream);

        entity = MCTools.getValidEntityByID(new CInt().load(stream).value);

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        compound.setInteger("entity", entity.getEntityId());

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        entity = MCTools.getValidEntityByID(((NBTTagCompound) nbt).getInteger("entity"));
    }
}
