package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.function.Predicate;

public class ImprovedRayTracing
{
    public static final ArrayList<Predicate<IBlockState>> BLOCK_STATE_ENDS_RAYTRACE_FILTERS = new ArrayList<>();

    protected static HashSet<IBlockState> transparentBlockstates = new HashSet<>(), nonTransparentBlockstates = new HashSet<>();
    protected static HashSet<Block> transparentBlocks = new HashSet<>(), nonTransparentBlocks = new HashSet<>();
    protected static HashSet<Class<? extends Block>> transparentBlockSuperclasses = new HashSet<>(), nonTransparentBlockSuperclasses = new HashSet<>(),
            transparentBlockClasses = new HashSet<>(), nonTransparentBlockClasses = new HashSet<>(), ignoredBlockClasses = new HashSet<>();
    protected static HashSet<Material> transparentMaterials = new HashSet<>(), nonTransparentMaterials = new HashSet<>();

    protected static final int ITERATION_WARNING_THRESHOLD = 200;
    protected static long lastWarning = -1;
    protected static int errorCount = 0;


    public static void reloadConfigs()
    {
        transparentBlockstates.clear();
        nonTransparentBlockstates.clear();

        transparentBlocks.clear();
        nonTransparentBlocks.clear();

        transparentBlockSuperclasses.clear();
        nonTransparentBlockSuperclasses.clear();
        transparentBlockClasses.clear();
        nonTransparentBlockClasses.clear();
        ignoredBlockClasses.clear();

        transparentMaterials.clear();
        nonTransparentMaterials.clear();

        for (String s : FantasticConfig.raytraceSettings.rayBlockstateFilter)
        {
            if (s.trim().equals("")) continue;

            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter: " + s);
                continue;
            }

            String[] tokens2 = Tools.fixedSplit(tokens[0].trim(), ":");
            if (tokens2.length != 3)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter: " + s);
                continue;
            }

            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tokens2[0].trim(), tokens2[1].trim()));
            if (block == null)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter; block not found: " + s);
                continue;
            }

            int meta;
            try
            {
                meta = Integer.parseInt(tokens2[2].trim());
            }
            catch (NumberFormatException e)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter: " + s);
                continue;
            }

            IBlockState blockState = block.getStateFromMeta(meta);
            if (block.getMetaFromState(blockState) != meta)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace blockstate filter; state not found for meta: " + s);
                continue;
            }

            if (Boolean.parseBoolean(tokens[1].trim())) transparentBlockstates.add(blockState);
            else nonTransparentBlockstates.add(blockState);
        }

        for (String s : FantasticConfig.raytraceSettings.rayBlockFilter)
        {
            if (s.trim().equals("")) continue;

            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace block filter: " + s);
                continue;
            }

            String[] tokens2 = Tools.fixedSplit(tokens[0].trim(), ":");
            if (tokens2.length != 2)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace block filter: " + s);
                continue;
            }

            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tokens2[0].trim(), tokens2[1].trim()));
            if (block == null)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace block filter; block not found: " + s);
                continue;
            }

            if (Boolean.parseBoolean(tokens[1].trim())) transparentBlocks.add(block);
            else nonTransparentBlocks.add(block);
        }

        for (String s : FantasticConfig.raytraceSettings.rayBlockSuperclassFilter)
        {
            if (s.trim().equals("")) continue;

            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace block superclass filter: " + s);
                continue;
            }

            Class<? extends Block> cls = ReflectionTool.getClassByName(tokens[0].trim());
            if (cls == null)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace block superclass filter; class not found: " + s);
                continue;
            }

            if (Boolean.parseBoolean(tokens[1].trim())) transparentBlockSuperclasses.add(cls);
            else nonTransparentBlockSuperclasses.add(cls);
        }

        for (String s : FantasticConfig.raytraceSettings.rayMaterialFilter)
        {
            if (s.trim().equals("")) continue;

            String[] tokens = Tools.fixedSplit(s, ",");
            if (tokens.length != 2)
            {
                System.err.println(TextFormatting.RED + "Invalid raytrace material filter: " + s);
                continue;
            }

            Material material;
            switch (tokens[0].trim().toLowerCase())
            {
                case "air":
                    material = Material.AIR;
                    break;

                case "grass":
                    material = Material.GRASS;
                    break;

                case "ground":
                    material = Material.GROUND;
                    break;

                case "wood":
                    material = Material.WOOD;
                    break;

                case "rock":
                    material = Material.ROCK;
                    break;

                case "iron":
                    material = Material.IRON;
                    break;

                case "anvil":
                    material = Material.ANVIL;
                    break;

                case "water":
                    material = Material.WATER;
                    break;

                case "lava":
                    material = Material.LAVA;
                    break;

                case "leaves":
                    material = Material.LEAVES;
                    break;

                case "plants":
                    material = Material.PLANTS;
                    break;

                case "vine":
                    material = Material.VINE;
                    break;

                case "sponge":
                    material = Material.SPONGE;
                    break;

                case "cloth":
                    material = Material.CLOTH;
                    break;

                case "fire":
                    material = Material.FIRE;
                    break;

                case "sand":
                    material = Material.SAND;
                    break;

                case "circuits":
                    material = Material.CIRCUITS;
                    break;

                case "carpet":
                    material = Material.CARPET;
                    break;

                case "glass":
                    material = Material.GLASS;
                    break;

                case "redstone_light":
                    material = Material.REDSTONE_LIGHT;
                    break;

                case "tnt":
                    material = Material.TNT;
                    break;

                case "coral":
                    material = Material.CORAL;
                    break;

                case "ice":
                    material = Material.ICE;
                    break;

                case "packed_ice":
                    material = Material.PACKED_ICE;
                    break;

                case "snow":
                    material = Material.SNOW;
                    break;

                case "crafted_snow":
                    material = Material.CRAFTED_SNOW;
                    break;

                case "cactus":
                    material = Material.CACTUS;
                    break;

                case "clay":
                    material = Material.CLAY;
                    break;

                case "gourd":
                    material = Material.GOURD;
                    break;

                case "dragon_egg":
                    material = Material.DRAGON_EGG;
                    break;

                case "portal":
                    material = Material.PORTAL;
                    break;

                case "cake":
                    material = Material.CAKE;
                    break;

                case "web":
                    material = Material.WEB;
                    break;

                case "piston":
                    material = Material.PISTON;
                    break;

                case "barrier":
                    material = Material.BARRIER;
                    break;

                case "structure_void":
                    material = Material.STRUCTURE_VOID;
                    break;

                default:
                    System.err.println(TextFormatting.RED + "Invalid raytrace material filter; material not found: " + s);
                    continue;
            }

            if (Boolean.parseBoolean(tokens[1].trim())) transparentMaterials.add(material);
            else nonTransparentMaterials.add(material);
        }
    }


    /**
     * @return Double.NaN if this ray would not collide with the entity, regardless of terrain.  The distance this ray penetrates through the given entity, if it does.  The distance from the edge of the entity to where this ray collided with a block, as a negative value, in all other cases
     */
    public static double entityPenetration(Entity fromEyesOf, double maxDistance, Entity target, boolean isPhysicsCheck)
    {
        if (fromEyesOf.world != target.world) return Double.NaN;

        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return entityPenetration(target, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), isPhysicsCheck);
    }

    /**
     * @return Double.NaN if this ray would not collide with the entity, regardless of terrain.  The distance this ray penetrates through the given entity, if it does.  The distance from the edge of the entity to where this ray collided with a block, as a negative value, in all other cases
     */
    public static double entityPenetration(Entity target, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean isPhysicsCheck)
    {
        return entityPenetration(target, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), isPhysicsCheck);
    }

    /**
     * @return Double.NaN if this ray would not collide with the entity, regardless of terrain.  The distance this ray penetrates through the given entity, if it does.  The distance from the edge of the entity to where this ray collided with a block (including an unloaded block), as a negative value, in all other cases
     */
    public static double entityPenetration(Entity target, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck)
    {
        RayTraceResult entityEnd = rayTraceEntity(target, vecEnd, vecStart);
        if (entityEnd == null || entityEnd.typeOfHit == RayTraceResult.Type.MISS) return Double.NaN;


        RayTraceResult entityStart = rayTraceEntity(target, vecStart, vecEnd);
        RayTraceResult blockHit = rayTraceBlocks(target.world, vecStart, entityEnd.hitVec, isPhysicsCheck);

        if (blockHit.typeOfHit == RayTraceResult.Type.MISS)
        {
            return entityStart.hitVec.distanceTo(entityEnd.hitVec);
        }

        if (blockHit.hitVec == null) return -vecStart.distanceTo(entityStart.hitVec);
        return vecStart.distanceTo(blockHit.hitVec) - vecStart.distanceTo(entityStart.hitVec);
    }


    public static RayTraceResult rayTraceEntity(Entity fromEyesOf, double maxDistance, Entity target)
    {
        if (fromEyesOf.world != target.world) return null;

        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return rayTraceEntity(target, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)));
    }

    public static RayTraceResult rayTraceEntity(Entity target, Vec3d vecStart, Vec3d vecEnd, double maxDistance)
    {
        return rayTraceEntity(target, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)));
    }

    public static RayTraceResult rayTraceEntity(Entity target, Vec3d vecStart, Vec3d vecEnd)
    {
        return target.getEntityBoundingBox().calculateIntercept(vecStart, vecEnd);
    }


    @Nonnull
    public static BlockPos[] blocksInRay(Entity fromEyesOf, double maxDistance, boolean isPhysicsCheck)
    {
        return blocksInRay(fromEyesOf, maxDistance, ITERATION_WARNING_THRESHOLD, isPhysicsCheck);
    }

    @Nonnull
    public static BlockPos[] blocksInRay(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean isPhysicsCheck)
    {
        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return blocksInRay(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), maxBlocks, isPhysicsCheck);
    }

    @Nonnull
    public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean isPhysicsCheck)
    {
        return blocksInRay(world, vecStart, vecEnd, maxDistance, ITERATION_WARNING_THRESHOLD, isPhysicsCheck);
    }

    @Nonnull
    public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, int maxBlocks, boolean isPhysicsCheck)
    {
        return blocksInRay(world, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), maxBlocks, isPhysicsCheck);
    }

    @Nonnull
    public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck)
    {
        return blocksInRay(world, vecStart, vecEnd, ITERATION_WARNING_THRESHOLD, isPhysicsCheck);
    }

    @Nonnull
    public static BlockPos[] blocksInRay(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean isPhysicsCheck)
    {
        world.profiler.startSection("Fantastic Lib: Blocks In Ray");


        RayTraceResult result;
        ArrayList<BlockPos> blocks = new ArrayList<>();
        BlockPos pos = new BlockPos(vecStart), endPos = new BlockPos(vecEnd);

        //Special cases for ending blockpos
        if (vecEnd.x > vecStart.x && vecEnd.x == (int) vecEnd.x) endPos = new BlockPos(endPos.getX() - 1, endPos.getY(), endPos.getZ());
        if (vecEnd.y > vecStart.y && vecEnd.y == (int) vecEnd.y) endPos = new BlockPos(endPos.getX(), endPos.getY() - 1, endPos.getZ());
        if (vecEnd.z > vecStart.z && vecEnd.z == (int) vecEnd.z) endPos = new BlockPos(endPos.getX(), endPos.getY(), endPos.getZ() - 1);


        //Check starting block
        if (!world.isBlockLoaded(pos))
        {
            world.profiler.endSection();
            return new BlockPos[0];
        }
        IBlockState state = world.getBlockState(pos);
        if ((isPhysicsCheck || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
        {
            result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
            if (result != null)
            {
                world.profiler.endSection();
                return new BlockPos[]{pos};
            }
        }

        //Add starting block to results
        blocks.add(pos);

        //End if this was the last block
        if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ())
        {
            world.profiler.endSection();
            return blocks.toArray(new BlockPos[0]);
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
        for (int i = 1; i <= maxBlocks; i++)
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


            //Add block to results
            blocks.add(pos);


            //Check the BlockPos
            if (!world.isBlockLoaded(pos))
            {
                world.profiler.endSection();
                return blocks.toArray(new BlockPos[0]);
            }
            state = world.getBlockState(pos);
            if ((isPhysicsCheck || !canSeeThrough(state)) && state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB)
            {
                result = state.collisionRayTrace(world, pos, vecStart, vecEnd);
                if (result != null)
                {
                    world.profiler.endSection();
                    return blocks.toArray(new BlockPos[0]);
                }
            }


            //End if this was the last block
            if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ())
            {
                world.profiler.endSection();
                return blocks.toArray(new BlockPos[0]);
            }
        }


        //Max iterations reached; force end and warn
        if (maxBlocks >= ITERATION_WARNING_THRESHOLD && (lastWarning == -1 || System.currentTimeMillis() - lastWarning > 1000 * 60 * 5))
        {
            System.err.println("WARNING: BEYOND-LIMIT RAYTRACING DETECTED!  This warning will not show more than once every 5 minutes.  This is usually due to inefficient raytrace calls from another mod");
            System.err.println("This type of error has occurred " + errorCount + " additional times since the last time this message was shown");
            System.err.println("From " + vecStart + " to " + vecEnd + " (distance: " + vecStart.distanceTo(vecEnd) + ")");
            System.err.println("Limit: " + maxBlocks + " iterations (not synonymous to distance, but longer distances are generally more iterations)");
            System.err.println();
            Tools.printStackTrace();
            lastWarning = System.currentTimeMillis();
        }
        else errorCount++;


        world.profiler.endSection();
        return blocks.toArray(new BlockPos[0]);
    }


    public static boolean isUnobstructed(Entity fromEyesOf, double maxDistance, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, isPhysicsCheck).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(Entity fromEyesOf, double maxDistance, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, isPhysicsCheck, alsoCheckFluids).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, maxBlocks, isPhysicsCheck).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, maxBlocks, isPhysicsCheck, alsoCheckFluids).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, maxDistance, isPhysicsCheck).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, int maxBlocks, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, maxDistance, maxBlocks, isPhysicsCheck).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, isPhysicsCheck).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, maxBlocks, isPhysicsCheck).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, isPhysicsCheck, alsoCheckFluids).typeOfHit == RayTraceResult.Type.MISS;
    }

    public static boolean isUnobstructed(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, maxBlocks, isPhysicsCheck, alsoCheckFluids).typeOfHit == RayTraceResult.Type.MISS;
    }


    @Nonnull
    public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, ITERATION_WARNING_THRESHOLD, isPhysicsCheck);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, ITERATION_WARNING_THRESHOLD, isPhysicsCheck, alsoCheckFluids);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(fromEyesOf, maxDistance, maxBlocks, isPhysicsCheck, false);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(Entity fromEyesOf, double maxDistance, int maxBlocks, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        Vec3d eyes = fromEyesOf.getPositionVector().addVector(0, fromEyesOf.getEyeHeight(), 0);
        return rayTraceBlocks(fromEyesOf.world, eyes, eyes.add(fromEyesOf.getLookVec().scale(maxDistance)), maxBlocks, isPhysicsCheck, alsoCheckFluids);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, maxDistance, ITERATION_WARNING_THRESHOLD, isPhysicsCheck);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, double maxDistance, int maxBlocks, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecStart.add(vecEnd.subtract(vecStart).normalize().scale(maxDistance)), maxBlocks, isPhysicsCheck);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, ITERATION_WARNING_THRESHOLD, isPhysicsCheck);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean isPhysicsCheck)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, maxBlocks, isPhysicsCheck, false);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        return rayTraceBlocks(world, vecStart, vecEnd, ITERATION_WARNING_THRESHOLD, isPhysicsCheck, alsoCheckFluids);
    }

    @Nonnull
    public static RayTraceResult rayTraceBlocks(World world, Vec3d vecStart, Vec3d vecEnd, int maxBlocks, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        world.profiler.startSection("Fantastic Lib: Improved Raytrace");


        RayTraceResult result;
        BlockPos pos = new BlockPos(vecStart), endPos = new BlockPos(vecEnd);

        //Special cases for ending blockpos
        if (vecEnd.x > vecStart.x && vecEnd.x == (int) vecEnd.x) endPos = new BlockPos(endPos.getX() - 1, endPos.getY(), endPos.getZ());
        if (vecEnd.y > vecStart.y && vecEnd.y == (int) vecEnd.y) endPos = new BlockPos(endPos.getX(), endPos.getY() - 1, endPos.getZ());
        if (vecEnd.z > vecStart.z && vecEnd.z == (int) vecEnd.z) endPos = new BlockPos(endPos.getX(), endPos.getY(), endPos.getZ() - 1);


        //Check starting block
        if (!world.isBlockLoaded(pos))
        {
            world.profiler.endSection();
            return new FixedRayTraceResult(null, null, null, pos);
        }
        IBlockState state = world.getBlockState(pos);
        result = checkState(world, pos, state, vecStart, vecEnd, isPhysicsCheck, alsoCheckFluids);
        if (result != null)
        {
            world.profiler.endSection();
            return result;
        }


        //End if this was the last block
        if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ())
        {
            world.profiler.endSection();
            return new FixedRayTraceResult(RayTraceResult.Type.MISS, vecEnd, null, pos);
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
        for (int i = 1; i <= maxBlocks; i++)
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
                return new FixedRayTraceResult(null, null, null, pos);
            }
            state = world.getBlockState(pos);
            result = checkState(world, pos, state, vecStart, vecEnd, isPhysicsCheck, alsoCheckFluids);
            if (result != null)
            {
                world.profiler.endSection();
                return result;
            }


            //End if this was the last block
            if (pos.getX() == endPos.getX() && pos.getY() == endPos.getY() && pos.getZ() == endPos.getZ())
            {
                world.profiler.endSection();
                return new FixedRayTraceResult(RayTraceResult.Type.MISS, vecEnd, null, pos);
            }
        }


        //Max iterations reached; force end and warn
        if (maxBlocks >= ITERATION_WARNING_THRESHOLD && (lastWarning == -1 || System.currentTimeMillis() - lastWarning > 1000 * 60 * 5))
        {
            System.err.println("WARNING: BEYOND-LIMIT RAYTRACING DETECTED!  This warning will not show more than once every 5 minutes.  This is usually due to inefficient raytrace calls from another mod");
            System.err.println("This type of error has occurred " + errorCount + " additional times since the last time this message was shown");
            System.err.println("From " + vecStart + " to " + vecEnd + " (distance: " + vecStart.distanceTo(vecEnd) + ")");
            System.err.println("Limit: " + maxBlocks + " iterations (not synonymous to distance, but longer distances are generally more iterations)");
            System.err.println();
            Tools.printStackTrace();
            lastWarning = System.currentTimeMillis();
        }
        else errorCount++;


        world.profiler.endSection();
        return new FixedRayTraceResult(null, null, null, null);
    }


    public static RayTraceResult checkState(World world, BlockPos pos, IBlockState state, Vec3d vecStart, Vec3d vecEnd, boolean isPhysicsCheck, boolean alsoCheckFluids)
    {
        RayTraceResult result = null;

        Block block = state.getBlock();
        if (block instanceof BlockFluidBase || block instanceof BlockLiquid)
        {
            if (alsoCheckFluids || (!isPhysicsCheck && !canSeeThrough(state)))
            {
                result = rayTraceWithinFluid(getActualFluidHeights(world, pos, state.getMaterial()), pos, vecStart, vecEnd);
            }
        }
        else
        {
            if (isPhysicsCheck || !canSeeThrough(state))
            {
                ArrayList<AxisAlignedBB> boxes = new ArrayList<>();
                state.addCollisionBoxToList(world, pos, Block.FULL_BLOCK_AABB.offset(pos), boxes, null, false);

                ArrayList<RayTraceResult> results = new ArrayList<>();
                for (AxisAlignedBB collisionBox : boxes)
                {
                    if (collisionBox != Block.NULL_AABB)
                    {
                        result = rayTraceWithinNonFluid(pos, vecStart, vecEnd, collisionBox);
                        if (result != null) results.add(result);
                    }
                }

                if (results.size() > 0)
                {
                    result = results.get(0);
                    double distSqr, minDistSqr = vecStart.squareDistanceTo(result.hitVec);
                    RayTraceResult r;
                    for (int i = results.size() - 1; i >= 1; i--)
                    {
                        r = results.get(i);
                        distSqr = vecStart.squareDistanceTo(r.hitVec);
                        if (distSqr < minDistSqr)
                        {
                            minDistSqr = distSqr;
                            result = r;
                        }
                    }
                }
            }
        }

        return result;
    }


    public static boolean canSeeThrough(IBlockState blockState)
    {
        //Library filters
        for (Predicate<IBlockState> predicate : BLOCK_STATE_ENDS_RAYTRACE_FILTERS)
        {
            if (!predicate.test(blockState)) return false;
        }


        //Config filters
        if (transparentBlockstates.contains(blockState)) return true;
        if (nonTransparentBlockstates.contains(blockState)) return false;

        Block block = blockState.getBlock();
        if (transparentBlocks.contains(block)) return true;
        if (nonTransparentBlocks.contains(block)) return false;

        Class cls = block.getClass();
        if (transparentBlockClasses.contains(cls)) return true;
        if (nonTransparentBlockClasses.contains(cls)) return false;
        if (!ignoredBlockClasses.contains(cls))
        {
            for (Class<? extends Block> superClass : transparentBlockSuperclasses)
            {
                if (superClass.isAssignableFrom(cls))
                {
                    transparentBlockClasses.add(cls);
                    return true;
                }
            }

            for (Class<? extends Block> superClass : nonTransparentBlockSuperclasses)
            {
                if (superClass.isAssignableFrom(cls))
                {
                    nonTransparentBlockClasses.add(cls);
                    return false;
                }
            }

            ignoredBlockClasses.add(cls);
        }

        Material material = blockState.getMaterial();
        if (transparentMaterials.contains(material)) return true;
        if (nonTransparentMaterials.contains(material)) return false;


        //Default filters

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
            return (block.getMetaFromState(blockState) & 8) != 0;
        }

        return false;
    }


    public static RayTraceResult rayTraceWithinNonFluid(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox)
    {
        if (boundingBox.contains(start)) return new RayTraceResult(start, null, pos);
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(start, end);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec, raytraceresult.sideHit, pos);
    }


    public static RayTraceResult rayTraceWithinFluid(float[] actualFluidHeights, BlockPos pos, Vec3d start, Vec3d end)
    {
        if (fluidContains(actualFluidHeights, start.subtract(pos.getX(), pos.getY(), pos.getZ()))) return new RayTraceResult(start, null, pos);
        if (start.equals(end)) return null;


        //For now, I've decided that a full check is not worth the extra computation it would take...
        //...so I'm going to limit this to a check for the START position (above)...
        //...and then a check for the END position...
        if (fluidContains(actualFluidHeights, end.subtract(pos.getX(), pos.getY(), pos.getZ()))) return new RayTraceResult(end, null, pos);

        //...and then a check for the EXIT position (reverse raytrace into full cube, then check if within heightmap)
        RayTraceResult result = Block.FULL_BLOCK_AABB.offset(pos).calculateIntercept(end, start);
        if (result == null) return null; //Actually not sure how this can happen, but it does...not going to worry about it right now though since this part is makeshift anyway

        end = result.hitVec;
        if (fluidContains(actualFluidHeights, end.subtract(pos.getX(), pos.getY(), pos.getZ()))) return new RayTraceResult(end, null, pos);
        return null;
    }

    public static boolean fluidContains(float[] actualFluidHeights, Vec3d relativeVec)
    {
        //relativeVec is assumed to be inside the cube area when this is called

        //N is -z, W is -x

        //NW-SE is the triangle seamline (the triangles are weighted SW and NE)
        double nw = actualFluidHeights[0];
        double se = actualFluidHeights[3];
        double c = (nw + se) * 0.5;
        double sw, ne;

        //Seamline is defined by z = x, so SW triangle is z >= x (South is positive)
        if (relativeVec.z >= relativeVec.x)
        {
            //SW triangle
            sw = actualFluidHeights[2];
            ne = c + (c - sw);
        }
        else
        {
            //NE triangle
            ne = actualFluidHeights[1];
            sw = c + (c - ne);
        }

        double n = nw + (ne - nw) * relativeVec.x;
        double s = sw + (se - sw) * relativeVec.x;
        return n + (s - n) * relativeVec.z > relativeVec.y;
    }

    public static float[] getActualFluidHeights(World world, BlockPos pos, Material material)
    {
        //NW-SE is the triangle seamline
        float nw = getFluidHeight(world, pos, material);
        float ne = getFluidHeight(world, pos.east(), material);
        float sw = getFluidHeight(world, pos.south(), material);
        float se = getFluidHeight(world, pos.east().south(), material);
        return new float[]{nw, ne, sw, se};
    }

    public static float getFluidHeight(World world, BlockPos pos, Material material)
    {
        //Should work for any instance of BlockLiquid or BlockFluidBase
        int i = 0;
        float f = 0;

        for (int j = 0; j < 4; j++)
        {
            BlockPos blockpos = pos.add(-(j & 1), 0, -(j >> 1 & 1));

            if (world.getBlockState(blockpos.up()).getMaterial() == material) return 1;

            IBlockState other = world.getBlockState(blockpos);
            Material otherMaterial = other.getMaterial();

            if (otherMaterial != material)
            {
                if (!otherMaterial.isSolid())
                {
                    f++;
                    i++;
                }
            }
            else
            {
                int k = other.getValue(BlockLiquid.LEVEL); //This should also work for BlockFluidBase

                if (k >= 8 || k == 0)
                {
                    f += BlockLiquid.getLiquidHeightPercent(k) * 10;
                    i += 10;
                }

                f += BlockLiquid.getLiquidHeightPercent(k);
                i++;
            }
        }

        return 1.0F - f / (float) i;
    }


    public static class FixedRayTraceResult extends RayTraceResult
    {
        public FixedRayTraceResult(Type typeIn, Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn)
        {
            super(typeIn, new Vec3d(0, 0, 0), sideHitIn, blockPosIn);
            hitVec = hitVecIn == null ? null : new Vec3d(hitVecIn.x, hitVecIn.y, hitVecIn.z);
        }
    }
}
