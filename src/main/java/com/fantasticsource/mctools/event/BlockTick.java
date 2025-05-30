package com.fantasticsource.mctools.event;

import com.fantasticsource.tools.Tools;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.Predicate;

public class BlockTick
{
    protected static ArrayList<Predicate<BlockTickData>> actions = new ArrayList<>();

    public static void addAction(Predicate<BlockTickData> action)
    {
        actions.add(action);
        if (actions.size() == 1) MinecraftForge.EVENT_BUS.register(BlockTick.class);
    }

    public static void removeAction(Predicate<BlockTickData> action)
    {
        actions.remove(action);
        if (actions.size() == 0) MinecraftForge.EVENT_BUS.unregister(BlockTick.class);
    }


    @SubscribeEvent
    public static void worldTick(TickEvent.WorldTickEvent event)
    {
        if (event.side != Side.SERVER || event.phase != TickEvent.Phase.END) return;


        WorldServer world = (WorldServer) event.world;
        if (world.getWorldInfo().getTerrainType() == WorldType.DEBUG_ALL_BLOCK_STATES) return;


        int i = world.getGameRules().getInt("randomTickSpeed");
        if (i <= 0) return;


        world.profiler.startSection("FLib BlockTick");
        int r, x, y, z;
        Chunk chunk;
        for (Iterator<Chunk> iterator = world.getPersistentChunkIterable(world.getPlayerChunkMap().getChunkIterator()); iterator.hasNext(); )
        {
            chunk = iterator.next();
            for (ExtendedBlockStorage extendedblockstorage : chunk.getBlockStorageArray())
            {
                if (extendedblockstorage != Chunk.NULL_BLOCK_STORAGE && extendedblockstorage.needsRandomTick())
                {
                    for (int i1 = 0; i1 < i; ++i1)
                    {
                        r = Tools.random(Integer.MAX_VALUE);
                        x = r & 15;
                        z = r >> 8 & 15;
                        y = r >> 16 & 15;
                        BlockTickData blockTickData = new BlockTickData(world, x + (chunk.x << 4), y + extendedblockstorage.getYLocation(), z + (chunk.z << 4), extendedblockstorage.get(x, y, z));
                        for (Predicate<BlockTickData> action : actions) action.test(blockTickData);
                    }
                }
            }
        }
        world.profiler.endSection();
    }


    public static class BlockTickData
    {
        public final World world;
        public final int x, y, z;
        public final IBlockState blockState;

        public BlockTickData(World world, int x, int y, int z, IBlockState blockState)
        {
            this.world = world;
            this.x = x;
            this.y = y;
            this.z = z;
            this.blockState = blockState;
        }
    }
}
