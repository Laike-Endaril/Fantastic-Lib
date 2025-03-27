package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.GlobalInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemStackChangedEvent extends Event
{
    static
    {
        MinecraftForge.EVENT_BUS.register(ItemStackChangedEvent.class);
    }

    public ItemStack stack;

    public ItemStackChangedEvent(ItemStack stack)
    {
        this.stack = stack;
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void itemStackConstruction(AttachCapabilitiesEvent<ItemStack> event)
    {
        MinecraftForge.EVENT_BUS.post(new ItemStackChangedEvent(event.getObject()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        for (ItemStack stack : GlobalInventory.getAllItems(event.getEntity()))
        {
            MinecraftForge.EVENT_BUS.post(new ItemStackChangedEvent(stack));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void openedContainer(PlayerContainerEvent.Open event)
    {
        for (Slot slot : event.getContainer().inventorySlots)
        {
            MinecraftForge.EVENT_BUS.post(new ItemStackChangedEvent(slot.getStack()));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void itemJoinWorld(EntityJoinWorldEvent event)
    {
        Entity entity = event.getEntity();
        if (!(entity instanceof EntityItem)) return;

        MinecraftForge.EVENT_BUS.post(new ItemStackChangedEvent(((EntityItem) entity).getItem()));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void itemPickup(EntityItemPickupEvent event)
    {
        MinecraftForge.EVENT_BUS.post(new ItemStackChangedEvent(event.getItem().getItem()));
    }
}
