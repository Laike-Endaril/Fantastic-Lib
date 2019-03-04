package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.glu.GLU;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;

import static com.fantasticsource.tools.Tools.*;

public class MCTools
{
    private static Field activeRenderInfoViewportField, activeRenderInfoProjectionField, activeRenderInfoModelviewField, minecraftRenderPartialTicksPausedField, configManagerCONFIGSField;

    static
    {
        try
        {
            configManagerCONFIGSField = ReflectionTool.getField(ConfigManager.class, "CONFIGS");
        }
        catch (Exception e)
        {
            crash(e, 700, false);
        }
    }


    @SideOnly(Side.CLIENT)
    public static void clientInit()
    {
        try
        {
            activeRenderInfoViewportField = ReflectionTool.getField(ActiveRenderInfo.class, "field_178814_a", "VIEWPORT");
            activeRenderInfoProjectionField = ReflectionTool.getField(ActiveRenderInfo.class, "field_178813_c", "PROJECTION");
            activeRenderInfoModelviewField = ReflectionTool.getField(ActiveRenderInfo.class, "field_178812_b", "MODELVIEW");
            minecraftRenderPartialTicksPausedField = ReflectionTool.getField(Minecraft.class, "field_193996_ah", "renderPartialTicksPaused");
        }
        catch (Exception e)
        {
            crash(e, 701, false);
        }
    }


    public static void reloadConfig(String configFilename, String modid) throws IllegalAccessException
    {
        ((Map<String, Configuration>) configManagerCONFIGSField.get(null)).remove(configFilename);
        ConfigManager.sync(modid, Config.Type.INSTANCE);
    }


    @SideOnly(Side.CLIENT)
    public static Pair<Float, Float> getEntityXYInWindow(Entity entity, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        return getEntityXYInWindow(entity, 0, 0, 0, trigLookupTable);
    }

    @SideOnly(Side.CLIENT)
    public static Pair<Float, Float> getEntityXYInWindow(Entity entity, double xOffset, double yOffset, double zOffset, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        Minecraft mc = Minecraft.getMinecraft();
        double partialTick = mc.isGamePaused() ? (double) (float) minecraftRenderPartialTicksPausedField.get(mc) : mc.getRenderPartialTicks();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick + xOffset;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick + yOffset;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick + zOffset;

        return get2DWindowCoordsFrom3DWorldCoords(x, y, z, partialTick, trigLookupTable);
    }

