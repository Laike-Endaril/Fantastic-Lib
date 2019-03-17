package com.fantasticsource.mctools;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class PlayerInventoryChangedEvent extends Event
{
    private static LinkedHashMap<EntityPlayer, ContainerPlayer> inventories = new LinkedHashMap<>();

    public final EntityPlayer player;

    private PlayerInventoryChangedEvent(EntityPlayer player)
    {
        this.player = player;
    }


    @SubscribeEvent
    public static void serverShutdown(FMLServerStoppingEvent event)
    {
        inventories.clear();
    }

    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END)
        {
            ArrayList<EntityPlayer> players = new ArrayList<>();
            for (World world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
            {
                for (EntityPlayer p : world.playerEntities)
                {
                    players.add(p);
                    System.out.println(p.inventory.getTimesChanged());
                }
            }
        }
    }
}
