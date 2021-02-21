package com.fantasticsource.mctools.component.path;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathEntityLookVec extends CPath
{
    public Entity entity;


    public CPathEntityLookVec()
    {
    }

    public CPathEntityLookVec(Entity entity)
    {
        this.entity = entity;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        if (entity == null) return null;

        return MCTools.getVectorN(entity.getLookVec());
    }


    @Override
    public CPathEntityLookVec write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(entity.getEntityId());

        return this;
    }

    @Override
    public CPathEntityLookVec read(ByteBuf buf)
    {
        super.read(buf);

        entity = MCTools.getValidEntityByID(buf.readInt());

        return this;
    }

    @Override
    public CPathEntityLookVec save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(entity.getEntityId()).save(stream);

        return this;
    }

    @Override
    public CPathEntityLookVec load(InputStream stream)
    {
        super.load(stream);

        entity = MCTools.getValidEntityByID(new CInt().load(stream).value);

        return this;
    }
}
