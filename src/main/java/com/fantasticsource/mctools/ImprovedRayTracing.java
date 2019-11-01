package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.tools.Tools;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ImprovedRayTracing
{
    private static final int MAX_ITERATIONS = 200;
    private static long lastWarning = -1;


    /**
     * @return Double.NaN if this ray would not collide with the entity, regardless of terrain.  The distance this ray penetrates through the given entity, if it does.  The distance from the edge of the entity to where this ray collided with a block, as a negative value, in all other cases
     */
    public static double entityPenetration(Entity fromEyesOf, double maxDistance, Entity target, boolean collideOnAllSolids)
    {
        if (fromEyesOf.world != target.world) return Double.NaN;

        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return entityPenetration(target, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), collideOnAllSolids);
    }

    /**
     * @return Double.NaN if this ray would not collide with the entity, regardless of terrain.  The distance this ray penetrates through the given entity, if it does.  The distance from the edge of the entity to where this ray collided with a block, as a negative value, in all other cases
     */
    public static double entityPenetration(Entity target, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids)
    {
        RayTraceResult entityEnd = rayTraceEntity(target, vecEnd, vecStart);
        if (entityEnd == null || entityEnd.typeOfHit == RayTraceResult.Type.MISS) return Double.NaN;


        RayTraceResult entityStart = rayTraceEntity(target, vecStart, vecEnd);
        RayTraceResult blockEnd = rayTraceBlocks(target.world, vecStart, entityEnd.hitVec, collideOnAllSolids);

        if (blockEnd.typeOfHit == RayTraceResult.Type.MISS)
        {
            return entityStart.hitVec.distanceTo(entityEnd.hitVec);
        }

        return vecStart.distanceTo(blockEnd.hitVec) - vecStart.distanceTo(entityStart.hitVec);
    }


    public static RayTraceResult rayTraceEntity(Entity fromEyesOf, double maxDistance, Entity target)
    {
        if (fromEyesOf.world != target.world) return null;

        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return rayTraceEntity(target, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)));
    }

    public static RayTraceResult rayTraceEntity(Entity target, Vec3d vecStart, Vec3d vecEnd)
    {
        return target.getEntityBoundingBox().calculateIntercept(vecStart, vecEnd);
    }


    public static boolean isUnobstructed(Entity fromEyesOf, double maxDistance, boolean collideOnAllSolids)
    {
        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return isUnobstructed(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), collideOnAllSolids);
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, collideOnAllSolids).hitVec == null;
    }


    @Nonnull
    public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, boolean collideOnAllSolids)
    {
        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return rayTraceBlocks(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), collideOnAllSolids);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, boolean collideOnAllSolids)
    {
        world.profiler.startSection("Fantastic Lib: Improved Raytrace");


        RayTraceResult result;
        BlockPos pos = new BlockPos(vecStart), endPos = new BlockPos(vecEnd);


        //Check starting block
        if (!world.isBlockLoaded(pos))
        {
            world.profiler.endSection();
            return new RayTraceResult(RayTraceResult.Type.MISS, null, null, pos);
        }
        IBlockState state = world.getBlockState(pos);
        if ((collideOnAllSolids || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
        {
            result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
            if (result != null)
            {
                world.profiler.endSection();
                return result;
            }
        }

        //Iterate through all non-starting blocks and check them
        double xStart = vecStart.x, yStart = vecStart.y, zStart = vecStart.z;
        double xRange = vecEnd.x - xStart, yRange = vecEnd.y - yStart, zRange = vecEnd.z - zStart;

        int xDir = xRange > 0 ? 1 : xRange < 0 ? -1 : 0;
        int yDir = yRange > 0 ? 1 : yRange < 0 ? -1 : 0;
        int zDir = zRange > 0 ? 1 : zRange < 0 ? -1 : 0;

        double xInverseRange = xDir == 0 ? Double.NaN : 1 / xRange;
        double yInverseRange = yDir == 0 ? Double.NaN : 1 / yRange;
        double zInverseRange = zDir == 0 ? Double.NaN : 1 / zRange;

        int nextXStop = xDir == 0 ? Integer.MAX_VALUE : pos.getX();
        if (xDir == 1) nextXStop++;
        int nextYStop = yDir == 0 ? Integer.MAX_VALUE : pos.getY();
        if (yDir == 1) nextYStop++;
        int nextZStop = zDir == 0 ? Integer.MAX_VALUE : pos.getZ();
        if (zDir == 1) nextZStop++;

        double xDistToStop, yDistToStop, zDistToStop;
        double normalizedXDistToStop = 7777777, normalizedYDistToStop = 7777777, normalizedZDistToStop;
        int mininumNormalizedDistance; //0 == none, 1 == x, 2 == y, 3 == z
        for (int i = 1; i <= MAX_ITERATIONS; i++)
        {
            //Find which direction to travel in next
            mininumNormalizedDistance = 0;
            if (xDir != 0)
            {
                xDistToStop = nextXStop - xStart;
                normalizedXDistToStop = xDistToStop * xInverseRange;
                mininumNormalizedDistance = 1;
            }
            if (yDir != 0)
            {
                yDistToStop = nextYStop - yStart;
                normalizedYDistToStop = yDistToStop * yInverseRange;
                if (normalizedYDistToStop < normalizedXDistToStop) mininumNormalizedDistance = 2;
            }
            if (zDir != 0)
            {
                zDistToStop = nextZStop - zStart;
                normalizedZDistToStop = zDistToStop * zInverseRange;
                if (normalizedZDistToStop < normalizedXDistToStop && normalizedZDistToStop < normalizedYDistToStop) mininumNormalizedDistance = 3;
            }


            //Travel to next position, and set new value for the next stopping point in the travelled direction
            if (mininumNormalizedDistance == 1) //X
            {
                pos = pos.east(xDir);
                nextXStop += xDir;
            }
            else if (mininumNormalizedDistance == 2) //Y
            {
                pos = pos.up(yDir);
                nextYStop += yDir;
            }
            else //Z
            {
                pos = pos.south(zDir);
                nextZStop += zDir;
            }


            //Check the BlockPos
            if (!world.isBlockLoaded(pos))
            {
                world.profiler.endSection();
                return new RayTraceResult(RayTraceResult.Type.MISS, null, null, pos);
            }
            state = world.getBlockState(pos);
            if ((collideOnAllSolids || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
            {
                result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
                if (result != null)
                {
                    world.profiler.endSection();
                    return result;
                }
            }


            //End if this was the last block
            if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ())
            {
                world.profiler.endSection();
                return new RayTraceResult(RayTraceResult.Type.MISS, null, null, pos);
            }
        }


        //Max iterations reached; force end and warn
        if (lastWarning == -1 || System.currentTimeMillis() - lastWarning > 1000 * 5)
        {
            System.err.println("WARNING: ENDLESS RAYTRACING DETECTED!  This warning will not show more than once every 5 seconds, but may be happening far more often");
            Tools.printStackTrace();
            lastWarning = System.currentTimeMillis();
        }
        world.profiler.endSection();
        return new RayTraceResult(RayTraceResult.Type.MISS, null, null, pos);
    }

    public static boolean canSeeThrough(IBlockState blockState)
    {
        Material material = blockState.getMaterial();

        if (material == Material.LEAVES) return true;
        if (material == Material.GLASS) return true;
        if (material == Material.ICE) return true;

        //These don't usually matter due to the ignoreBlockWithoutBoundingBox thing, but here they are anyway, just in case
        if (material == Material.AIR) return true;
        if (material == Material.WATER) return true;
        if (material == Material.FIRE) return true;
        if (material == Material.PORTAL) return !Compat.betterportals;
        if (material == Material.BARRIER) return true;
        if (material == Material.PLANTS) return true;
        if (material == Material.WEB) return true;
        if (material == Material.VINE) return true;


        Block block = blockState.getBlock();

        //Special blocks types that don't follow the rules
        if (block instanceof BlockSlime) return true;
        if (block instanceof BlockTrapDoor) return true;
        if (block instanceof BlockFence) return true;
        if (block instanceof BlockFenceGate) return true;

        //Special blocks that don't follow the rules
        if (block == Blocks.ACACIA_DOOR) return true;
        if (block == Blocks.JUNGLE_DOOR) return true;
        if (block == Blocks.IRON_BARS) return true;

        //Honeybadger blocks :/
        if (block == Blocks.OAK_DOOR || block == Blocks.IRON_DOOR)
        {
            if ((block.getMetaFromState(blockState) & 8) != 0) return true;
        }

        return false;
    }
}
