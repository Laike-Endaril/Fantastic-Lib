package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.items.ItemMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;

import static com.fantasticsource.fantasticlib.FantasticLib.NAME;

public class InventoryChangedEvent extends EntityEvent
{
    public static HashSet<Class<? extends Entity>> watchedClasses = new HashSet<>();
    public static HashMap<Entity, GlobalInventoryData> previousContents = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(InventoryChangedEvent.class);
    }


    public final GlobalInventoryData oldInventory, newInventory;


    public InventoryChangedEvent(Entity entity, GlobalInventoryData oldInventory, GlobalInventoryData newInventory)
    {
        super(entity);
        this.oldInventory = oldInventory;
        this.newInventory = newInventory;
    }


    @SubscribeEvent
    public static void serverTick(TickEvent.ServerTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.START || watchedClasses.size() == 0) return;


        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        Profiler profiler = server.profiler;

        profiler.startSection(NAME + ": InventoryChangedEvent");
        HashMap<Entity, GlobalInventoryData> newContents = new LinkedHashMap<>();

        ArrayList<InventoryChangedEvent> events = new ArrayList<>();
        GlobalInventoryData oldInventory, newInventory;
        boolean found;
        for (WorldServer world : MCTools.DIMENSION_MANAGER_WORLDS.values())
        {
            for (Entity entity : world.loadedEntityList)
            {
                if (entity.isDead) continue;

                found = false;
                for (Class<? extends Entity> cls : watchedClasses)
                {
                    if (cls.isAssignableFrom(entity.getClass()))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found) continue;


                oldInventory = previousContents.get(entity);
                newInventory = new GlobalInventoryData(entity);

                if (oldInventory != null && oldInventory.equals(newInventory)) newContents.put(entity, oldInventory);
                else
                {
                    events.add(new InventoryChangedEvent(entity, oldInventory, newInventory));
                    newContents.put(entity, newInventory);
                }
            }
        }

        profiler.endStartSection("Fantastic Lib: InventoryChangedEvent listeners");
        for (InventoryChangedEvent event1 : events) MinecraftForge.EVENT_BUS.post(event1);

        profiler.endStartSection(NAME + ": InventoryChangedEvent map replacement");
        previousContents = newContents;

        profiler.endSection();
    }


    public static class GlobalInventoryData
    {
        public final ItemStack[] allNonSkin, tiamatInventory;

        public GlobalInventoryData(Entity entity)
        {
            this(GlobalInventory.getAllNonSkinItems(entity).toArray(new ItemStack[0]), GlobalInventory.getAllTiamatItems(entity).toArray(new ItemStack[0]));
        }

        public GlobalInventoryData(ItemStack[] allNonSkin, ItemStack[] tiamatInventory)
        {
            this.allNonSkin = allNonSkin;
            this.tiamatInventory = tiamatInventory;
        }

        public GlobalInventoryData deepCopy()
        {
            int size1 = allNonSkin.length, size2 = tiamatInventory.length;
            ItemStack[] newAllNonSkin = new ItemStack[size1], newTiamatInventory = new ItemStack[size2];

            System.arraycopy(allNonSkin, 0, newAllNonSkin, 0, size1);
            System.arraycopy(tiamatInventory, 0, newTiamatInventory, 0, size2);

            return new GlobalInventoryData(newAllNonSkin, newTiamatInventory);
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof GlobalInventoryData)) return false;

            GlobalInventoryData other = (GlobalInventoryData) obj;
            if (allNonSkin.length != other.allNonSkin.length) return false;

            return ItemMatcher.stacksMatch(allNonSkin, other.allNonSkin);
        }
    }
}
