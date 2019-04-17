package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerData
{
    public static LinkedHashMap<UUID, PlayerData> playerData = new LinkedHashMap<>();

    private static String modDir = Loader.instance().getConfigDir().getAbsolutePath() + File.separator + FantasticLib.MODID + File.separator;
    private static String referenceDir = modDir + "reference" + File.separator;

    public String name;
    public EntityPlayer player;

    public PlayerData(String name)
    {
        this(name, null);
    }

    public PlayerData(String name, EntityPlayer player)
    {
        this.name = name;
        this.player = player;
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
            for (Map.Entry<UUID, PlayerData> entry : playerData.entrySet()) writer.write(entry.getKey() + " = " + entry.getValue().name + "\r\n");
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
        if (!file.exists())
        {
            file.mkdir();
            return;
        }

        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(new File(referenceDir + "players.txt")));
            String line = reader.readLine();
            while (!line.equals(""))
            {
                String[] tokens = line.split(",");
                playerData.put(UUID.fromString(tokens[0].trim()), new PlayerData(tokens[1].trim()));

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
    public static void playerLogon(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayer player = event.player;
        playerData.put(player.getPersistentID(), new PlayerData(player.getName(), player));
        save();
    }

    @SubscribeEvent
    public static void playerLogoff(PlayerEvent.PlayerLoggedOutEvent event)
    {
        playerData.get(event.player.getPersistentID()).player = null;
    }
}
