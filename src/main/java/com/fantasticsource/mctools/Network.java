package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.mctools.component.CResourceLocation;
import com.fantasticsource.mctools.controlintercept.ControlEvent;
import com.fantasticsource.mctools.sound.SimpleSound;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(FantasticLib.MODID);
    private static int discriminator = 0;
    private static ArrayList<Runnable> serverActions = new ArrayList<>(), clientActions = new ArrayList<>();
    private static EntityPlayerMP currentActionPlayer = null;


    public static void init()
    {
        WRAPPER.registerMessage(PlaySimpleSoundPacketHandler.class, PlaySimpleSoundPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ControlEventPacketHandler.class, ControlEventPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(BasicPacketToServerHandler.class, BasicPacketToServer.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(BasicPacketToClientHandler.class, BasicPacketToClient.class, discriminator++, Side.CLIENT);
    }


    public static EntityPlayerMP getCurrentActionPlayer()
    {
        return currentActionPlayer;
    }

    public static int registerBasicPacketToServer(Runnable action)
    {
        serverActions.add(action);
        return serverActions.size() - 1;
    }

    public static int registerBasicPacketToClient(Runnable action)
    {
        clientActions.add(action);
        return clientActions.size() - 1;
    }


    public static class PlaySimpleSoundPacket implements IMessage
    {
        public CResourceLocation rl = new CResourceLocation();

        public PlaySimpleSoundPacket()
        {
            //Required
        }

        public PlaySimpleSoundPacket(ResourceLocation rl)
        {
            this.rl.set(rl);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            rl.write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            rl.read(buf);
        }
    }

    public static class PlaySimpleSoundPacketHandler implements IMessageHandler<PlaySimpleSoundPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PlaySimpleSoundPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> mc.getSoundHandler().playSound(new SimpleSound(packet.rl.value, SoundCategory.MASTER)));
            return null;
        }
    }

    public static class ControlEventPacket implements IMessage
    {
        public ControlEvent event;

        public ControlEventPacket()
        {
            //Required
        }

        public ControlEventPacket(ControlEvent event)
        {
            this.event = event;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            ByteBufUtils.writeUTF8String(buf, event.name);
            buf.writeBoolean(event.state);
            buf.writeBoolean(event.lastState != null);
            if (event.lastState != null) buf.writeBoolean(event.lastState);
            ByteBufUtils.writeUTF8String(buf, event.identifier);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            event = new ControlEvent(ByteBufUtils.readUTF8String(buf), buf.readBoolean(), buf.readBoolean() ? buf.readBoolean() : null, ByteBufUtils.readUTF8String(buf));
        }
    }

    public static class ControlEventPacketHandler implements IMessageHandler<ControlEventPacket, IMessage>
    {
        @Override
        public IMessage onMessage(ControlEventPacket packet, MessageContext ctx)
        {
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();

            server.addScheduledTask(() -> MinecraftForge.EVENT_BUS.post(packet.event.setPlayer(ctx.getServerHandler().player)));
            return null;
        }
    }


    public static class BasicPacketToServer implements IMessage
    {
        public int action;

        public BasicPacketToServer()
        {
            //Required
        }

        public BasicPacketToServer(int action)
        {
            this.action = action;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(action);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            action = buf.readInt();
        }
    }

    public static class BasicPacketToServerHandler implements IMessageHandler<BasicPacketToServer, IMessage>
    {
        @Override
        public IMessage onMessage(BasicPacketToServer packet, MessageContext ctx)
        {
            FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(() ->
            {
                currentActionPlayer = ctx.getServerHandler().player;
                serverActions.get(packet.action).run();
                currentActionPlayer = null;
            });
            return null;
        }
    }


    public static class BasicPacketToClient implements IMessage
    {
        public int action;

        public BasicPacketToClient()
        {
            //Required
        }

        public BasicPacketToClient(int action)
        {
            this.action = action;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(action);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            action = buf.readInt();
        }
    }

    public static class BasicPacketToClientHandler implements IMessageHandler<BasicPacketToClient, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(BasicPacketToClient packet, MessageContext ctx)
        {
            Minecraft.getMinecraft().addScheduledTask(() -> clientActions.get(packet.action).run());
            return null;
        }
    }
}
