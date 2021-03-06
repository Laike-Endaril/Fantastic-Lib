package com.fantasticsource.mctools;

import com.fantasticsource.mctools.animation.CBipedAnimation;
import com.fantasticsource.mctools.betterattributes.BetterAttribute;
import com.fantasticsource.mctools.component.CResourceLocation;
import com.fantasticsource.mctools.controlintercept.ControlEvent;
import com.fantasticsource.mctools.sound.SimpleSound;
import com.fantasticsource.tools.component.CUUID;
import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
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

import java.util.UUID;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class Network
{
    public static final SimpleNetworkWrapper WRAPPER = new SimpleNetworkWrapper(MODID);
    private static int discriminator = 0;

    public static void init()
    {
        WRAPPER.registerMessage(PlaySimpleSoundPacketHandler.class, PlaySimpleSoundPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(ControlEventPacketHandler.class, ControlEventPacket.class, discriminator++, Side.SERVER);
        WRAPPER.registerMessage(GenericComponentPacketHandler.class, GenericComponentPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RemoveEntityImmediatePacketHandler.class, RemoveEntityImmediatePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(BetterAttributePacketHandler.class, BetterAttributePacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(AddBipedAnimationsPacketHandler.class, AddBipedAnimationsPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(RemoveBipedAnimationPacketHandler.class, RemoveBipedAnimationPacket.class, discriminator++, Side.CLIENT);
        WRAPPER.registerMessage(UpdateBipedAnimationsPacketHandler.class, UpdateBipedAnimationsPacket.class, discriminator++, Side.CLIENT);
    }


    public static class PlaySimpleSoundPacket implements IMessage
    {
        public Integer followingID;
        public Float x, y, z;
        public CResourceLocation rl = new CResourceLocation();
        public int attenuationType;
        public float volume, pitch;
        public SoundCategory soundCategory;

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

        public PlaySimpleSoundPacket(ResourceLocation rl, Entity following, int attenuationType, float volume, float pitch)
        {
            this(rl, following, attenuationType, volume, pitch, SoundCategory.MASTER);
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, Entity following, int attenuationType, float volume, float pitch, SoundCategory soundCategory)
        {
            x = null;
            y = null;
            z = null;

            this.rl.set(rl);
            this.followingID = following == null ? null : following.getEntityId();
            this.attenuationType = attenuationType;
            this.volume = volume;
            this.pitch = pitch;
            this.soundCategory = soundCategory;
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z)
        {
            this(rl, x, y, z, 2, 1, 1);
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z, int attenuationType, float volume, float pitch)
        {
            this(rl, x, y, z, attenuationType, volume, pitch, SoundCategory.MASTER);
        }

        public PlaySimpleSoundPacket(ResourceLocation rl, float x, float y, float z, int attenuationType, float volume, float pitch, SoundCategory soundCategory)
        {
            this.followingID = null;

            this.rl.set(rl);
            this.x = x;
            this.y = y;
            this.z = z;
            this.attenuationType = attenuationType;
            this.volume = volume;
            this.pitch = pitch;
            this.soundCategory = soundCategory;
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
            ByteBufUtils.writeUTF8String(buf, soundCategory.getName());
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
            soundCategory = SoundCategory.getByName(ByteBufUtils.readUTF8String(buf));
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
                    Entity following = Minecraft.getMinecraft().world.getEntityByID(packet.followingID);
                    if (following != null) simpleSound = new SimpleSound(packet.rl.value, packet.soundCategory, following);
                }
                else if (packet.x != null) simpleSound = new SimpleSound(packet.rl.value, packet.soundCategory, packet.x, packet.y, packet.z);
                else simpleSound = new SimpleSound(packet.rl.value, packet.soundCategory);

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


    public static class RemoveEntityImmediatePacket implements IMessage
    {
        public int id;

        public RemoveEntityImmediatePacket()
        {
            //Required
        }

        public RemoveEntityImmediatePacket(Entity entity)
        {
            id = entity.getEntityId();
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(id);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            id = buf.readInt();
        }
    }

    public static class RemoveEntityImmediatePacketHandler implements IMessageHandler<RemoveEntityImmediatePacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(RemoveEntityImmediatePacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                if (mc.world == null) return;

                Entity entity = mc.world.getEntityByID(packet.id);
                if (entity == null) return;


                MCTools.removeEntityImmediate(entity);
                mc.world.removeAllEntities();
            });
            return null;
        }
    }


    public static class BetterAttributePacket implements IMessage
    {
        int entityID;
        String attributeName;
        double base, total, current;

        public BetterAttributePacket()
        {
            //Required
        }

        public BetterAttributePacket(Entity entity, BetterAttribute attribute)
        {
            entityID = entity.getEntityId();
            attributeName = attribute.name;
            base = attribute.getBaseAmount(entity);
            total = attribute.getTotalAmount(entity);
            current = attribute.getCurrentAmount(entity);
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(entityID);
            ByteBufUtils.writeUTF8String(buf, attributeName);
            buf.writeDouble(base);
            buf.writeDouble(total);
            buf.writeDouble(current);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            entityID = buf.readInt();
            attributeName = ByteBufUtils.readUTF8String(buf);
            base = buf.readDouble();
            total = buf.readDouble();
            current = buf.readDouble();
        }
    }

    public static class BetterAttributePacketHandler implements IMessageHandler<BetterAttributePacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(BetterAttributePacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                if (mc.world == null) return;

                Entity entity = mc.world.getEntityByID(packet.entityID);
                if (entity == null) return;

                BetterAttribute attribute = BetterAttribute.BETTER_ATTRIBUTES.get(packet.attributeName);
                if (attribute == null) return;

                NBTTagCompound compound = MCTools.getOrGenerateSubCompound(entity.getEntityData(), MODID);
                attribute.setBaseAmount(entity, packet.base);
                MCTools.getOrGenerateSubCompound(compound, "attributes").setDouble(attribute.name, packet.total);
                attribute.setCurrentAmount(entity, packet.current);
            });
            return null;
        }
    }


    public static class AddBipedAnimationsPacket implements IMessage
    {
        int entityID;
        CBipedAnimation[] animations;
        long serverSystemMillis;

        public AddBipedAnimationsPacket()
        {
            //Required
        }

        public AddBipedAnimationsPacket(Entity entity, CBipedAnimation... animations)
        {
            entityID = entity.getEntityId();
            this.animations = animations;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(entityID);
            buf.writeInt(animations.length);
            for (CBipedAnimation animation : animations) animation.write(buf);
            buf.writeLong(System.currentTimeMillis());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            entityID = buf.readInt();
            animations = new CBipedAnimation[buf.readInt()];
            for (int i = 0; i < animations.length; i++) animations[i] = new CBipedAnimation().read(buf);
            serverSystemMillis = buf.readLong();
        }
    }

    public static class AddBipedAnimationsPacketHandler implements IMessageHandler<AddBipedAnimationsPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(AddBipedAnimationsPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                if (mc.world == null) return;

                Entity entity = mc.world.getEntityByID(packet.entityID);
                if (entity == null) return;


                long offsetMillis = System.currentTimeMillis() - packet.serverSystemMillis;
                for (CBipedAnimation animation : packet.animations)
                {
                    animation.startTime += offsetMillis;
                    animation.setAllStartTimes(animation.startTime);

                    if (animation.pauseTime > -1) animation.pauseTime += offsetMillis;
                    for (CPath.CPathData data : animation.getAllData()) if (data.pauseTime > -1) data.pauseTime += offsetMillis;

                    CBipedAnimation.addAnimation(entity, animation);
                }
            });
            return null;
        }
    }


    public static class RemoveBipedAnimationPacket implements IMessage
    {
        int entityID;
        UUID animationID;

        public RemoveBipedAnimationPacket()
        {
            //Required
        }

        public RemoveBipedAnimationPacket(Entity entity, CBipedAnimation animation)
        {
            entityID = entity.getEntityId();
            animationID = animation.id;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(entityID);
            new CUUID().set(animationID).write(buf);
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            entityID = buf.readInt();
            animationID = new CUUID().read(buf).value;
        }
    }

    public static class RemoveBipedAnimationPacketHandler implements IMessageHandler<RemoveBipedAnimationPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(RemoveBipedAnimationPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                if (mc.world == null) return;

                Entity entity = mc.world.getEntityByID(packet.entityID);
                if (entity == null) return;


                CBipedAnimation.removeAnimations(entity, packet.animationID);
            });
            return null;
        }
    }


    public static class UpdateBipedAnimationsPacket implements IMessage
    {
        int entityID;
        CBipedAnimation[] animations;
        long serverSystemMillis;

        public UpdateBipedAnimationsPacket()
        {
            //Required
        }

        public UpdateBipedAnimationsPacket(Entity entity, CBipedAnimation... animations)
        {
            entityID = entity.getEntityId();
            this.animations = animations;
        }

        @Override
        public void toBytes(ByteBuf buf)
        {
            buf.writeInt(entityID);
            buf.writeInt(animations.length);
            for (CBipedAnimation animation : animations) animation.write(buf);
            buf.writeLong(System.currentTimeMillis());
        }

        @Override
        public void fromBytes(ByteBuf buf)
        {
            entityID = buf.readInt();
            animations = new CBipedAnimation[buf.readInt()];
            for (int i = 0; i < animations.length; i++) animations[i] = new CBipedAnimation().read(buf);
            serverSystemMillis = buf.readLong();
        }
    }

    public static class UpdateBipedAnimationsPacketHandler implements IMessageHandler<UpdateBipedAnimationsPacket, IMessage>
    {
        @Override
        @SideOnly(Side.CLIENT)
        public IMessage onMessage(UpdateBipedAnimationsPacket packet, MessageContext ctx)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.addScheduledTask(() ->
            {
                if (mc.world == null) return;

                Entity entity = mc.world.getEntityByID(packet.entityID);
                if (entity == null) return;


                long offsetMillis = System.currentTimeMillis() - packet.serverSystemMillis;
                for (CBipedAnimation animation : packet.animations)
                {
                    animation.startTime += offsetMillis;
                    animation.setAllStartTimes(animation.startTime);

                    if (animation.pauseTime > -1) animation.pauseTime += offsetMillis;
                    for (CPath.CPathData data : animation.getAllData()) if (data.pauseTime > -1) data.pauseTime += offsetMillis;

                    if (CBipedAnimation.removeAnimations(entity, animation.id)) CBipedAnimation.addAnimation(entity, animation);
                }
            });
            return null;
        }
    }
}
