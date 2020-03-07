package com.fantasticsource.mctools.nbtcap;

import com.fantasticsource.api.FLibAPI;
import com.fantasticsource.api.INBTCap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedHashMap;
import java.util.function.Predicate;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class NBTCap implements INBTCap
{
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        EntityPlayer player = event.getEntityPlayer();
        INBTCap nbtCap = player.getCapability(FLibAPI.NBT_CAP, null);
        INBTCap oldNBTCap = event.getOriginal().getCapability(FLibAPI.NBT_CAP, null);
        if (nbtCap == null || oldNBTCap == null) return;

        for (String modid : oldNBTCap.getRegisteredModIDs())
        {
            nbtCap.setCompound(modid, oldNBTCap.getCompound(modid));
        }
    }


    @SubscribeEvent()
    public static void attachToEntity(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();
        for (Predicate<Entity> predicate : entityPredicates.values())
        {
            if (predicate.test(entity))
            {
                event.addCapability(new ResourceLocation(MODID, "nbtcap"), new NBTCapProvider());
                return;
            }
        }
    }

    @SubscribeEvent()
    public static void attachToStack(AttachCapabilitiesEvent<ItemStack> event)
    {
        ItemStack stack = event.getObject();
        for (Predicate<ItemStack> predicate : stackPredicates.values())
        {
            if (predicate.test(stack))
            {
                event.addCapability(new ResourceLocation(MODID, "nbtcap"), new NBTCapProvider());
                return;
            }
        }
    }

    @SubscribeEvent()
    public static void attachToWorld(AttachCapabilitiesEvent<World> event)
    {
        World world = event.getObject();
        for (Predicate<World> predicate : worldPredicates.values())
        {
            if (predicate.test(world))
            {
                event.addCapability(new ResourceLocation(MODID, "nbtcap"), new NBTCapProvider());
                return;
            }
        }
    }

    @SubscribeEvent()
    public static void attachToTE(AttachCapabilitiesEvent<TileEntity> event)
    {
        TileEntity te = event.getObject();
        for (Predicate<TileEntity> predicate : tePredicates.values())
        {
            if (predicate.test(te))
            {
                event.addCapability(new ResourceLocation(MODID, "nbtcap"), new NBTCapProvider());
                return;
            }
        }
    }


    protected LinkedHashMap<String, NBTTagCompound> compounds = new LinkedHashMap<>();

    @Override
    public String[] getRegisteredModIDs()
    {
        return registeredModIDs.toArray(new String[0]);
    }

    @Override
    public NBTTagCompound getCompound(String modid)
    {
        return compounds.computeIfAbsent(modid, o -> new NBTTagCompound());
    }

    @Override
    public void setCompound(String modid, NBTTagCompound compound)
    {
        compounds.put(modid, compound);
    }
}
