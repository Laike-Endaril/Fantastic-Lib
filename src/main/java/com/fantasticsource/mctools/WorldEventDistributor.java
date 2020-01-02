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
            for (IWorldEventListener listener : normalListeners) listener.notifyBlockUpdate(event.world, event.pos, event.oldState, event.newState, event.flags);
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos)
    {
        DNotifyLightSetEvent event = new DNotifyLightSetEvent(pos);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.notifyLightSet(event.pos);
        }
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        DMarkBlockRangeForRenderUpdateEvent event = new DMarkBlockRangeForRenderUpdateEvent(x1, y1, z1, x2, y2, z2);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.markBlockRangeForRenderUpdate(event.x1, event.y1, event.z1, event.x2, event.y2, event.z2);
        }
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch)
    {
        DSoundEvent event = new DSoundEvent(player, soundEvent, soundCategory, x, y, z, volume, pitch);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.playSoundToAllNearExcept(event.player, event.soundEvent, event.soundCategory, event.x, event.y, event.z, event.volume, event.pitch);
        }
    }

    @Override
    public void playRecord(SoundEvent soundEvent, BlockPos pos)
    {
        DPlayRecordEvent event = new DPlayRecordEvent(soundEvent, pos);
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
        DSpawnParticleEvent event = new DSpawnParticleEvent(id, ignoreRange, minimumParticles, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.spawnParticle(event.id, event.ignoreRange, event.minimumParticles, event.x, event.y, event.z, event.xSpeed, event.ySpeed, event.zSpeed, event.parameters);
        }
    }

    @Override
    public void onEntityAdded(Entity entity)
    {
        EVENT_BUS.post(new DEntityAddedEvent(entity));
        for (IWorldEventListener listener : normalListeners) listener.onEntityAdded(entity);
    }

    @Override
    public void onEntityRemoved(Entity entity)
    {
        EVENT_BUS.post(new DEntityRemovedEvent(entity));
        for (IWorldEventListener listener : normalListeners) listener.onEntityRemoved(entity);
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        DBroadcastSoundEvent event = new DBroadcastSoundEvent(soundID, pos, data);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.broadcastSound(event.soundID, event.pos, event.data);
        }
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos pos, int data)
    {
        DPlayEvent event = new DPlayEvent(player, type, pos, data);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.playEvent(event.player, event.type, event.pos, event.data);
        }
    }

    @Override
    public void sendBlockBreakProgress(int breakerID, BlockPos pos, int progress)
    {
        DSendBlockBreakProgressEvent event = new DSendBlockBreakProgressEvent(breakerID, pos, progress);
        if (!EVENT_BUS.post(event))
        {
            for (IWorldEventListener listener : normalListeners) listener.sendBlockBreakProgress(event.breakerID, event.pos, event.progress);
        }
    }


    @Cancelable
    public class DBlockUpdateEvent extends Event
    {
        public World world;
        public BlockPos pos;
        public IBlockState oldState, newState;
        public int flags;

        private DBlockUpdateEvent(World world, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
        {
            this.world = world;
            this.pos = pos;
            this.oldState = oldState;
            this.newState = newState;
            this.flags = flags;
        }
    }

    @Cancelable
    public class DNotifyLightSetEvent extends Event
    {
        public BlockPos pos;

        private DNotifyLightSetEvent(BlockPos pos)
        {
            this.pos = pos;
        }
    }

    @Cancelable
    public class DMarkBlockRangeForRenderUpdateEvent extends Event
    {
        public int x1, y1, z1, x2, y2, z2;

        private DMarkBlockRangeForRenderUpdateEvent(int x1, int y1, int z1, int x2, int y2, int z2)
        {
            this.x1 = x1;
            this.y1 = y1;
            this.z1 = z1;
            this.x2 = x2;
            this.y2 = y2;
            this.z2 = z2;
        }
    }

    @Cancelable
    public class DSoundEvent extends Event
    {
        public EntityPlayer player;
        public SoundEvent soundEvent;
        public SoundCategory soundCategory;
        public double x, y, z;
        public float volume, pitch;
        public Entity entity = null;

        private DSoundEvent(@Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch)
        {
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
    public class DPlayRecordEvent extends Event
    {
        public SoundEvent soundEvent;
        public BlockPos pos;

        private DPlayRecordEvent(SoundEvent soundEvent, BlockPos pos)
        {
            this.soundEvent = soundEvent;
            this.pos = pos;
        }
    }

    @Cancelable
    public class DSpawnParticleEvent extends Event
    {
        public int id;
        public boolean ignoreRange, minimumParticles;
        public double x, y, z, xSpeed, ySpeed, zSpeed;
        public int[] parameters;

        private DSpawnParticleEvent(int id, boolean ignoreRange, boolean minimumParticles, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
        {
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

    public class DEntityAddedEvent extends Event
    {
        private Entity entity;

        private DEntityAddedEvent(Entity entity)
        {
            this.entity = entity;
        }

        public Entity getEntity()
        {
            return entity;
        }
    }

    public class DEntityRemovedEvent extends Event
    {
        private Entity entity;

        private DEntityRemovedEvent(Entity entity)
        {
            this.entity = entity;
        }

        public Entity getEntity()
        {
            return entity;
        }
    }

    @Cancelable
    public class DBroadcastSoundEvent extends Event
    {
        public int soundID, data;
        public BlockPos pos;

        private DBroadcastSoundEvent(int soundID, BlockPos pos, int data)
        {
            this.soundID = soundID;
            this.pos = pos;
            this.data = data;
        }
    }

    @Cancelable
    public class DPlayEvent extends Event
    {
        public EntityPlayer player;
        public int type, data;
        public BlockPos pos;

        private DPlayEvent(EntityPlayer player, int type, BlockPos pos, int data)
        {
            this.player = player;
            this.type = type;
            this.pos = pos;
            this.data = data;
        }
    }

    @Cancelable
    public class DSendBlockBreakProgressEvent extends Event
    {
        public int breakerID, progress;
        public BlockPos pos;

        private DSendBlockBreakProgressEvent(int breakerID, BlockPos pos, int progress)
        {
            this.breakerID = breakerID;
            this.pos = pos;
            this.progress = progress;
        }
    }
}
