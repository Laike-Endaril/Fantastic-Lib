package com.fantasticsource.mctools.event;

import com.fantasticsource.mctools.items.ItemMatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class LivingBaseInventoryChangedEvent extends LivingEvent
{
    public static LinkedHashMap<Entity, ArrayList<ItemStack>> previousContents = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(LivingBaseInventoryChangedEvent.class);
    }


    public ArrayList<ItemStack> oldInventory, newInventory;


    public LivingBaseInventoryChangedEvent(EntityLivingBase entity, ArrayList<ItemStack> oldInventory, ArrayList<ItemStack> newInventory)
    {
        super(entity);

        this.oldInventory = oldInventory;
        this.newInventory = newInventory;
    }


    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        if (event.side == Side.CLIENT || event.phase == TickEvent.Phase.END) return;


        ArrayList<ItemStack> oldInventory, newInventory;
        ItemStack oldStack, newStack;
        for (Entity entity : event.world.loadedEntityList)
        {
            if (!(entity instanceof EntityLivingBase)) continue;

            EntityLivingBase livingBase = (EntityLivingBase) entity;
            if (!livingBase.isEntityAlive())
            {
                previousContents.remove(livingBase);
                continue;
            }


            newInventory = new ArrayList<>();
            if (livingBase instanceof EntityPlayerMP)
            {
                InventoryPlayer inventory = ((EntityPlayerMP) livingBase).inventory;
                for (int i = 0; i < inventory.getSizeInventory(); i++)
                {
                    newInventory.add(inventory.getStackInSlot(i));
                }
            }
            else
            {
                //Not a player; get items held in hands and equipped to vanilla armor slots
                for (ItemStack stack : livingBase.getEquipmentAndArmor()) newInventory.add(stack);
            }


            oldInventory = previousContents.getOrDefault(livingBase, new ArrayList<>());
            if (newInventory.size() != oldInventory.size())
            {
                MinecraftForge.EVENT_BUS.post(new LivingBaseInventoryChangedEvent((EntityLivingBase) entity, oldInventory, newInventory));
                continue;
            }

            boolean match = true;
            for (int i = 0; i < newInventory.size(); i++)
            {
                if (!ItemMatcher.stacksMatch(oldInventory.get(i), newInventory.get(i)))
                {
                    match = false;
                    break;
                }
            }
            if (!match)
            {
                MinecraftForge.EVENT_BUS.post(new LivingBaseInventoryChangedEvent((EntityLivingBase) entity, oldInventory, newInventory));
            }
        }
    }

    @SubscribeEvent
    public static void serverStop(FMLServerStoppedEvent event)
    {
        previousContents.clear();
    }
}
