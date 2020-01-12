package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.input.Mouse;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

@Cancelable
public class DirectMouseEvent extends Event
{
    private static final Field mouseReadBufferField = ReflectionTool.getField(Mouse.class, "readBuffer");
    private static ByteBuffer mouseBuffer, buffer;

    static
    {
        try
        {
            mouseBuffer = (ByteBuffer) mouseReadBufferField.get(null);
            buffer = ByteBuffer.allocateDirect(mouseBuffer.capacity());
            MinecraftForge.EVENT_BUS.register(DirectMouseEvent.class);
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 702, true);
        }
    }


    @SubscribeEvent
    public static void interceptMouse(TickEvent.ClientTickEvent event)
    {
        int offset = mouseBuffer.position();
        mouseBuffer.mark();
        while (Mouse.next())
        {
            if (!MinecraftForge.EVENT_BUS.post(new DirectMouseEvent()))
            {
                //If not cancelled, forward the event bytes to the new buffer, which will then be passed back to Mouse for vanilla MC to find
                offset = mouseBuffer.position() - offset;
                mouseBuffer.reset();
                for (int i = 0; i < offset; i++) buffer.put(mouseBuffer.get());
            }

            offset = mouseBuffer.position();
            mouseBuffer.mark();
        }

        mouseBuffer.clear();
        buffer.flip();
        mouseBuffer.put(buffer);
        mouseBuffer.flip();
        buffer.clear();
    }
}
