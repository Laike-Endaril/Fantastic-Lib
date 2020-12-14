package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.UUID;

public class GametypeChangedEvent extends Event
{
    public static final HashMap<Pair<UUID, Boolean>, GameType> PLAYER_GAMETYPES = new HashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(GametypeChangedEvent.class);
    }

    public final EntityPlayer player;
    public final GameType oldGameType, newGameType;

    public GametypeChangedEvent(EntityPlayer player, GameType oldGameType, GameType newGameType)
    {
        this.player = player;
        this.oldGameType = oldGameType;
        this.newGameType = newGameType;
    }

    @SubscribeEvent
    public static void playerLogoff(PlayerEvent.PlayerLoggedOutEvent event)
    {
        GameType oldGameType = PLAYER_GAMETYPES.remove(new Pair<>(event.player.getPersistentID(), event.player instanceof EntityPlayerMP));
        if (oldGameType != null) MinecraftForge.EVENT_BUS.post(new GametypeChangedEvent(event.player, oldGameType, null));
    }

    public static void serverStopped(FMLServerStoppedEvent event)
    {
        PLAYER_GAMETYPES.clear();
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event)
    {
        if (event.phase == TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        Pair<UUID, Boolean> pair = new Pair<>(player.getPersistentID(), player instanceof EntityPlayerMP);
        GameType oldGameType = PLAYER_GAMETYPES.get(pair), newGameType = MCTools.getGameType(player);
        if (oldGameType != newGameType)
        {
            PLAYER_GAMETYPES.put(pair, newGameType);
            MinecraftForge.EVENT_BUS.post(new GametypeChangedEvent(player, oldGameType, newGameType));
        }
    }
}
