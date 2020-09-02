package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.mctools.component.CResourceLocation;
import com.fantasticsource.mctools.controlintercept.ControlEvent;
import com.fantasticsource.mctools.sound.SimpleSound;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(FantasticLib.MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(PlaySimpleSoundPacketHandler.class, PlaySimpleSoundPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ControlEventPacketHandler.class, ControlEventPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(GenericComponentPacketHandler.class, GenericComponentPacket.class, discriminator++, Side.CLIENT);
    }


    public static class PlaySimpleSoundPacket implements IMessage
    {
        public Integer followingID;
        public Float x, y, z;
        public CResourceLocation rl = new CResourceLocation();
        public int attenuationType;
        public float volume, pitch;

        public PlaySimpleSoundPacket()
        {
            //Required
        }

        public PlaySimpleSoundPacket(ResourceLocation rl)
        {
            this(rl, null);
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, Entity following)
        {
            this(rl, following, 2, 1, 1);
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z)
        {
            this(rl, x, y, z, 2, 1, 1);
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, Entity following, int attenuationType, float volume, float pitch)
        {
            x = null;
            y = null;
            z = null;

            this.rl.set(rl);
            this.followingID = following.getEntityId();
            this.attenuationType = attenuationType;
            this.volume = volume;
            this.pitch = pitch;
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z, int attenuationType, float volume, float pitch)
        {
            this.followingID = null;

            this.rl.set(rl);
            this.x = x;
            this.y = y;
            this.z = z;
            this.attenuationType = attenuationType;
            this.volume = volume;
            this.pitch = pitch;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            rl.write(buf);

            buf.writeBoolean(followingID != null);
            if (followingID != null) buf.writeInt(followingID);

            buf.writeBoolean(x != null);
            if (x != null)
            {
                buf.writeFloat(x);
                buf.writeFloat(y);
                buf.writeFloat(z);
            }

            buf.writeInt(attenuationType);
            buf.writeFloat(volume);
            buf.writeFloat(pitch);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            rl.read(buf);

            if (buf.readBoolean()) followingID = buf.readInt();

            if (buf.readBoolean())
            {
                x = buf.readFloat();
                y = buf.readFloat();
                z = buf.readFloat();
            }

            attenuationType = buf.readInt();
            volume = buf.readFloat();
            pitch = buf.readFloat();
        }
    }

    public static class PlaySimpleSoundPacketHandler implements IMessageHandler<PlaySimpleSoundPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(PlaySimpleSoundPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                SimpleSound simpleSound = null;

                if (packet.followingID != null)
                {
                    for (WorldServer world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
                    {
                        Entity following = world.getEntityByID(packet.followingID);
                        if (following != null)
                        {
                            simpleSound = new SimpleSound(packet.rl.value, SoundCategory.MASTER, following);
                            break;
                        }
                    }
                }
                else if (packet.x != null) simpleSound = new SimpleSound(packet.rl.value, SoundCategory.MASTER, packet.x, packet.y, packet.z);
                else simpleSound = new SimpleSound(packet.rl.value, SoundCategory.MASTER);

                if (simpleSound != null)
                {
                    simpleSound.attenuationType = packet.attenuationType == 0 ? ISound.AttenuationType.NONE : ISound.AttenuationType.LINEAR;
                    simpleSound.volume = packet.volume;
                    simpleSound.pitch = packet.pitch;
                    mc.getSoundHandler().playSound(simpleSound);
                }
            });
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


    public static class GenericComponentPacket implements IMessage
    {
        public Component component;

        public GenericComponentPacket()
        {
            //Required
        }

        public GenericComponentPacket(Component component)
        {
            this.component = component;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            Component.writeMarked(buf, component);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            component = Component.readMarked(buf);
        }
    }

    public static class GenericComponentPacketHandler implements IMessageHandler<GenericComponentPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(GenericComponentPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() -> packet.component.onClientSync());
            return null;
        }
    }
}
