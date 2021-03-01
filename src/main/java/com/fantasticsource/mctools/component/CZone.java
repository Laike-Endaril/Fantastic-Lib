package com.fantasticsource.mctools.component;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;

import java.io.InputStream;
import java.io.OutputStream;

public class CZone extends Component
{
    protected BlockPos min, max;

    public CZone()
    {
        this(BlockPos.ORIGIN, BlockPos.ORIGIN);
    }

    public CZone(BlockPos pos1, BlockPos pos2)
    {
        setBounds(pos1, pos2);
    }


    public CZone setBounds(BlockPos pos1, BlockPos pos2)
    {
        min = new BlockPos(Tools.min(pos1.getX(), pos2.getX()), Tools.min(pos1.getY(), pos2.getY()), Tools.min(pos1.getZ(), pos2.getZ()));
        max = new BlockPos(Tools.max(pos1.getX(), pos2.getX()), Tools.max(pos1.getY(), pos2.getY()), Tools.max(pos1.getZ(), pos2.getZ()));

        return this;
    }

    public BlockPos getMin()
    {
        return new BlockPos(min);
    }

    public BlockPos getMax()
    {
        return new BlockPos(max);
    }

    @Override
    public CZone write(ByteBuf buf)
    {
        buf.writeInt(min.getX());
        buf.writeInt(min.getY());
        buf.writeInt(min.getZ());

        buf.writeInt(max.getX());
        buf.writeInt(max.getY());
        buf.writeInt(max.getZ());

        return this;
    }

    @Override
    public CZone read(ByteBuf buf)
    {
        min = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
        max = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());

        return this;
    }

    @Override
    public CZone save(OutputStream stream)
    {
        new CInt().set(min.getX()).save(stream).set(min.getY()).save(stream).set(min.getZ()).save(stream).set(max.getX()).save(stream).set(max.getY()).save(stream).set(max.getZ()).save(stream);

        return this;
    }

    @Override
    public CZone load(InputStream stream)
    {
        CInt ci = new CInt();
        min = new BlockPos(ci.load(stream).value, ci.load(stream).value, ci.load(stream).value);
        max = new BlockPos(ci.load(stream).value, ci.load(stream).value, ci.load(stream).value);

        return this;
    }
}
