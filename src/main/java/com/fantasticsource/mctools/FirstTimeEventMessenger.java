package com.fantasticsource.mctools;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;

public class FirstTimeEventMessenger
{
    protected static final HashSet<Class<? extends Event>> DONE = new HashSet<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(FirstTimeEventMessenger.class);
    }

    public static void init()
    {
        //Indirectly runs static block above
    }

    @SubscribeEvent
    public static void event(Event event)
    {
        if (DONE.contains(event.getClass())) return;

        DONE.add(event.getClass());
        System.out.println(TextFormatting.DARK_AQUA + "Event: " + event.getClass().getName());
    }
}
