package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.command.CommandResultStats;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.DimensionType;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;
import org.lwjgl.util.vector.Quaternion;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Pattern;

import static com.fantasticsource.tools.Tools.distance;
import static com.fantasticsource.tools.Tools.radtodeg;

public class MCTools
{
    public static final TrigLookupTable TRIG_TABLE = new TrigLookupTable(1024);
    private static Field configManagerCONFIGSField, languageManagerCurrentLocaleField, localePropertiesField;
    private static boolean host = false;

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


    public static PNG getPNG(ResourceLocation rl)
    {
        try
        {
            return PNG.load(Minecraft.getMinecraft().getResourceManager().getResource(rl).getInputStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public static GameType getGameType(EntityPlayer player)
    {
        if (player instanceof EntityPlayerMP) return ((EntityPlayerMP) player).interactionManager.getGameType();

        if (player instanceof AbstractClientPlayer)
        {
            NetworkPlayerInfo info = Minecraft.getMinecraft().getConnection().getPlayerInfo(player.getGameProfile().getId());
            if (info == null) return null;
            return info.getGameType();
        }

        throw new IllegalArgumentException("Unknown player class: " + player.getClass().getName());
    }


    public static boolean devEnv()
    {
        return FantasticLib.DEV_ENV;
    }


    public static BufferedReader getJarResourceReader(Class classInJar, String resourcePathAndName)
    {
        return new BufferedReader(new InputStreamReader(getJarResourceStream(classInJar, resourcePathAndName)));
    }

    public static InputStream getJarResourceStream(Class classInJar, String resourcePathAndName)
    {
        return classInJar.getClassLoader().getResourceAsStream(resourcePathAndName);
    }


    @SideOnly(Side.CLIENT)
    public static String getResourcePackDir()
    {
        return getConfigDir() + ".." + File.separator + "resourcepacks" + File.separator;
    }


    public static boolean isWhitelisted(EntityPlayerMP player)
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        return !server.isDedicatedServer() || server.getPlayerList().getWhitelistedPlayers().isWhitelisted(player.getGameProfile());
    }


    public static String getSaveFolder(WorldProvider worldProvider)
    {
        String saveFolder = worldProvider.getSaveFolder();
        return saveFolder != null ? saveFolder : ".";
    }


    public static DimensionType getDimensionType(String name)
    {
        try
        {
            return DimensionType.byName(name);
        }
        catch (IllegalArgumentException e)
        {
            return null;
        }
    }


    public static void removeSubNBTAndClean(NBTTagCompound compound, String... keys)
    {
        if (keys.length == 0) return;


        String key = keys[0];
        if (keys.length == 1)
        {
            compound.removeTag(key);
            return;
        }


        if (!compound.hasKey(key)) return;


        String[] newKeys = new String[keys.length - 1];
        System.arraycopy(keys, 1, newKeys, 0, newKeys.length);
        NBTTagCompound subCompound = compound.getCompoundTag(key);
        removeSubNBTAndClean(subCompound, newKeys);
        if (subCompound.getKeySet().size() == 0) compound.removeTag(key);
    }

    public static NBTTagCompound getOrGenerateSubCompound(NBTTagCompound compound, String... keys)
    {
        for (String key : keys)
        {
            if (!compound.hasKey(key)) compound.setTag(key, new NBTTagCompound());
            compound = compound.getCompoundTag(key);
        }

        return compound;
    }

    public static NBTTagCompound getSubCompoundIfExists(NBTTagCompound compound, String... keys)
    {
        for (String key : keys)
        {
            if (!compound.hasKey(key)) return null;
            compound = compound.getCompoundTag(key);
        }

        return compound;
    }

    public static NBTTagCompound combineNBT(NBTTagCompound... sources)
    {
        NBTTagCompound result = new NBTTagCompound();
        mergeNBT(result, true, sources);
        return result;
    }

    public static void mergeNBT(NBTTagCompound destination, boolean overwrite, NBTTagCompound... sources)
    {
        for (NBTTagCompound source : sources)
        {
            if (source == null) continue;

            for (String key : source.getKeySet())
            {
                NBTBase sourceTag = source.getTag(key), destinationTag = destination.getTag(key);

                if (destinationTag == null)
                {
                    destination.setTag(key, sourceTag.copy());
                }
                else if (sourceTag instanceof NBTTagCompound && destinationTag instanceof NBTTagCompound)
                {
                    mergeNBT((NBTTagCompound) destinationTag, overwrite, (NBTTagCompound) sourceTag);
                }
                else if (sourceTag instanceof NBTTagList && destinationTag instanceof NBTTagList)
                {
                    mergeNBT((NBTTagList) destinationTag, (NBTTagList) sourceTag);
                }
                else if (sourceTag instanceof NBTTagByteArray && destinationTag instanceof NBTTagByteArray)
                {
                    destination.setTag(key, combineNBT((NBTTagByteArray) destinationTag, (NBTTagByteArray) sourceTag));
                }
                else if (sourceTag instanceof NBTTagIntArray && destinationTag instanceof NBTTagIntArray)
                {
                    destination.setTag(key, combineNBT((NBTTagIntArray) destinationTag, (NBTTagIntArray) sourceTag));
                }
                else if (sourceTag instanceof NBTTagLongArray && destinationTag instanceof NBTTagLongArray)
                {
                    throw new NotImplementedException("Didn't feel like doing the reflection at the time, and not sure this will ever come up.  Poke Laike Endaril if you see this.");
                }
                else if (overwrite)
                {
                    destination.setTag(key, sourceTag.copy());
                }
            }
        }
    }

    public static NBTTagList combineNBT(NBTTagList... sources)
    {
        NBTTagList result = new NBTTagList();
        mergeNBT(result, sources);
        return result;
    }

    public static void mergeNBT(NBTTagList destination, NBTTagList... sources)
    {
        for (NBTTagList source : sources)
        {
            int size = source.tagCount();
            for (int i = 0; i < size; i++) destination.appendTag(source.get(i));
        }
    }

    public static NBTTagByteArray combineNBT(NBTTagByteArray... sources)
    {
        byte[] sArray;
        int size = 0, offset = 0;
        for (NBTTagByteArray source : sources)
        {
            size += source.getByteArray().length;
        }

        byte[] newArray = new byte[size];

        for (NBTTagByteArray source : sources)
        {
            sArray = source.getByteArray();
            size = sArray.length;
            System.arraycopy(sArray, 0, newArray, offset, size);
            offset += size;
        }

        return new NBTTagByteArray(newArray);
    }

    public static NBTTagIntArray combineNBT(NBTTagIntArray... sources)
    {
        int[] sArray;
        int size = 0, offset = 0;
        for (NBTTagIntArray source : sources)
        {
            size += source.getIntArray().length;
        }

        int[] newArray = new int[size];

        for (NBTTagIntArray source : sources)
        {
            sArray = source.getIntArray();
            size = sArray.length;
            System.arraycopy(sArray, 0, newArray, offset, size);
            offset += size;
        }

        return new NBTTagIntArray(newArray);
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


    public static ItemStack getItemStack(String itemID)
    {
        String domain = "minecraft", name;
        int meta = 0;

        String[] tokens = Tools.fixedSplit("" + itemID, ":");
        if (tokens.length == 1) name = tokens[0];
        else if (tokens.length == 2)
        {
            try
            {
                meta = Integer.parseInt(tokens[1]);
                name = tokens[0];
            }
            catch (NumberFormatException e)
            {
                domain = tokens[0];
                name = tokens[1];
            }
        }
        else
        {
            domain = tokens[0];
            name = tokens[1];
            meta = Integer.parseInt(tokens[2]);
        }

        return getItemStack(domain, name, meta);
    }

    public static ItemStack getItemStack(String domain, String name, int meta)
    {
        ResourceLocation rl = new ResourceLocation(domain, name);
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        if (item != null) return new ItemStack(item, 1, meta);

        Block block = ForgeRegistries.BLOCKS.getValue(rl);
        if (block != null) return new ItemStack(block, 1, meta);

        return null;
    }

    public static ItemStack cloneItemStack(ItemStack stack)
    {
        try
        {
            return new ItemStack(JsonToNBT.getTagFromJson(stack.serializeNBT().toString()));
        }
        catch (NBTException e)
        {
            e.printStackTrace();
            return ItemStack.EMPTY;
        }
    }


    public static void give(EntityPlayerMP player, ItemStack stack)
    {
        int count = stack.getCount();
        boolean flag = player.inventory.addItemStackToInventory(stack);

        if (flag)
        {
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.inventoryContainer.detectAndSendChanges();
        }

        if (flag && stack.isEmpty())
        {
            stack.setCount(1);
            player.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count);
            EntityItem entityitem1 = player.dropItem(stack, false);

            if (entityitem1 != null)
            {
                entityitem1.makeFakeItem();
            }
        }
        else
        {
            player.setCommandStat(CommandResultStats.Type.AFFECTED_ITEMS, count - stack.getCount());
            EntityItem entityitem = player.dropItem(stack, false);

            if (entityitem != null)
            {
                entityitem.setNoPickupDelay();
                entityitem.setOwner(player.getName());
            }
        }
    }


    public static void setLore(ItemStack stack, String lore)
    {
        if (lore == null || lore.equals(""))
        {
            removeLore(stack);
            return;
        }

        setLore(stack, Arrays.asList(Tools.fixedSplit(lore, "\n")));
    }

    public static void setLore(ItemStack stack, List<String> loreLines)
    {
        if (loreLines == null || loreLines.size() == 0 || (loreLines.size() == 1 && (loreLines.get(0) == null || loreLines.get(0).equals(""))))
        {
            removeLore(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey("display")) compound.setTag("display", new NBTTagCompound());
        compound = compound.getCompoundTag("display");

        compound.setTag("Lore", new NBTTagList());
        NBTTagList lore = compound.getTagList("Lore", Constants.NBT.TAG_STRING);

        for (String line : loreLines) lore.appendTag(new NBTTagString(line));
    }

    public static void removeLore(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("display")) return;

        NBTTagCompound display = compound.getCompoundTag("display");
        if (!display.hasKey("Lore")) return;

        display.removeTag("Lore");

        if (display.getKeySet().size() == 0) compound.removeTag("display");
    }

    public static ArrayList<String> getLore(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return null;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("display")) return null;

        compound = compound.getCompoundTag("display");
        if (!compound.hasKey("Lore")) return null;

        NBTTagList lore = compound.getTagList("Lore", Constants.NBT.TAG_STRING);
        ArrayList<String> loreLines = new ArrayList<>();
        for (int i = 0; i < lore.tagCount(); i++) loreLines.add(lore.getStringTagAt(i));

        return loreLines;
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
        return angleDifDeg(searcher.getPositionEyes(0), searcher.rotationYawHead, searcher.rotationPitch, target.getPositionVector().addVector(0, target.height / 2, 0));
    }

    public static double angleDifDeg(Vec3d origin, float yaw, float pitch, Vec3d p2)
    {
        return angleDifDeg(origin, origin.add(Vec3d.fromPitchYaw(pitch, yaw)), p2);
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

    public static void playSimpleSoundAtEntityPosition(ResourceLocation rl, Entity entity)
    {
        playSimpleSoundAtEntityPosition(rl, entity, 16);
    }

    public static void playSimpleSoundAtEntityPosition(ResourceLocation rl, Entity entity, double maxDistance)
    {
        playSimpleSoundAtEntityPosition(rl, entity, maxDistance, 2, 1, 1);
    }

    public static void playSimpleSoundAtEntityPosition(ResourceLocation rl, Entity entity, double maxDistance, int attenuationType, float volume, float pitch)
    {
        playSimpleSoundAtEntityPosition(rl, entity, maxDistance, attenuationType, volume, pitch, SoundCategory.MASTER);
    }

    public static void playSimpleSoundAtEntityPosition(ResourceLocation rl, Entity entity, double maxDistance, int attenuationType, float volume, float pitch, SoundCategory soundCategory)
    {
        playSimpleSoundAtPosition(rl, entity.dimension, entity.posX, entity.posY, entity.posZ, maxDistance, attenuationType, volume, pitch, soundCategory);
    }

    public static void playSimpleSoundAtPosition(ResourceLocation rl, int dimension, double x, double y, double z)
    {
        playSimpleSoundAtPosition(rl, dimension, x, y, z, 16);
    }

    public static void playSimpleSoundAtPosition(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance)
    {
        playSimpleSoundAtPosition(rl, dimension, x, y, z, maxDistance, 2, 1, 1);
    }

    public static void playSimpleSoundAtPosition(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, int attenuationType, float volume, float pitch)
    {
        playSimpleSoundAtPosition(rl, dimension, x, y, z, maxDistance, attenuationType, volume, pitch, SoundCategory.MASTER);
    }

    public static void playSimpleSoundAtPosition(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, int attenuationType, float volume, float pitch, SoundCategory soundCategory)
    {
        Network.WRAPPER.sendToAllAround(new Network.PlaySimpleSoundPacket(rl, (float) x, (float) y, (float) z, attenuationType, volume, pitch, soundCategory), new NetworkRegistry.TargetPoint(dimension, x, y, z, maxDistance));
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


    public static void populateEntityDoubleMap(String[] regexArray, LinkedHashMap<Class<? extends EntityLivingBase>, LinkedHashMap<String, Double>> mapToPopulate)
    {
        for (String regex : regexArray)
        {
            String[] tokens = Tools.fixedSplit(regex, ",");
            double value = Double.parseDouble(tokens[1].trim());

            String[] tokens2 = Tools.fixedSplit(tokens[0].trim(), ":");
            String domain = "minecraft", name, specificName = ".*";

            if (tokens2.length == 1)
            {
                name = tokens2[0];
            }
            else
            {
                domain = tokens2[0];
                name = tokens2[1];
                if (tokens2.length > 2) specificName = tokens2[2];
            }

            if (name.toLowerCase().equals("player"))
            {
                mapToPopulate.computeIfAbsent(EntityPlayerMP.class, o -> new LinkedHashMap<>()).put(specificName, value);
            }
            else
            {
                for (Map.Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries())
                {
                    if (!Pattern.matches(domain, entry.getKey().getResourceDomain())) continue;
                    if (!Pattern.matches(name, entry.getKey().getResourcePath())) continue;

                    Class cls = entry.getValue().getEntityClass();
                    if (!(EntityLivingBase.class.isAssignableFrom(cls))) continue;

                    mapToPopulate.computeIfAbsent((Class<? extends EntityLivingBase>) cls, o -> new LinkedHashMap<>()).put(specificName, value);
                }
            }
        }
    }

    public static double entityMatchesDoubleMapOrDefault(EntityLivingBase entity, LinkedHashMap<Class<? extends EntityLivingBase>, LinkedHashMap<String, Double>> populatedMap, double defaultValue)
    {
        HashMap<String, Double> map = populatedMap.get(entity.getClass());
        if (map == null) return defaultValue;

        String name = entity.getName();
        for (Map.Entry<String, Double> entry : map.entrySet())
        {
            if (Pattern.matches(entry.getKey(), name)) return entry.getValue();
        }

        return defaultValue;
    }


    public static void populateEntityIntMap(String[] regexArray, LinkedHashMap<Class<? extends EntityLivingBase>, LinkedHashMap<String, Integer>> mapToPopulate)
    {
        for (String regex : regexArray)
        {
            String[] tokens = Tools.fixedSplit(regex, ",");
            int value = Integer.parseInt(tokens[1].trim());

            String[] tokens2 = Tools.fixedSplit(tokens[0].trim(), ":");
            String domain = "minecraft", name, specificName = ".*";

            if (tokens2.length == 1)
            {
                name = tokens2[0];
            }
            else
            {
                domain = tokens2[0];
                name = tokens2[1];
                if (tokens2.length > 2) specificName = tokens2[2];
            }

            if (name.toLowerCase().equals("player"))
            {
                mapToPopulate.computeIfAbsent(EntityPlayerMP.class, o -> new LinkedHashMap<>()).put(specificName, value);
            }
            else
            {
                for (Map.Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries())
                {
                    if (!Pattern.matches(domain, entry.getKey().getResourceDomain())) continue;
                    if (!Pattern.matches(name, entry.getKey().getResourcePath())) continue;

                    Class cls = entry.getValue().getEntityClass();
                    if (!(EntityLivingBase.class.isAssignableFrom(cls))) continue;

                    mapToPopulate.computeIfAbsent((Class<? extends EntityLivingBase>) cls, o -> new LinkedHashMap<>()).put(specificName, value);
                }
            }
        }
    }

    public static int entityMatchesIntMapOrDefault(EntityLivingBase entity, LinkedHashMap<Class<? extends EntityLivingBase>, LinkedHashMap<String, Integer>> populatedMap, int defaultValue)
    {
        HashMap<String, Integer> map = populatedMap.get(entity.getClass());
        if (map == null) return defaultValue;

        String name = entity.getName();
        for (Map.Entry<String, Integer> entry : map.entrySet())
        {
            if (Pattern.matches(entry.getKey(), name)) return entry.getValue();
        }

        return defaultValue;
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
        //TODO This wipes config tooltips and doesn't fully load configs
        reloadConfig(getConfigDir() + modid + ".cfg", modid);
    }

    public static void reloadConfig(String configFilename, String modid) throws IllegalAccessException
    {
        //TODO This wipes config tooltips and doesn't fully load configs
        ((Map<String, Configuration>) configManagerCONFIGSField.get(null)).remove(configFilename);
        ConfigManager.sync(modid, Config.Type.INSTANCE);
    }


    public static String getWorldSaveDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory().toString() + File.separator;
    }

    public static String getDataDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory().toString() + File.separator + "data" + File.separator;
    }

    public static String getPlayerDataDir(MinecraftServer server)
    {
        return server.worlds[0].getSaveHandler().getWorldDirectory().toString() + File.separator + "playerdata" + File.separator;
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


    public static Double getAttribute(EntityLivingBase entity, String attributeName)
    {
        for (IAttributeInstance instance : entity.getAttributeMap().getAllAttributes())
        {
            if (instance.getAttribute().getName().equals(attributeName)) return instance.getAttributeValue();
        }

        return null;
    }

    public static double getAttribute(EntityLivingBase entity, IAttribute attribute)
    {
        return getAttribute(entity, attribute, attribute.getDefaultValue());
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
