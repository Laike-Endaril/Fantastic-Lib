package com.fantasticsource.mctools.component.path;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathFollowEntity extends CPath
{
    public Entity entityToFollow;


    public CPathFollowEntity()
    {
    }

    public CPathFollowEntity(Entity entityToFollow)
    {
        this.entityToFollow = entityToFollow;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        if (entityToFollow == null) return null;

        return new VectorN(entityToFollow.posX, entityToFollow.posY, entityToFollow.posZ);
    }


    @Override
    public CPathFollowEntity write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeInt(entityToFollow.getEntityId());

        return this;
    }

    @Override
    public CPathFollowEntity read(ByteBuf buf)
    {
        super.read(buf);

        entityToFollow = MCTools.getValidEntityByID(buf.readInt());

        return this;
    }

    @Override
    public CPathFollowEntity save(OutputStream stream)
    {
        super.save(stream);

        new CInt().set(entityToFollow.getEntityId()).save(stream);

        return this;
    }

    @Override
    public CPathFollowEntity load(InputStream stream)
    {
        super.load(stream);

        entityToFollow = MCTools.getValidEntityByID(new CInt().load(stream).value);

        return this;
    }
}
