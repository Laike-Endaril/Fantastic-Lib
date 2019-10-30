package com.fantasticsource.mctools;

import com.fantasticsource.tools.Timestamp;
import net.minecraft.world.World;

import java.time.Instant;

public class MCTimestamp extends Timestamp
{
    public final long serverTick, clientTick, worldTick;

    public MCTimestamp(World world)
    {
        super(Instant.now());

        serverTick = ServerTickTimer.currentTick();
        clientTick = ClientTickTimer.currentTick();

        worldTick = world.getWorldTime();
    }

    public MCTimestamp(Instant instant, long serverTick, long clientTick, long worldTick)
    {
        super(instant);

        this.serverTick = serverTick;
        this.clientTick = clientTick;

        this.worldTick = worldTick;
    }

    public int getGameDay()
    {
        return (int) (worldTick / 24000L);
    }

    public int getGameTimeOfDay()
    {
        return (int) (worldTick % 24000L);
    }
}
