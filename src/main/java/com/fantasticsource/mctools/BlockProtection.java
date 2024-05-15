package com.fantasticsource.mctools;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

public class BlockProtection
{
    private static boolean initialized = false;

    private static void init()
    {
        if (!initialized)
        {
            initialized = true;
            MinecraftForge.EVENT_BUS.register(BlockProtection.class);
        }
    }

    static
    {
        //Automatically initialize main class when anyone references the main class or the event in it
        init();
    }


    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        //This prevents the player from making progress on breaking a block instead of preventing the breaking itself; looks and acts much cleaner this way
        //It also catches instant breaking, at least in the case of punching flowers and whatnot.  Must simply use a high break speed by default
        Entity entity = event.getEntity();
        if (isBuildProtected(entity.world, event.getPos(), entity, event))
        {
            event.setNewSpeed(0);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockLeftClick(PlayerInteractEvent.LeftClickBlock event)
    {
        //This prevents the player from using an item.  Might not catch everything but when it works, it works nicely (it doesn't cause inventory desync)
        //This does not detect buckets!  Buckets are handled in the FillBucketEvent (which should be named UseBucketEvent)
        Vec3d vec = event.getHitVec();
        if (vec == null || isBuildProtected(event.getWorld(), new BlockPos(event.getHitVec()), event.getEntity(), event))
        {
            event.setUseItem(Event.Result.DENY);
            event.setUseBlock(Event.Result.DENY);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void bucketUse(FillBucketEvent event)
    {
        //Prevents bucket usage in the protected zones
        //This event is not just for filling buckets; it is also for emptying them
        RayTraceResult rayTrace = event.getTarget();
        if (rayTrace == null || isBuildProtected(event.getWorld(), new BlockPos(rayTrace.hitVec), event.getEntity(), event))
        {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockRightClick(PlayerInteractEvent.RightClickBlock event)
    {
        //This prevents the player from using an item.  Might not catch everything but when it works, it works nicely (it doesn't cause inventory desync)
        //This does not detect buckets!  Buckets are handled in the FillBucketEvent (which should be named UseBucketEvent)
        Vec3d vec = event.getHitVec();
        if (vec == null || isBuildProtected(event.getWorld(), new BlockPos(event.getHitVec()), event.getEntity(), event))
        {
            event.setUseItem(Event.Result.DENY);
            event.setUseBlock(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockPlace(BlockEvent.PlaceEvent event)
    {
        //Might catch some things RightClickBlock doesn't catch, but not as nice for when blocks are placed from inventory, because it causes inventory desync
        if (isBuildProtected(event.getWorld(), event.getPos(), event.getPlayer(), event))
        {
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void blockMultiPlace(BlockEvent.MultiPlaceEvent event)
    {
        //I imagine this is similar to PlaceEvent, but for when multiple blocks are placed at once
        for (BlockSnapshot snapshot : event.getReplacedBlockSnapshots())
        {
            if (isBuildProtected(snapshot.getWorld(), snapshot.getPos(), event.getPlayer(), event))
            {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
                break;
            }
        }
    }


    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onDetonation(ExplosionEvent.Detonate event)
    {
        World world = event.getWorld();
        List<BlockPos> list = event.getAffectedBlocks();
        for (BlockPos pos : list.toArray(new BlockPos[list.size()]))
        {
            if (isBuildProtected(world, pos, null, event)) list.remove(pos);
        }
    }


    public static boolean isBuildProtected(World world, BlockPos pos, Entity entity, Event originalEvent)
    {
        if (entity instanceof EntityPlayer && ((EntityPlayer) entity).isCreative()) return false;

        return MinecraftForge.EVENT_BUS.post(new AlterBlockEvent(world, pos, entity, originalEvent));
    }


    @Cancelable
    public static class AlterBlockEvent extends Event
    {
        static
        {
            //Automatically initialize main class when anyone references the main class or the event in it
            init();
        }

        public final World world;
        public final BlockPos blockPos;
        public final Entity entityAltering;
        public final Event originalEvent;

        public AlterBlockEvent(World world, BlockPos blockPos, @Nullable Entity entityAltering, Event originalEvent)
        {
            this.world = world;
            this.blockPos = blockPos;
            this.entityAltering = entityAltering;
            this.originalEvent = originalEvent;
        }
    }
}