    @SideOnly(Side.CLIENT)
    public static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        Minecraft mc = Minecraft.getMinecraft();
        double partialTick = mc.isGamePaused() ? (double) (float) minecraftRenderPartialTicksPausedField.get(mc) : mc.getRenderPartialTicks();
        return get2DWindowCoordsFrom3DWorldCoords(x, y, z, partialTick, trigLookupTable);
    }


    /**
     * When the entity is visible in the current projection, the returned values are its position in the window
     * When the entity is not visible in the current projection, the returned values are an off-screen position with the correct ratio to be used for an edge-of-screen indicator
     */
    @SideOnly(Side.CLIENT)
    private static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z, double partialTick, TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        EntityPlayer player = Minecraft.getMinecraft().player;
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;

        FloatBuffer result = FloatBuffer.allocate(3);
        GLU.gluProject((float) (x - px), (float) (y - py), (float) (z - pz), (FloatBuffer) activeRenderInfoModelviewField.get(null), (FloatBuffer) activeRenderInfoProjectionField.get(null), (IntBuffer) activeRenderInfoViewportField.get(null), result);

        RenderManager manager = Minecraft.getMinecraft().getRenderManager();
        Vec3d cameraPos = getCameraPosition();
        double yawDif = posMod(angleDifDeg(manager.playerViewY, getYawDeg(cameraPos, new Vec3d(x, y, z), trigLookupTable)), 360);
        double pitchDif = posMod(angleDifDeg(manager.playerViewX, getPitchDeg(cameraPos, new Vec3d(x, y, z), trigLookupTable)), 360);
        if (yawDif >= 180) yawDif -= 360;
        if (pitchDif >= 180) pitchDif -= 360;
        double indicatorAngle = pitchDif * trigLookupTable.cos(degtorad(yawDif));
        System.out.println(indicatorAngle);

        float xx;
        if (yawDif >= 90) xx = getViewportWidth();
        else if (yawDif <= -90) xx = 0;
        else xx = result.get(0);

        float yy;
        if (pitchDif >= 90) yy = 0;
        else if (pitchDif <= -90) yy = getViewportHeight();
        else yy = (float) getViewportHeight() - result.get(1);

        return new Pair<>(xx, yy);
    }


    @SideOnly(Side.CLIENT)
    public static int getViewportWidth() throws IllegalAccessException
    {
        return ((IntBuffer) activeRenderInfoViewportField.get(null)).get(2);
    }

    @SideOnly(Side.CLIENT)
    public static int getViewportHeight() throws IllegalAccessException
    {
        return ((IntBuffer) activeRenderInfoViewportField.get(null)).get(3);
    }

    @SideOnly(Side.CLIENT)
    public static Vec3d getCameraPosition()
    {
        return Minecraft.getMinecraft().player.getPositionVector().add(ActiveRenderInfo.getCameraPosition());
    }


    public static String getDataDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory() + File.separator + "data" + File.separator;
    }

    public static void crash(Exception e, int code, boolean hardExit)
    {
        e.printStackTrace();
        FMLCommonHandler.instance().exitJava(code, hardExit);
    }

    public static boolean isRidingOrRiddenBy(Entity entity1, Entity entity2)
    {
        //getRidingEntity DOES NOT GET THE RIDING ENTITY!  It gets the RIDDEN entity (these are opposites, ppl...)
        return entity1 != null && entity2 != null && (entity1.getRidingEntity() == entity2 || entity2.getRidingEntity() == entity1);
    }

    public static boolean isOwned(Entity entity)
    {
        return getOwner(entity) != null;
    }

    public static Entity getOwner(Entity entity)
    {
        if (!(entity instanceof IEntityOwnable)) return null;
        return ((IEntityOwnable) entity).getOwner();
    }

    public static double getYawDeg(Vec3d fromVec, Vec3d toVec, TrigLookupTable trigTable)
    {
        return radtodeg(trigTable.arctanFullcircle(fromVec.z, fromVec.x, toVec.z, toVec.x));
    }

    public static double getPitchDeg(Vec3d fromVec, Vec3d toVec, TrigLookupTable trigTable)
    {
        double result = radtodeg(trigTable.arctanFullcircle(0, 0, distance(fromVec.x, fromVec.z, toVec.x, toVec.z), toVec.y - fromVec.y));
        return result >= 180 ? result - 360 : result;
    }

    public static double getAttribute(EntityLivingBase entity, IAttribute attribute, double defaultVal)
    {
        //getEntityAttribute is incorrectly tagged as @Nonnull; it can and will return a null value sometimes, thus this helper
        IAttributeInstance iAttributeInstance = entity.getEntityAttribute(attribute);
        return iAttributeInstance == null ? defaultVal : iAttributeInstance.getAttributeValue();
    }

    public static void teleport(EntityLivingBase entity, BlockPos pos, boolean doEvent, float fallDamage)
    {
        teleport(entity, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, doEvent, fallDamage);
    }

    public static void teleport(EntityLivingBase entity, double x, double y, double z, boolean doEvent, float fallDamage)
    {
        if (!doEvent)
        {
            entity.setPositionAndUpdate(x, y, z);
            entity.fallDistance = 0;
            if (fallDamage > 0) entity.attackEntityFrom(DamageSource.FALL, fallDamage);
        }
        else
        {
            EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, fallDamage);
            if (!MinecraftForge.EVENT_BUS.post(event))
            {
                if (entity.isRiding())
                {
                    entity.dismountRidingEntity();
                }

                entity.setPositionAndUpdate(event.getTargetX(), event.getTargetY(), event.getTargetZ());
                entity.fallDistance = 0;

                fallDamage = event.getAttackDamage();
                if (fallDamage > 0) entity.attackEntityFrom(DamageSource.FALL, fallDamage);
            }
        }
    }

    public static BlockPos randomPos(BlockPos centerPos, int xzRange, int yRange)
    {
        return centerPos.add(-xzRange + (int) (Math.random() * xzRange * 2 + 1), -xzRange + (int) (Math.random() * xzRange * 2 + 1), -yRange + (int) (Math.random() * yRange * 2 + 1));
    }

    public static boolean isOP(EntityPlayerMP player)
    {
        for (String string : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayerNames())
        {
            if (string.equalsIgnoreCase(player.getGameProfile().getName())) return true;
        }

        return false;
    }

    public static boolean isServerOwner(EntityPlayerMP player)
    {
        return FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner().equalsIgnoreCase(player.getGameProfile().getName());
    }

    public static boolean isPassive(EntityLivingBase livingBase)
    {
        //This is not 100% accurate for modded entities, so having an entity-specific config override is suggested
        if (livingBase == null) return false;

        if (livingBase instanceof EntityLiving)
        {
            EntityLiving living = (EntityLiving) livingBase;
            EntityAIBase ai;
            for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries)
            {
                ai = task.action;
                if (ai instanceof NPEAttackTargetTaskHolder || ai instanceof EntityAIAttackMelee || ai instanceof EntityAIAttackRanged || ai instanceof EntityAIAttackRangedBow) return false;
            }
        }

        return getAttribute(livingBase, SharedMonsterAttributes.ATTACK_DAMAGE, 0) <= 0;
    }

    public static void printAITasks(EntityLiving living)
    {
        ExplicitPriorityQueue<EntityAIBase> queue = new ExplicitPriorityQueue<>();
        EntityAIBase ai;
        String str;
        double priority;

        System.out.println("===================================");
        System.out.println(living.getName());
        System.out.println("===================================");
        for (EntityAITasks.EntityAITaskEntry task : living.targetTasks.taskEntries)
        {
            queue.add(task.action, task.priority);
        }
        while (queue.size() > 0)
        {
            priority = queue.peekPriority();
            ai = queue.poll();
            str = ai.getClass().getSimpleName();
            if (str.equals("")) str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getPackage().getName() + ".???????";
            System.out.println(priority + "\t" + str);
        }
        System.out.println("===================================");
        for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries)
        {
            queue.add(task.action, task.priority);
        }
        while (queue.size() > 0)
        {
            priority = queue.peekPriority();
            ai = queue.poll();
            str = ai.getClass().getSimpleName();
            if (str.equals("")) str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getPackage().getName() + ".???????";
            System.out.println(priority + "\t" + str);
        }
        System.out.println("===================================");
        System.out.println();
    }
}
