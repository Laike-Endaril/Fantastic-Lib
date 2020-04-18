package com.fantasticsource.mctools.data;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.component.CStringUTF8;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.network.ByteBufUtils;

import java.io.*;
import java.util.LinkedHashMap;

public abstract class CDataHandler extends Component
{
    private static final LinkedHashMap<Class<? extends CDataHandler>, LinkedHashMap<String, CDataHandler>> SERVER_DATA_HANDLERS = new LinkedHashMap<>();
    private static final LinkedHashMap<Class<? extends CDataHandler>, LinkedHashMap<String, CDataHandler>> SYNCED_DATA_HANDLERS = new LinkedHashMap<>();


    String key;
    final boolean sync;

    public CDataHandler()
    {
        //Required for component functions
        sync = true;
    }

    public CDataHandler(String key, boolean sync)
    {
        this.key = key;
        this.sync = sync;

        if (sync) SYNCED_DATA_HANDLERS.computeIfAbsent(getClass(), o -> new LinkedHashMap<>()).put(key, this);
        else SERVER_DATA_HANDLERS.computeIfAbsent(getClass(), o -> new LinkedHashMap<>()).put(key, this);

        if (sync && MCTools.hosting())
        {
            for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
            {
                Network.WRAPPER.sendTo(new Network.GenericComponentPacket(this), player);
            }
        }
    }

    @Override
    public CDataHandler write(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, key);

        return this;
    }

    @Override
    public CDataHandler read(ByteBuf buf)
    {
        key = ByteBufUtils.readUTF8String(buf);

        return this;
    }

    @Override
    public CDataHandler save(OutputStream stream)
    {
        new CStringUTF8().set(key).save(stream);

        return this;
    }

    @Override
    public CDataHandler load(InputStream stream)
    {
        key = new CStringUTF8().load(stream).value;

        return this;
    }

    @Override
    public CDataHandler writeText(BufferedWriter writer)
    {
        try
        {
            writer.write(key + "\r\n");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return this;
    }

    @Override
    public CDataHandler readText(BufferedReader reader)
    {
        try
        {
            key = reader.readLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return this;
    }

    public abstract void load();

    public abstract void save();

    public abstract void clear();

    public final void reload()
    {
        clear();
        load();
    }

    public final void destroy()
    {
        LinkedHashMap<String, CDataHandler> dataHandlers = sync ? SYNCED_DATA_HANDLERS.get(getClass()) : SERVER_DATA_HANDLERS.get(getClass());
        if (dataHandlers != null)
        {
            dataHandlers.remove(key);
            if (dataHandlers.size() == 0)
            {
                if (sync) SYNCED_DATA_HANDLERS.remove(getClass());
                else SERVER_DATA_HANDLERS.remove(getClass());
            }
        }
        for (EntityPlayerMP player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers())
        {
            Network.WRAPPER.sendTo(new Network.GenericComponentPacket(new Removal(key)), player);
        }
    }

    public final void syncToClient(EntityPlayerMP player)
    {
        Network.WRAPPER.sendTo(new Network.GenericComponentPacket(this), player);
    }

    @Override
    public void onClientSync()
    {
        SYNCED_DATA_HANDLERS.computeIfAbsent(getClass(), o -> new LinkedHashMap<>()).put(key, this);
    }


    @SubscribeEvent
    public static void syncOnLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        for (LinkedHashMap<String, CDataHandler> handlers : SYNCED_DATA_HANDLERS.values())
        {
            for (CDataHandler handler : handlers.values()) handler.syncToClient((EntityPlayerMP) event.player);
        }
    }

    protected static void load(Class<? extends CDataHandler> cls)
    {
        for (LinkedHashMap<String, CDataHandler> dataHandlers : new LinkedHashMap[]{SYNCED_DATA_HANDLERS.get(cls), SERVER_DATA_HANDLERS.get(cls)})
        {
            if (dataHandlers != null)
            {
                for (CDataHandler handler : dataHandlers.values())
                {
                    handler.load();
                }
            }
        }
    }

    protected static void clear(Class<? extends CDataHandler> cls)
    {
        LinkedHashMap<String, CDataHandler> dataHandlers = SYNCED_DATA_HANDLERS.get(cls);
        if (dataHandlers != null)
        {
            for (CDataHandler handler : dataHandlers.values())
            {
                handler.load();
            }
        }
    }


    public static CDataHandler get(Class<? extends CDataHandler> cls, String key, boolean sync)
    {
        LinkedHashMap<String, CDataHandler> dataHandlers = sync ? SYNCED_DATA_HANDLERS.get(cls) : SERVER_DATA_HANDLERS.get(cls);
        return dataHandlers == null ? null : dataHandlers.get(key);
    }


    private static class Removal extends CDataHandler
    {
        public Removal(String key)
        {
            super();
            this.key = key;
        }

        @Override
        public void load()
        {
        }

        @Override
        public void save()
        {
        }

        @Override
        public void clear()
        {
        }

        @Override
        public void onClientSync()
        {
            LinkedHashMap<String, CDataHandler> dataHandlers = SYNCED_DATA_HANDLERS.get(getClass());
            if (dataHandlers != null)
            {
                dataHandlers.remove(key);
                if (dataHandlers.size() == 0) SYNCED_DATA_HANDLERS.remove(getClass());
            }
        }
    }
}
