package com.fantasticsource.mctools;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;
import java.util.Map;

public class ClientTickTimer
{
    private static long currentTick = 0;
    private static LinkedHashMap<Runnable, Long> runnables = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(ClientTickTimer.class);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void update(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            currentTick++;

            for (Map.Entry<Runnable, Long> entry : runnables.entrySet().toArray(new Map.Entry[0]))
            {
                if (currentTick >= entry.getValue())
                {
                    runnables.remove(entry.getKey());
                    entry.getKey().run();
                }
            }
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
