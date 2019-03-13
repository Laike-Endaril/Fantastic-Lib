package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IWorldEventListener;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

public class WorldEventDistributor implements IWorldEventListener
{
    private static Field worldEventListenersField;

    static
    {
        try
        {
            worldEventListenersField = ReflectionTool.getField(World.class, "field_73021_x", "eventListeners");
        }
        catch (NoSuchFieldException | IllegalAccessException e)
        {
            MCTools.crash(e, 702, true);
        }
    }

    private World world;

    private List<IWorldEventListener> normalListeners = new ArrayList<>();

    private WorldEventDistributor(World world, List<IWorldEventListener> originalListeners)
    {
        this.world = world;
        normalListeners.addAll(originalListeners);
        originalListeners.clear();
    }

    @SubscribeEvent
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
    public void notifyBlockUpdate(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
    {
        if (!EVENT_BUS.post(new DBlockUpdateEvent(worldIn, pos, oldState, newState, flags)))
        {
            for (IWorldEventListener listener : normalListeners) listener.notifyBlockUpdate(worldIn, pos, oldState, newState, flags);
        }
    }

    @Override
    public void notifyLightSet(BlockPos pos)
    {
        for (IWorldEventListener listener : normalListeners) listener.notifyLightSet(pos);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2)
    {
        for (IWorldEventListener listener : normalListeners) listener.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
    }

    @Override
    public void playSoundToAllNearExcept(@Nullable EntityPlayer player, SoundEvent soundIn, SoundCategory category, double x, double y, double z, float volume, float pitch)
    {
        if (!EVENT_BUS.post(new DSoundEvent(player, soundIn, category, x, y, z, volume, pitch)))
        {
            for (IWorldEventListener listener : normalListeners) listener.playSoundToAllNearExcept(player, soundIn, category, x, y, z, volume, pitch);
        }
    }

    @Override
    public void playRecord(SoundEvent soundIn, BlockPos pos)
    {
        for (IWorldEventListener listener : normalListeners) listener.playRecord(soundIn, pos);
    }

    @Override
    public void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        for (IWorldEventListener listener : normalListeners) listener.spawnParticle(particleID, ignoreRange, xCoord, yCoord, zCoord, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void spawnParticle(int id, boolean ignoreRange, boolean minimumParticles, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, int... parameters)
    {
        for (IWorldEventListener listener : normalListeners) listener.spawnParticle(id, ignoreRange, minimumParticles, x, y, z, xSpeed, ySpeed, zSpeed, parameters);
    }

    @Override
    public void onEntityAdded(Entity entityIn)
    {
        for (IWorldEventListener listener : normalListeners) listener.onEntityAdded(entityIn);
    }

    @Override
    public void onEntityRemoved(Entity entityIn)
    {
        for (IWorldEventListener listener : normalListeners) listener.onEntityRemoved(entityIn);
    }

    @Override
    public void broadcastSound(int soundID, BlockPos pos, int data)
    {
        for (IWorldEventListener listener : normalListeners) listener.broadcastSound(soundID, pos, data);
    }

    @Override
    public void playEvent(EntityPlayer player, int type, BlockPos blockPosIn, int data)
    {
        for (IWorldEventListener listener : normalListeners) listener.playEvent(player, type, blockPosIn, data);
    }

    @Override
    public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress)
    {
        for (IWorldEventListener listener : normalListeners) listener.sendBlockBreakProgress(breakerId, pos, progress);
    }


    @Cancelable
    public class DBlockUpdateEvent extends Event
    {
        private DBlockUpdateEvent(World worldIn, BlockPos pos, IBlockState oldState, IBlockState newState, int flags)
        {

        }
    }

    @Cancelable
    public class DSoundEvent extends Event
    {
        private EntityPlayer player;
        private SoundEvent soundEvent;
        private SoundCategory soundCategory;
        private Vec3d position;
        private float volume, pitch;
        private Entity entity;

        private DSoundEvent(@Nullable EntityPlayer player, SoundEvent soundEvent, SoundCategory soundCategory, double x, double y, double z, float volume, float pitch)
        {
            this.player = player;
            this.soundEvent = soundEvent;
            this.soundCategory = soundCategory;
            position = new Vec3d(x, y, z);
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

        public Entity getEntity()
        {
            return entity;
        }

        public EntityPlayer getPlayer()
        {
            return player;
        }

        public SoundEvent getSoundEvent()
        {
            return soundEvent;
        }

        public SoundCategory getSoundCategory()
        {
            return soundCategory;
        }

        public Vec3d getPosition()
        {
            return position;
        }

        public float getVolume()
        {
            return volume;
        }

        public float getPitch()
        {
            return pitch;
        }
    }
}
