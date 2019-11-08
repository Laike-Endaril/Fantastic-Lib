package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.nbt.NBTBase;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.util.vector.Quaternion;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.fantasticsource.tools.Tools.distance;
import static com.fantasticsource.tools.Tools.radtodeg;

public class MCTools
{
    private static Field configManagerCONFIGSField, languageManagerCurrentLocaleField, localePropertiesField;
    private static boolean host = false;
    public static final TrigLookupTable TRIG_TABLE = new TrigLookupTable(1024);

    static
    {
        try
        {
            configManagerCONFIGSField = ReflectionTool.getField(ConfigManager.class, "CONFIGS");
            if (FantasticLib.isClient)
            {
                languageManagerCurrentLocaleField = ReflectionTool.getField(LanguageManager.class, "field_135049_a", "CURRENT_LOCALE");
                localePropertiesField = ReflectionTool.getField(Locale.class, "field_135032_a", "properties");
            }
        }
        catch (Exception e)
        {
            crash(e, 700, false);
        }
    }


    public static Quaternion rotatedQuaternion(Quaternion v, Quaternion axis, double theta)
    {
        double sinThetaDiv2 = TRIG_TABLE.sin(theta * 0.5);
        double cosThetaDiv2 = TRIG_TABLE.cos(theta * 0.5);
        Quaternion q = new Quaternion((float) (sinThetaDiv2 * axis.x), (float) (sinThetaDiv2 * axis.y), (float) (sinThetaDiv2 * axis.z), (float) cosThetaDiv2);
        Quaternion qConjugate = new Quaternion((float) -(sinThetaDiv2 * axis.x), (float) -(sinThetaDiv2 * axis.y), (float) -(sinThetaDiv2 * axis.z), (float) cosThetaDiv2);
        return Quaternion.mul(Quaternion.mul(q, v, null), qConjugate, null);
    }


    public static void spawnDebugSnowball(World world, double x, double y, double z)
    {
        EntitySnowball snowball = new EntitySnowball(world, x, y, z);
        snowball.setVelocity(0, 0, 0);
        snowball.setNoGravity(true);
        world.spawnEntity(snowball);
    }

    public static double lookAngleDifDeg(EntityLivingBase searcher, Entity target)
    {
        double angleDif = Vec3d.fromPitchYaw(searcher.rotationPitch, searcher.rotationYawHead).normalize().dotProduct(new Vec3d(target.posX - searcher.posX, (target.posY + target.height / 2) - (searcher.posY + searcher.getEyeHeight()), target.posZ - searcher.posZ).normalize());

        //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
        if (angleDif < -1) angleDif = -1;
        else if (angleDif > 1) angleDif = 1;

        return Tools.radtodeg(TRIG_TABLE.arccos(angleDif)); //0 in front, 180 in back
    }

    public static double angleDifDeg(Vec3d origin, Vec3d p1, Vec3d p2)
    {
        double angleDif = p1.subtract(origin).normalize().dotProduct(p2.subtract(origin).normalize());

        //And because Vec3d.fromPitchYaw occasionally returns values barely out of the range of (-1, 1)...
        if (angleDif < -1) angleDif = -1;
        else if (angleDif > 1) angleDif = 1;

        return Tools.radtodeg(TRIG_TABLE.arccos(angleDif)); //0 in front, 180 in back
    }


