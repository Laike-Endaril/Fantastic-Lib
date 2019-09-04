package com.fantasticsource.mctools;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.LinkedHashMap;

public class ClientTickTimer
{
    private static long currentTick = 0;
    private static LinkedHashMap<Long, Runnable> runnables = new LinkedHashMap<>();

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
            runnables.entrySet().removeIf(e -> e.getKey() >= currentTick && execute(e.getValue()));
        }
    }

    private static boolean execute(Runnable runnable)
    {
        runnable.run();
        return true;
    }

    public static long currentTick()
    {
        return currentTick;
    }

    public static void schedule(int tickDelay, Runnable runnable)
    {
        runnables.put(currentTick + tickDelay, runnable);
    }
}
