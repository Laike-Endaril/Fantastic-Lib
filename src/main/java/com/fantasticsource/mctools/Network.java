package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.mctools.component.CResourceLocation;
import com.fantasticsource.mctools.sound.SimpleSound;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
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
}
