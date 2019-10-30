package com.fantasticsource.mctools;

import com.fantasticsource.tools.Timestamp;
import net.minecraft.world.World;

import java.time.Instant;

public class MCTimestamp extends Timestamp
{
    public final long serverTick, clientTick, worldTime;

    protected MCTimestamp(World world)
    {
        super(Instant.now());

        serverTick = ServerTickTimer.currentTick();
        clientTick = ClientTickTimer.currentTick();

        worldTime = world.getWorldTime();
    }

    public int getGameDay()
    {
        return (int) (worldTime / 24000L);
    }

    public int getGameTimeOfDay()
    {
        return (int) (worldTime % 24000L);
    }
}
