package com.fantasticsource.mctools.event;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.mctools.items.ItemMatcher;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class LivingBaseInventoryChangedEvent extends LivingEvent
{
    public static Class tiamatPlayerInventoryClass = Compat.tiamatrpg ? ReflectionTool.getClassByName("com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory") : null;
    public static Field tiamatServerInventoriesField = tiamatPlayerInventoryClass == null ? null : ReflectionTool.getField(tiamatPlayerInventoryClass, "tiamatServerInventories");
    public static LinkedHashMap<UUID, IInventory> tiamatServerInventories = null;

    static
    {
        try
        {
            tiamatServerInventories = (LinkedHashMap<UUID, IInventory>) tiamatServerInventoriesField.get(null);
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }


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
                EntityPlayerMP player = (EntityPlayerMP) livingBase;
                InventoryPlayer inventory = player.inventory;
                for (int i = 0; i < inventory.getSizeInventory(); i++)
                {
                    newInventory.add(inventory.getStackInSlot(i));
                }

                if (Compat.baubles)
                {
                    IBaublesItemHandler baublesInventory = BaublesApi.getBaublesHandler(player);
                    for (int i = 0; i < baublesInventory.getSlots(); i++)
                    {
                        newInventory.add(baublesInventory.getStackInSlot(i));
                    }
                }

                if (tiamatServerInventories != null)
                {
                    IInventory tiamatInventory = tiamatServerInventories.get(player);
                    for (int i = 0; i < tiamatInventory.getSizeInventory(); i++)
                    {
                        newInventory.add(tiamatInventory.getStackInSlot(i));
                    }
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
