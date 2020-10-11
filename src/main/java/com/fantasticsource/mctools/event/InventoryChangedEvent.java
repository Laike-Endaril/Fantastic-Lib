package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.items.ItemMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class InventoryChangedEvent extends EntityEvent
{
    public static LinkedHashMap<Entity, GlobalInventoryData> previousContents = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(InventoryChangedEvent.class);
    }


    public final GlobalInventoryData oldInventory, newInventory;
    public final HashMap<Integer, ItemStack> newTiamatItems = new HashMap<>();


    public InventoryChangedEvent(Entity entity, GlobalInventoryData oldInventory, GlobalInventoryData newInventory)
    {
        super(entity);
        this.oldInventory = oldInventory;
        this.newInventory = newInventory;

        if (newInventory.tiamatInventory != null)
        {
            ArrayList<ItemStack> n = newInventory.tiamatInventory;
            if (oldInventory == null)
            {
                int i = 0;
                for (ItemStack stack : newInventory.tiamatInventory)
                {
                    newTiamatItems.put(i++, stack);
                }
            }
            else
            {
                ItemStack stack;
                ArrayList<ItemStack> o = oldInventory.tiamatInventory;
                int size = newInventory.tiamatInventory.size();
                for (int i = 0; i < size; i++)
                {
                    stack = n.get(i);
                    if (!ItemMatcher.stacksMatch(stack, o.get(i))) newTiamatItems.put(i, stack);
                }
            }
        }
    }


    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.END) return;


        previousContents.entrySet().removeIf(entry ->
        {
            Entity entity = entry.getKey();
            if (!entity.isAddedToWorld()) return true;

            for (World world : FMLCommonHandler.instance().getMinecraftServerInstance().worlds)
            {
                if (world == event.world) return false;
            }

            return true;
        });

        GlobalInventoryData oldInventory, newInventory;
        for (Entity entity : event.world.loadedEntityList.toArray(new Entity[0]))
        {
            if (!entity.isAddedToWorld())
            {
                previousContents.remove(entity);
                continue;
            }


            newInventory = new GlobalInventoryData(entity);
            oldInventory = previousContents.get(entity);
            if (oldInventory == null || !oldInventory.equals(newInventory))
            {
                MinecraftForge.EVENT_BUS.post(new InventoryChangedEvent(entity, oldInventory, newInventory));
                previousContents.put(entity, newInventory);
            }
        }
    }


    public static class GlobalInventoryData
    {
        public final ArrayList<ItemStack> allNonSkin, tiamatInventory;
        public final LinkedHashMap<String, ArrayList<ItemStack>> allCategorized;

        public GlobalInventoryData(Entity entity)
        {
            this(GlobalInventory.getAllNonSkinItems(entity), GlobalInventory.getAllTiamatItems(entity), GlobalInventory.getAllItemsCategorized(entity));
        }

        public GlobalInventoryData(ArrayList<ItemStack> allNonSkin, ArrayList<ItemStack> tiamatInventory, LinkedHashMap<String, ArrayList<ItemStack>> allCategorized)
        {
            this.allNonSkin = allNonSkin;
            this.tiamatInventory = tiamatInventory;
            this.allCategorized = allCategorized;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (!(obj instanceof GlobalInventoryData)) return false;

            GlobalInventoryData other = (GlobalInventoryData) obj;
            int size = allNonSkin.size();
            if (size != other.allNonSkin.size()) return false;

            for (int i = 0; i < size; i++)
            {
                if (!ItemMatcher.stacksMatch(allNonSkin.get(i), other.allNonSkin.get(i))) return false;
            }

            return true;
        }
    }
}
