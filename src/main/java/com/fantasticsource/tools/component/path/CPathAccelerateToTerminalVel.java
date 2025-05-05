package com.fantasticsource.tools.component.path;

import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.component.CLong;
import com.fantasticsource.tools.component.CVectorN;
import com.fantasticsource.tools.datastructures.VectorN;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagList;

import java.io.InputStream;
import java.io.OutputStream;

public class CPathAccelerateToTerminalVel extends CPath
{
    public long timeToTerminalVelocity;
    public VectorN terminalVelocity;


    public CPathAccelerateToTerminalVel()
    {
    }

    public CPathAccelerateToTerminalVel(long timeToTerminalVelocity, double... values)
    {
        this(timeToTerminalVelocity, new VectorN(values));
    }

    public CPathAccelerateToTerminalVel(long timeToTerminalVelocity, VectorN terminalVelocity)
    {
        this.timeToTerminalVelocity = timeToTerminalVelocity;
        this.terminalVelocity = terminalVelocity;
    }


    @Override
    public VectorN getRelativePositionInternal(long time)
    {
        VectorN result = terminalVelocity.copy();
        if (timeToTerminalVelocity == 0) return result;


        long minTime = Tools.min(time, timeToTerminalVelocity);
        for (int i = 0; i < result.values.length; i++)
        {
            if (result.values[i] < 0) result.values[i]
                    //Accelerative part of equation
                    = 0.5 * result.values[i] * minTime - timeToTerminalVelocity * terminalVelocity.values[i] * TrigLookupTable.TRIG_TABLE_1048576.sin(Math.PI * minTime / timeToTerminalVelocity) / (2 * Math.PI)
                    //Terminal velocity part of equation
                    + Tools.min(0, (time - timeToTerminalVelocity) * terminalVelocity.values[i]);

            else if (result.values[i] > 0) result.values[i]
                    //Accelerative part of equation
                    = 0.5 * result.values[i] * minTime - timeToTerminalVelocity * terminalVelocity.values[i] * TrigLookupTable.TRIG_TABLE_1048576.sin(Math.PI * minTime / timeToTerminalVelocity) / (2 * Math.PI)
                    //Terminal velocity part of equation
                    + Tools.max(0, (time - timeToTerminalVelocity) * terminalVelocity.values[i]);
        }
        return result.scale(0.001);
    }


    @Override
    public CPathAccelerateToTerminalVel write(ByteBuf buf)
    {
        super.write(buf);

        buf.writeLong(timeToTerminalVelocity);
        new CVectorN().set(terminalVelocity).write(buf);

        return this;
    }

    @Override
    public CPathAccelerateToTerminalVel read(ByteBuf buf)
    {
        super.read(buf);

        timeToTerminalVelocity = buf.readLong();
        terminalVelocity = new CVectorN().read(buf).value;

        return this;
    }

    @Override
    public CPathAccelerateToTerminalVel save(OutputStream stream)
    {
        super.save(stream);

        new CLong().set(timeToTerminalVelocity).save(stream);
        new CVectorN().set(terminalVelocity).save(stream);

        return this;
    }

    @Override
    public CPathAccelerateToTerminalVel load(InputStream stream)
    {
        super.load(stream);

        timeToTerminalVelocity = new CLong().load(stream).value;
        terminalVelocity = new CVectorN().load(stream).value;

        return this;
    }


    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound compound = super.serializeNBT();

        compound.setLong("timeToTerminalVelocity", timeToTerminalVelocity);
        if (terminalVelocity.values.length > 0)
        {
            NBTTagList list = new NBTTagList();
            for (double d : terminalVelocity.values) list.appendTag(new NBTTagDouble(d));
            compound.setTag("terminalVelocity", list);
        }

        return compound;
    }

    @Override
    public void deserializeNBT(NBTBase nbt)
    {
        super.deserializeNBT(nbt);

        NBTTagCompound compound = (NBTTagCompound) nbt;

        timeToTerminalVelocity = compound.getLong("timeToTerminalVelocity");

        terminalVelocity = new VectorN();
        if (compound.hasKey("terminalVelocity"))
        {
            NBTTagList list = (NBTTagList) compound.getTag("terminalVelocity");
            terminalVelocity.values = new double[list.tagCount()];
            for (int i = 0; i < terminalVelocity.values.length; i++) terminalVelocity.values[i] = list.getDoubleAt(i);
        }
    }
}
