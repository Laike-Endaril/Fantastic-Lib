package com.fantasticsource.mctools;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ServerTickTimer
{
    private static long currentTick = 0;
    private static LinkedHashMap<Runnable, Long> runnables = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(ServerTickTimer.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void update(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            currentTick++;

            ArrayList<Runnable> removals = new ArrayList<>();
            for (Map.Entry<Runnable, Long> entry : runnables.entrySet())
            {
                if (currentTick >= entry.getValue())
                {
                    removals.add(entry.getKey());
                    entry.getKey().run();
                }
            }

            for (Runnable runnable : removals) runnables.remove(runnable);
        }
    }

    public static long currentTick()
    {
        return currentTick;
    }

    public static void schedule(int tickDelay, Runnable runnable)
    {
        runnables.put(runnable, currentTick + tickDelay);
    }

    public static void unschedule(Runnable runnable)
    {
        runnables.remove(runnable);
    }
}
