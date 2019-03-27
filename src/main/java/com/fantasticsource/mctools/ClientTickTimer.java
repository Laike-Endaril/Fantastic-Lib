package com.fantasticsource.mctools;

import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class ClientTickTimer
{
    private static long currentTick = 0;

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void update(TickEvent.ClientTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) currentTick++;
    }

    public static long currentTick()
    {
        return currentTick;
    }
}
