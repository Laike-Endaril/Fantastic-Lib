package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.util.ArrayList;

public class LWJGLControlEvent extends Event
{
    protected static Field keyboardReadBufferField, mouseReadBufferField;
    protected static ByteBuffer keyboardBuffer, mouseBuffer, tempKeyboardBuffer, tempMouseBuffer;

    static
    {
        try
        {
            keyboardReadBufferField = ReflectionTool.getField(org.lwjgl.input.Keyboard.class, "readBuffer");
            mouseReadBufferField = ReflectionTool.getField(org.lwjgl.input.Mouse.class, "readBuffer");

            keyboardBuffer = (ByteBuffer) keyboardReadBufferField.get(null);
            mouseBuffer = (ByteBuffer) mouseReadBufferField.get(null);

            tempKeyboardBuffer = ByteBuffer.allocateDirect(keyboardBuffer.capacity());
            tempMouseBuffer = ByteBuffer.allocateDirect(mouseBuffer.capacity());

            MinecraftForge.EVENT_BUS.register(LWJGLControlEvent.class);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 702, true);
        }
    }


    protected byte[] lwjglBytes;
    protected boolean cancelOriginal = false;
    protected ArrayList<String> serverPackets = new ArrayList<>();
    protected String identifier = "";


    protected LWJGLControlEvent(byte[] lwjglBytes)
    {
        this.lwjglBytes = lwjglBytes;
    }

    protected LWJGLControlEvent(Network.LWJGLEventPacket packet)
    {
        lwjglBytes = packet.lwjglBytes;
        identifier = packet.identifier;
    }

    @SubscribeEvent
    public static void interceptControls(TickEvent.ClientTickEvent tickEvent)
    {
        //Keyboard
        int offset = keyboardBuffer.position();
        keyboardBuffer.mark();
        while (org.lwjgl.input.Keyboard.next())
        {
            offset = keyboardBuffer.position() - offset;
            keyboardBuffer.reset();
            byte[] lwjglBytes = new byte[offset];
            keyboardBuffer.get(lwjglBytes);

            LWJGLControlEvent event = new LWJGLControlEvent.Keyboard(lwjglBytes);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.cancelOriginal)
            {
                //If not cancelled, forward the event bytes to the new buffer, which will then be passed back to Keyboard for vanilla MC to find
                tempKeyboardBuffer.put(lwjglBytes);
            }
            for (String identifier : event.serverPackets)
            {
                Network.WRAPPER.sendToServer(new Network.LWJGLEventPacket(event, identifier));
            }

            offset = keyboardBuffer.position();
            keyboardBuffer.mark();
        }

        keyboardBuffer.clear();
        tempKeyboardBuffer.flip();
        keyboardBuffer.put(tempKeyboardBuffer);
        keyboardBuffer.flip();
        tempKeyboardBuffer.clear();


        //Mouse
        offset = mouseBuffer.position();
        mouseBuffer.mark();
        while (org.lwjgl.input.Mouse.next())
        {
            offset = mouseBuffer.position() - offset;
            mouseBuffer.reset();
            byte[] lwjglBytes = new byte[offset];
            mouseBuffer.get(lwjglBytes);

            LWJGLControlEvent event = new LWJGLControlEvent.Mouse(lwjglBytes);
            MinecraftForge.EVENT_BUS.post(event);
            if (!event.cancelOriginal)
            {
                //If not cancelled, forward the event bytes to the new buffer, which will then be passed back to Mouse for vanilla MC to find
                tempMouseBuffer.put(lwjglBytes);
            }
            for (String identifier : event.serverPackets)
            {
                Network.WRAPPER.sendToServer(new Network.LWJGLEventPacket(event, identifier));
            }

            offset = mouseBuffer.position();
            mouseBuffer.mark();
        }

        mouseBuffer.clear();
        tempMouseBuffer.flip();
        mouseBuffer.put(tempMouseBuffer);
        mouseBuffer.flip();
        tempMouseBuffer.clear();
    }

    public void sendToServer(String identifier)
    {
        if (identifier == null || identifier.equals("")) throw new IllegalArgumentException("Identifier cannot be null or empty!");
        serverPackets.add(identifier);
    }

    public void cancelOriginal()
    {
        cancelOriginal = true;
    }

    public byte[] getLWJGLBytes()
    {
        return lwjglBytes.clone();
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public static class Mouse extends LWJGLControlEvent
    {
        protected Mouse(byte[] lwjglBytes)
        {
            super(lwjglBytes);
        }

        public Mouse(Network.LWJGLEventPacket packet)
        {
            super(packet);
        }
    }

    public static class Keyboard extends LWJGLControlEvent
    {
        protected Keyboard(byte[] lwjglBytes)
        {
            super(lwjglBytes);
        }

        public Keyboard(Network.LWJGLEventPacket packet)
        {
            super(packet);
        }
    }
}
