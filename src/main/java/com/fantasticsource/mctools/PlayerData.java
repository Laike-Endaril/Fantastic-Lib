package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.UUID;

public class PlayerData
{
    public static LinkedHashMap<UUID, PlayerData> playerData = new LinkedHashMap<>();

    private static String modDir = Loader.instance().getConfigDir().getAbsolutePath() + File.separator + FantasticLib.MODID + File.separator;
    private static String referenceDir = modDir + "reference" + File.separator;

    public String name;
    public UUID id;
    public EntityPlayer player;

    public PlayerData(String name, UUID id)
    {
        this.player = null;
        this.name = name;
        this.id = id;
    }

    public PlayerData(EntityPlayer player)
    {
        this.player = player;
        name = player.getName();
        id = player.getPersistentID();
    }


    public static PlayerData get(UUID id)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        PlayerList playerList = server == null ? null : server.getPlayerList();
        EntityPlayer player = playerList == null ? null : playerList.getPlayerByUUID(id);

        if (player != null)
        {
            PlayerData data = new PlayerData(player);
            playerData.put(player.getPersistentID(), data);
            return data;
        }

        PlayerData data = playerData.get(id);
        if (data != null) data.player = player;
        return data;
    }

    public static PlayerData get(String name)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        PlayerList playerList = server == null ? null : server.getPlayerList();
        EntityPlayer player = playerList == null ? null : playerList.getPlayerByUsername(name);

        if (player != null)
        {
            PlayerData data = new PlayerData(player);
            playerData.put(player.getPersistentID(), data);
            return data;
        }

        for (PlayerData data : playerData.values())
        {
            if (data.name.equals(name)) return data;
        }

        return null;
    }


    public static String getName(UUID id)
    {
        PlayerData data = get(id);
        return data == null ? null : data.name;
    }

    public static UUID getID(String name)
    {
        PlayerData data = get(name);
        return data == null ? null : data.id;
    }

    public static EntityPlayer getEntity(UUID id)
    {
        PlayerData data = get(id);
        return data == null ? null : data.player;
    }

    public static EntityPlayer getEntity(String name)
    {
        PlayerData data = get(name);
        return data == null ? null : data.player;
    }


    public static void save()
    {
        File file = new File(modDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir);
        if (!file.exists()) file.mkdir();

        try
        {
            BufferedWriter writer = new BufferedWriter(new FileWriter(new File(referenceDir + "players.txt")));
            for (UUID id : playerData.keySet())
            {
                PlayerData data = get(id);
                if (data != null && data.name != null) writer.write(id + " = " + data.name + "\r\n");
            }
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void load()
    {
        File file = new File(modDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir);
        if (!file.exists()) file.mkdir();

        file = new File(referenceDir + "players.txt");
        if (!file.exists())
        {
            save();
            return;
        }

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null && !line.trim().equals(""))
            {
                String[] tokens = line.split("=");
                UUID id = UUID.fromString(tokens[0].trim());
                playerData.put(id, new PlayerData(tokens[1].trim(), id));

                line = reader.readLine();
            }
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }


    @SubscribeEvent
    public static void playerLogon(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayerMP)
        {
            EntityPlayerMP player = (EntityPlayerMP) entity;
            playerData.put(player.getPersistentID(), new PlayerData(player));
            save();
        }
    }

    @SubscribeEvent
    public static void playerLogoff(PlayerEvent.PlayerLoggedOutEvent event)
    {
        if (event.player != null)
        {
            UUID id = event.player.getPersistentID();
            if (playerData.containsKey(id))
            {
                playerData.get(id).player = null;
            }
        }
    }
}
