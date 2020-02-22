package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

public class WorldEventDistributor implements IWorldEventListener
{
    private static Field worldEventListenersField = ReflectionTool.getField(World.class, "field_73021_x", "eventListeners");

    private World world;

    private List<IWorldEventListener> normalListeners = new ArrayList<>();

    private WorldEventDistributor(World world, List<IWorldEventListener> originalListeners)
    {
        this.world = world;
        normalListeners.addAll(originalListeners);
        originalListeners.clear();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public static void worldLoad(WorldEvent.Load event)
    {
        try
        {
            World world = event.getWorld();
            List<IWorldEventListener> originalListeners = (List<IWorldEventListener>) worldEventListenersField.get(world);
            originalListeners.add(0, new WorldEventDistributor(world, originalListeners));
        }
        catch (IllegalAccessException e)
        {
            MCTools.crash(e, 703, true);
        }
    }


    @Override
    public void notifyBlockUpdate(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        DBlockUpdateEvent event = new DBlockUpdateEvent(world, pos, oldState, newState, flags);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.notifyBlockUpdate(event.getWorld(), event.pos, event.oldState, event.newState, event.flags);
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos)
    {
        DNotifyLightSetEvent event = new DNotifyLightSetEvent(world, pos);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.notifyLightSet(event.pos);
        }
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        DMarkBlockRangeForRenderUpdateEvent event = new DMarkBlockRangeForRenderUpdateEvent(world, x1, y1, z1, x2, y2, z2);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.markBlockRangeForRenderUpdate(event.x1, event.y1, event.z1, event.x2, event.y2, event.z2);
        }
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch)
    {
        DSoundEvent event = new DSoundEvent(world, player, soundEvent, soundCategory, x, y, z, volume, pitch);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.playSoundToAllNearExcept(event.player, event.soundEvent, event.soundCategory, event.x, event.y, event.z, event.volume, event.pitch);
        }
    }

    @Override
    public void playRecord(SoundEvent soundEvent, BlockPos pos)
    {
        DPlayRecordEvent event = new DPlayRecordEvent(world, soundEvent, pos);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.playRecord(event.soundEvent, event.pos);
        }
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        spawnParticle(particleID, ignoreRange, false, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnParticle(int id, boolean ignoreRange, boolean minimumParticles, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        DSpawnParticleEvent event = new DSpawnParticleEvent(world, id, ignoreRange, minimumParticles, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.spawnParticle(event.id, event.ignoreRange, event.minimumParticles, event.x, event.y, event.z, event.xSpeed, event.ySpeed, event.zSpeed, event.parameters);
        }
    }

    @Override
    public void onEntityAdded(Entity entity)
    {
        EVENT_BUS.post(new DEntityAddedEvent(world, entity));
        for (IWorldEventListener listener : normalListeners) listener.onEntityAdded(entity);
    }

    @Override
    public void onEntityRemoved(Entity entity)
    {
        EVENT_BUS.post(new DEntityRemovedEvent(world, entity));
        for (IWorldEventListener listener : normalListeners) listener.onEntityRemoved(entity);
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        DBroadcastSoundEvent event = new DBroadcastSoundEvent(world, soundID, pos, data);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.broadcastSound(event.soundID, event.pos, event.data);
        }
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos pos, int data)
    {
        DPlayEvent event = new DPlayEvent(world, player, type, pos, data);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.playEvent(event.player, event.type, event.pos, event.data);
        }
    }

    @Override
    public void sendBlockBreakProgress(int breakerID, BlockPos pos, int progress)
    {
        DSendBlockBreakProgressEvent event = new DSendBlockBreakProgressEvent(world, breakerID, pos, progress);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.sendBlockBreakProgress(event.breakerID, event.pos, event.progress);
        }
    }


    public static class DWorldEvent extends Event
    {
        private World world;

        public DWorldEvent(World world)
        {
            this.world = world;
        }

        public World getWorld()
        {
            return world;
        }
    }

    @Cancelable
    public static class DBlockUpdateEvent extends DWorldEvent
    {
        public BlockPos pos;
        public IBlockState oldState, newState;
        public int flags;

        private DBlockUpdateEvent(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
        {
            super(world);

            this.pos = pos;
            this.oldState = oldState;
            this.newState = newState;
            this.flags = flags;
        }
    }

    @Cancelable
    public static class DNotifyLightSetEvent extends DWorldEvent
    {
        public BlockPos pos;

        private DNotifyLightSetEvent(World world, BlockPos pos)
        {
            super(world);

            this.pos = pos;
        }
    }

    @Cancelable
    public static class DMarkBlockRangeForRenderUpdateEvent extends DWorldEvent
    {
        public int x1, y1, z1, x2, y2, z2;

        private DMarkBlockRangeForRenderUpdateEvent(World world, int x1, int y1, int z1, int x2, int y2, int z2)
        {
            super(world);

            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
    }

    @Cancelable
    public static class DSoundEvent extends DWorldEvent
    {
        public EntityPlayer player;
        public SoundEvent soundEvent;
        public SoundCategory soundCategory;
        public double x, y, z;
        public float volume, pitch;
        public Entity entity = null;

        private DSoundEvent(World world, @Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch)
        {
            super(world);

            this.player = player;
            this.soundEvent = soundEvent;
            this.soundCategory = soundCategory;
            this.x = x;
            this.y = y;
            this.z = z;
            this.volume = volume;
            this.pitch = pitch;

            for (Entity entity : world.loadedEntityList)
            {
                if (entity.posX == x && entity.posY == y && entity.posZ == z)
                {
                    this.entity = entity;
                    break;
                }
            }
        }
    }

    @Cancelable
    public static class DPlayRecordEvent extends DWorldEvent
    {
        public SoundEvent soundEvent;
        public BlockPos pos;

        private DPlayRecordEvent(World world, SoundEvent soundEvent, BlockPos pos)
        {
            super(world);

            this.soundEvent = soundEvent;
            this.pos = pos;
        }
    }

    @Cancelable
    public static class DSpawnParticleEvent extends DWorldEvent
    {
        public int id;
        public boolean ignoreRange, minimumParticles;
        public double x, y, z, xSpeed, ySpeed, zSpeed;
        public int[] parameters;

        private DSpawnParticleEvent(World world, int id, boolean ignoreRange, boolean minimumParticles, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
        {
            super(world);

            this.id = id;
            this.ignoreRange = ignoreRange;
            this.minimumParticles = minimumParticles;
            this.x = x;
            this.y = y;
            this.z = z;
            this.xSpeed = xSpeed;
            this.ySpeed = ySpeed;
            this.zSpeed = zSpeed;
            this.parameters = parameters;
        }
    }

    public static class DEntityAddedEvent extends DWorldEvent
    {
        private Entity entity;

        private DEntityAddedEvent(World world, Entity entity)
        {
            super(world);

            this.entity = entity;
        }

        public Entity getEntity()
        {
            return entity;
        }
    }

    public static class DEntityRemovedEvent extends DWorldEvent
    {
        private Entity entity;

        private DEntityRemovedEvent(World world, Entity entity)
        {
            super(world);

            this.entity = entity;
        }

        public Entity getEntity()
        {
            return entity;
        }
    }

    @Cancelable
    public static class DBroadcastSoundEvent extends DWorldEvent
    {
        public int soundID, data;
        public BlockPos pos;

        private DBroadcastSoundEvent(World world, int soundID, BlockPos pos, int data)
        {
            super(world);

            this.soundID = soundID;
            this.pos = pos;
            this.data = data;
        }
    }

    @Cancelable
    public static class DPlayEvent extends DWorldEvent
    {
        public EntityPlayer player;
        public int type, data;
        public BlockPos pos;

        private DPlayEvent(World world, EntityPlayer player, int type, BlockPos pos, int data)
        {
            super(world);

            this.player = player;
            this.type = type;
            this.pos = pos;
            this.data = data;
        }
    }

    @Cancelable
    public static class DSendBlockBreakProgressEvent extends DWorldEvent
    {
        public int breakerID, progress;
        public BlockPos pos;

        private DSendBlockBreakProgressEvent(World world, int breakerID, BlockPos pos, int progress)
        {
            super(world);

            this.breakerID = breakerID;
            this.pos = pos;
            this.progress = progress;
        }
    }
}