    public static void playSimpleSoundOnClient(EntityPlayerMP player, ResourceLocation rl)
    {
        Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl), player);
    }


    @SideOnly(Side.CLIENT)
    public static void removeLangKey(String key) throws IllegalAccessException
    {
        Locale locale = (Locale) languageManagerCurrentLocaleField.get(null);
        Map<String, String> properties = (Map<String, String>) localePropertiesField.get(locale);
        properties.remove(key);
    }

    @SideOnly(Side.CLIENT)
    public static void addLangKey(String key, String value) throws IllegalAccessException
    {
        Locale locale = (Locale) languageManagerCurrentLocaleField.get(null);
        Map<String, String> properties = (Map<String, String>) localePropertiesField.get(locale);
        properties.put(key, value);
    }


    public static void populateEntityMap(String[] regexArray, LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> mapToPopulate)
    {
        for (String regex : regexArray)
        {
            String[] tokens = Tools.fixedSplit(regex, ":");
            String domain = "minecraft", name, specificName = ".*";

            if (tokens.length == 1)
            {
                name = tokens[0];
            }
            else
            {
                domain = tokens[0];
                name = tokens[1];
                if (tokens.length > 2) specificName = tokens[2];
            }

            if (name.toLowerCase().equals("player"))
            {
                mapToPopulate.computeIfAbsent(EntityPlayerMP.class, o -> new HashSet<>()).add(specificName);
            }
            else
            {
                for (Map.Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries())
                {
                    if (!Pattern.matches(domain, entry.getKey().getResourceDomain())) continue;
                    if (!Pattern.matches(name, entry.getKey().getResourcePath())) continue;

                    Class cls = entry.getValue().getEntityClass();
                    if (!(EntityLivingBase.class.isAssignableFrom(cls))) continue;

                    mapToPopulate.computeIfAbsent((Class<? extends EntityLivingBase>) cls, o -> new HashSet<>()).add(specificName);
                }
            }
        }
    }

    public static boolean entityMatchesMap(EntityLivingBase entity, LinkedHashMap<Class<? extends EntityLivingBase>, HashSet<String>> populatedMap)
    {
        HashSet<String> set = populatedMap.get(entity.getClass());
        if (set == null) return false;

        String name = entity.getName();
        for (String s : set) if (Pattern.matches(s, name)) return true;
        return false;
    }


    public static ArrayList<String> legibleNBT(NBTBase nbt)
    {
        return legibleNBT(nbt.toString());
    }

    public static ArrayList<String> legibleNBT(String nbtString)
    {
        ArrayList<String> result = new ArrayList<>();

        char[] chars = nbtString.toCharArray();
        StringBuilder current = new StringBuilder();
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            switch (c)
            {
                case '{':
                case '[':
                    if (!current.toString().equals("")) result.add(indent + current.toString());
                    result.add(indent.toString() + c);
                    current = new StringBuilder();
                    indent.append(" ");
                    break;

                case '}':
                case ']':
                    if (!current.toString().equals("")) result.add(indent + current.toString());
                    indent = new StringBuilder(indent.substring(0, indent.length() - 1));
                    result.add(indent.toString() + c + (i + 1 < chars.length && chars[i + 1] == ',' ? ',' : ""));
                    current = new StringBuilder();
                    break;

                case ',':
                    if (!current.toString().equals("")) result.add(indent + current.toString() + c);
                    current = new StringBuilder();
                    break;

                default:
                    current.append(c);
            }
        }

        return result;
    }

    /**
     * Whether we are hosting the game; returns true if a server is currently running within this application
     */
    public static boolean hosting()
    {
        return host;
    }

    public static void serverStart(FMLServerAboutToStartEvent event)
    {
        host = true;
    }

    public static void serverStop(FMLServerStoppedEvent event)
    {
        host = false;
    }


    public static String getConfigDir()
    {
        return Loader.instance().getConfigDir().getAbsolutePath() + File.separator;
    }

    public static Configuration getConfig(String modid) throws IllegalAccessException
    {
        return ((Map<String, Configuration>) configManagerCONFIGSField.get(null)).get(getConfigDir() + modid + ".cfg");
    }

    public static void reloadConfig(String modid) throws IllegalAccessException
    {
        reloadConfig(getConfigDir() + modid + ".cfg", modid);
    }

    public static void reloadConfig(String configFilename, String modid) throws IllegalAccessException
    {
        ((Map<String, Configuration>) configManagerCONFIGSField.get(null)).remove(configFilename);
        ConfigManager.sync(modid, Config.Type.INSTANCE);
    }


    public static String getWorldSaveDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory() + File.separator;
    }

    public static String getDataDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory() + File.separator + "data" + File.separator;
    }

    public static String getPlayerDataDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory() + File.separator + "playerdata" + File.separator;
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


    public static double getYawRad(Vec3d fromVec, Vec3d toVec, TrigLookupTable trigTable)
    {
        return trigTable.arctanFullcircle(fromVec.z, fromVec.x, toVec.z, toVec.x);
    }

    public static double getYawDeg(Vec3d fromVec, Vec3d toVec, TrigLookupTable trigTable)
    {
        return radtodeg(getYawRad(fromVec, toVec, trigTable));
    }

    public static double getPitchRad(Vec3d fromVec, Vec3d toVec, TrigLookupTable trigTable)
    {
        double result = trigTable.arctanFullcircle(0, 0, distance(fromVec.x, fromVec.z, toVec.x, toVec.z), toVec.y - fromVec.y);
        return result >= Math.PI ? result - Math.PI * 2 : result;
    }

    public static double getPitchDeg(Vec3d fromVec, Vec3d toVec, TrigLookupTable trigTable)
    {
        return radtodeg(getPitchRad(fromVec, toVec, trigTable));
    }


    public static double getAttribute(EntityLivingBase entity, IAttribute attribute, double defaultVal)
    {
        if (entity == null) return defaultVal;

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
        if (isServerOwner(player)) return true;

        for (String string : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getOppedPlayerNames())
        {
            if (string.equalsIgnoreCase(player.getGameProfile().getName())) return true;
        }

        return false;
    }

    public static boolean isServerOwner(EntityPlayerMP player)
    {
        return player.getName().equals(FMLCommonHandler.instance().getMinecraftServerInstance().getServerOwner());
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
        for (String s : getAITaskData(living)) System.out.println(s);
    }

    public static ArrayList<String> getAITaskData(EntityLiving living)
    {
        ArrayList<String> result = new ArrayList<>();

        ExplicitPriorityQueue<EntityAIBase> queue = new ExplicitPriorityQueue<>();
        EntityAIBase ai;
        String str;
        double priority;

        result.add("===================================");
        result.add(living.getName());
        result.add("===================================");
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
            result.add(priority + "\t" + str);
        }
        result.add("===================================");
        for (EntityAITasks.EntityAITaskEntry task : living.tasks.taskEntries)
        {
            queue.add(task.action, task.priority);
        }
        while (queue.size() > 0)
        {
            priority = queue.peekPriority();
            ai = queue.poll();
            boolean isNPE = false;

            if (ai instanceof NPEAttackTargetTaskHolder)
            {
                isNPE = true;
                ai = ((NPEAttackTargetTaskHolder) ai).getBadAI();
            }

            str = ai.getClass().getSimpleName();
            if (str.equals("")) str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getPackage().getName() + ".???????";

            result.add(priority + "\t" + (isNPE ? "NpeAttackTargetTaskHolder (" + str + ")" : str));
        }
        result.add("===================================");
        result.add("");

        return result;
    }
}
