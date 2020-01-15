package com.fantasticsource.mctools.controlintercept;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class ControlEvent extends Event
{
    protected static Map<String, KeyBinding> keybinds;
    protected static Map<KeyBinding, Boolean> keybindStates = new LinkedHashMap<>(), rawKeybindStates = new LinkedHashMap<>();

    static
    {
        try
        {
            if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
            {
                //Physical client
                keybinds = (Map<String, KeyBinding>) ReflectionTool.getField(KeyBinding.class, "field_74516_a", "KEYBIND_ARRAY").get(null);
                updateStatesAndReportChanged();
                MinecraftForge.EVENT_BUS.register(ControlEvent.class);
            }
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 702, true);
        }
    }

    public String name;
    public KeyBinding binding = null;
    public boolean state;
    public Boolean lastState;
    public String identifier = "";
    public EntityPlayerMP player = null;

    protected ArrayList<String> serverQueue = new ArrayList<>();
    protected boolean cancelOriginal = false;

    protected ControlEvent(String name, KeyBinding binding, boolean state, Boolean lastState)
    {
        this.name = name;
        this.binding = binding;
        this.state = state;
        this.lastState = lastState;
    }

    public ControlEvent(String name, boolean state, Boolean lastState, String identifier)
    {
        this.name = name;
        this.state = state;
        this.lastState = lastState;
        this.identifier = identifier;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void interceptControls(InputEvent inputEvent)
    {
        for (ControlEvent event : updateStatesAndReportChanged())
        {
            MinecraftForge.EVENT_BUS.post(event);

            if (event.cancelOriginal) setKeyState(event.binding, event.lastState);
            else keybindStates.put(event.binding, event.state);

            for (String identifier : event.serverQueue)
            {
                event.identifier = identifier;
                Network.WRAPPER.sendToServer(new Network.ControlEventPacket(event));
            }
        }
    }

    protected static ArrayList<ControlEvent> updateStatesAndReportChanged()
    {
        ArrayList<ControlEvent> result = new ArrayList<>();

        boolean state, lastState, lastRawState;
        KeyBinding binding;
        for (Map.Entry<String, KeyBinding> entry : keybinds.entrySet())
        {
            binding = entry.getValue();
            state = binding.isKeyDown();
            lastState = keybindStates.computeIfAbsent(binding, o -> false);
            lastRawState = rawKeybindStates.computeIfAbsent(binding, o -> false);
            if (state != lastState)
            {
                if (state != lastRawState) result.add(new ControlEvent(entry.getKey(), binding, state, lastState));
                else setKeyState(binding, lastState);
            }
            rawKeybindStates.put(binding, state);
        }

        return result;
    }

    protected static void setKeyState(KeyBinding binding, boolean state)
    {
        if (binding.isKeyDown() == state) return;


        KeyBinding.setKeyBindState(binding.getKeyCode(), state);

        if (state) KeyBinding.onTick(binding.getKeyCode());
        else
        {
            while (binding.isPressed())
            {
            }
        }
    }

    public void cancelOriginal()
    {
        cancelOriginal = true;
    }

    public void sendToServer(String identifier)
    {
        if (identifier == null || identifier.equals("")) throw new IllegalArgumentException("Identifier cannot be null or empty!");
        serverQueue.add(identifier);
    }

    public ControlEvent setPlayer(EntityPlayerMP player)
    {
        this.player = player;
        return this;
    }
}
