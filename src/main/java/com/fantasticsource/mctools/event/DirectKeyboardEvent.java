package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Keyboard;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

@Cancelable
public class DirectKeyboardEvent extends Event
{
    private static final Field keyboardReadBufferField = ReflectionTool.getField(Keyboard.class, "readBuffer");
    private static ByteBuffer keyboardBuffer, buffer;

    static
    {
        try
        {
            keyboardBuffer = (ByteBuffer) keyboardReadBufferField.get(null);
            buffer = ByteBuffer.allocateDirect(keyboardBuffer.capacity());
            MinecraftForge.EVENT_BUS.register(DirectKeyboardEvent.class);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 702, true);
        }
    }


    @SubscribeEvent
    public static void interceptKeyboard(TickEvent.ClientTickEvent event)
    {
        int offset = keyboardBuffer.position();
        keyboardBuffer.mark();
        while (Keyboard.next())
        {
            if (!MinecraftForge.EVENT_BUS.post(new DirectKeyboardEvent()))
            {
                //If not cancelled, forward the event bytes to the new buffer, which will then be passed back to Keyboard for vanilla MC to find
                offset = keyboardBuffer.position() - offset;
                keyboardBuffer.reset();
                for (int i = 0; i < offset; i++) buffer.put(keyboardBuffer.get());
            }

            offset = keyboardBuffer.position();
            keyboardBuffer.mark();
        }

        keyboardBuffer.clear();
        buffer.flip();
        keyboardBuffer.put(buffer);
        keyboardBuffer.flip();
        buffer.clear();
    }
}
