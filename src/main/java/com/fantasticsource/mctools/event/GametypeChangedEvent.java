package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
    public static void playerLogoffServer(PlayerEvent.PlayerLoggedOutEvent event)
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

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void playerLogoffClient(FMLNetworkEvent.ClientDisconnectionFromServerEvent event)
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        GameType oldGameType = PLAYER_GAMETYPES.remove(new Pair<>(player.getPersistentID(), false));
        if (oldGameType != null) MinecraftForge.EVENT_BUS.post(new GametypeChangedEvent(player, oldGameType, null));
    }
}
