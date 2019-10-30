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


    public int getGameDayAbsolute()
    {
        return (int) (worldTick / 24000L);
    }

    public int getGameTimeOfDayAbsolute()
    {
        return (int) (worldTick % 24000L);
    }


    public int getGameYear()
    {
        return getGameDayAbsolute() / 360 + 1;
    }

    public int getGameSeason()
    {
        return (getGameDayAbsolute() % 360) / 90 + 1;
    }

    public int getGameMonth()
    {
        return (getGameDayAbsolute() % 360) / 30 + 1;
    }

    public int getGameDay()
    {
        return (getGameDayAbsolute() % 30) + 1;
    }

    public int getGameHour()
    {
        return (int) ((worldTick % 24000L) / 1000L);
    }

    public int getGameMinute()
    {
        return (int) ((worldTick % 1000L) / 16.6666666667);
    }

    public int getGameSecond()
    {
        return (int) (((double) worldTick % 16.6666666667) / 0.27777777777);
    }
}
