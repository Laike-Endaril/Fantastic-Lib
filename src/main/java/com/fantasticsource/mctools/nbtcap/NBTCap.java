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

        nbtCap.setCompound(oldNBTCap.getCompound());
    }


    @SubscribeEvent()
    public static void attachToEntity(AttachCapabilitiesEvent<Entity> event)
    {
        Entity entity = event.getObject();
        for (Predicate<Entity> predicate : entityPredicates)
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
        for (Predicate<ItemStack> predicate : stackPredicates)
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
        for (Predicate<World> predicate : worldPredicates)
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
        for (Predicate<TileEntity> predicate : tePredicates)
        {
            if (predicate.test(te))
            {
                event.addCapability(new ResourceLocation(MODID, "nbtcap"), new NBTCapProvider());
                return;
            }
        }
    }


    protected NBTTagCompound compound = new NBTTagCompound();

    @Override
    public NBTTagCompound getCompound()
    {
        return compound;
    }

    @Override
    public void setCompound(NBTTagCompound compound)
    {
        this.compound = compound;
    }
}
