package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.lwjgl.Quaternion;
import com.fantasticsource.tools.PNG;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.ExplicitPriorityQueue;
import com.fantasticsource.tools.datastructures.VectorN;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.Locale;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.*;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityDispatcher;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.NotImplementedException;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

import static com.fantasticsource.tools.Tools.distance;
import static com.fantasticsource.tools.Tools.radtodeg;

public class MCTools
{
    public static final TrigLookupTable TRIG_TABLE = new TrigLookupTable(1024);

    public static final Int2ObjectMap<WorldServer> DIMENSION_MANAGER_WORLDS = (Int2ObjectMap<WorldServer>) ReflectionTool.get(DimensionManager.class, "worlds", null);

    protected static final Field
            CONFIG_MANAGER_CONFIGS_FIELD = ReflectionTool.getField(ConfigManager.class, "CONFIGS"),
            CONFIGURATION_CHANGED_FIELD = ReflectionTool.getField(Configuration.class, "changed"),
            ITEMSTACK_CAPABILITIES_FIELD = ReflectionTool.getField(ItemStack.class, "capabilities");

    protected static final Method
            WORLD_IS_CHUNK_LOADED_METHOD = ReflectionTool.getMethod(World.class, "func_175680_a", "isChunkLoaded");

    protected static Field languageManagerCurrentLocaleField, localePropertiesField;
    protected static boolean host = false;


    static
    {
        if (FantasticLib.isClient)
        {
            languageManagerCurrentLocaleField = ReflectionTool.getField(LanguageManager.class, "field_135049_a", "CURRENT_LOCALE");
            localePropertiesField = ReflectionTool.getField(Locale.class, "field_135032_a", "properties");
        }
    }


    public static void removeEntityImmediate(Entity entity)
    {
        entity.setDead();

        World world = entity.world;
        if (world == null) return;


        if (!world.isRemote) Network.WRAPPER.sendToAllTracking(new Network.RemoveEntityImmediatePacket(entity), entity);

        int chunkX = entity.chunkCoordX;
        int chunkZ = entity.chunkCoordZ;

        if (entity.addedToChunk && (boolean) ReflectionTool.invoke(WORLD_IS_CHUNK_LOADED_METHOD, world, chunkX, chunkZ, true))
        {
            world.getChunkFromChunkCoords(chunkX, chunkZ).removeEntity(entity);
        }

        world.loadedEntityList.remove(entity);
        world.onEntityRemoved(entity);
    }


    public static String getAttributeModString(IAttribute attribute, AttributeModifier mod)
    {
        return getAttributeModString(attribute.getName(), mod.getAmount(), mod.getOperation());
    }

    public static String getAttributeModString(IAttribute attribute, double amount, int operation)
    {
        return getAttributeModString(attribute.getName(), amount, operation);
    }

    public static String getAttributeModString(String attributeName, AttributeModifier mod)
    {
        return getAttributeModString(attributeName, mod.getAmount(), mod.getOperation());
    }

    public static String getAttributeModString(String attributeName, double amount, int operation)
    {
        boolean isGoodAttribute = isGoodAttribute(attributeName);
        if (operation == 0) return (getColorAndSign(isGoodAttribute, amount, operation) + Tools.formatNicely(Math.abs(amount)) + " " + TextFormatting.GRAY + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 1) return (getColorAndSign(isGoodAttribute, amount, operation) + Tools.formatNicely(Math.abs(amount) * 100) + "% " + TextFormatting.GRAY + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        if (operation == 2) return (getColorAndSign(isGoodAttribute, amount, operation) + Tools.formatNicely(Math.abs(amount)) + "x " + TextFormatting.GRAY + I18n.translateToLocal("attribute.name." + attributeName)).replaceAll("[.]0([^0-9])", "$1");
        throw new IllegalArgumentException("Unknown mod attribute operation: " + operation);
    }

    public static String getColorAndSign(boolean isGoodAttribute, double amount, int operation)
    {
        String sign;
        TextFormatting color;

        if (operation == 2)
        {
            sign = amount < 0 ? "-" : "";
            color = (amount < 1) == isGoodAttribute ? TextFormatting.RED : TextFormatting.GREEN;
        }
        else
        {
            sign = amount < 0 ? "-" : "+";
            color = (amount < 0) == isGoodAttribute ? TextFormatting.RED : TextFormatting.GREEN;
        }

        return color + sign;
    }

    public static boolean isGoodAttribute(IAttribute attribute)
    {
        return isGoodAttribute(attribute.getName());
    }

    public static boolean isGoodAttribute(String attributeName)
    {
        return !Tools.contains(FantasticConfig.negativeAttributes, attributeName);
    }


    public static void trackFirstEvents()
    {
        FirstTimeEventMessenger.init();
    }


    public static boolean isClient()
    {
        return FantasticLib.isClient;
    }


    public static Entity getValidEntityByID(int id)
    {
        for (Entity entity : validEntities()) if (entity.getEntityId() == id) return entity;
        return null;
    }

    public static ArrayList<Entity> validEntities()
    {
        ArrayList<Entity> result = new ArrayList<>();
        for (World world : validWorlds()) result.addAll(world.loadedEntityList);
        return result;
    }

    public static ArrayList<World> validWorlds()
    {
        return new ArrayList<>(DIMENSION_MANAGER_WORLDS.values());
    }

    public static boolean entityIsValid(Entity entity)
    {
        if (entity.isDead || entity.world == null || !worldIsValid((WorldServer) entity.world)) return false;
        return entity.world.loadedEntityList.contains(entity);
    }

    public static boolean worldIsValid(WorldServer world)
    {
        return DIMENSION_MANAGER_WORLDS.containsValue(world);
    }


    public static VectorN getVectorN(Vec3d vec3d)
    {
        return new VectorN(vec3d.x, vec3d.y, vec3d.z);
    }

    public static Vec3d getVec3d(VectorN vectorN)
    {
        return new Vec3d(vectorN.values[0], vectorN.values[1], vectorN.values[2]);
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
        return Tools.getJarResourceReader(classInJar, resourcePathAndName);
    }

    public static InputStream getJarResourceStream(Class classInJar, String resourcePathAndName)
    {
        return Tools.getJarResourceStream(classInJar, resourcePathAndName);
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


    public static NBTBase removeSubNBTAndClean(NBTTagCompound compound, String... keys)
    {
        if (keys.length == 0) return null;


        NBTBase result;
        String key = keys[0];
        if (keys.length == 1)
        {
            result = compound.getTag(key);
            compound.removeTag(key);
            return result;
        }


        if (!compound.hasKey(key)) return null;


        String[] newKeys = new String[keys.length - 1];
        System.arraycopy(keys, 1, newKeys, 0, newKeys.length);
        NBTTagCompound subCompound = compound.getCompoundTag(key);
        result = removeSubNBTAndClean(subCompound, newKeys);
        if (subCompound.getKeySet().size() == 0) compound.removeTag(key);
        return result;
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
        boolean string = false;
        for (int i = 0; i < chars.length; i++)
        {
            char c = chars[i];
            switch (c)
            {
                case '"':
                    string = !string;
                    current.append(c);
                    break;
                case '{':
                case '[':
                    if (string)
                    {
                        current.append(c);
                        break;
                    }
                    if (!current.toString().equals("")) result.add(indent + current.toString());
                    result.add(indent.toString() + c);
                    current = new StringBuilder();
                    indent.append(" ");
                    break;

                case '}':
                case ']':
                    if (string)
                    {
                        current.append(c);
                        break;
                    }
                    if (!current.toString().equals("")) result.add(indent + current.toString());
                    indent = new StringBuilder(indent.substring(0, indent.length() - 1));
                    result.add(indent.toString() + c + (i + 1 < chars.length && chars[i + 1] == ',' ? ',' : ""));
                    current = new StringBuilder();
                    break;

                case ',':
                    if (string)
                    {
                        current.append(c);
                        break;
                    }
                    if (!current.toString().equals("")) result.add(indent + current.toString() + c);
                    current = new StringBuilder();
                    break;

                default:
                    current.append(c);
            }
        }

        return result;
    }


    public static void destroyItemStack(ItemStack stack)
    {
        stack.deserializeNBT(new NBTTagCompound());
        stack.setCount(0);
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

    //The difference between this and vanilla is that this one triggers the AttachCapabilitiesEvent AFTER initializing NBT
    public static ItemStack cloneItemStack(ItemStack stack)
    {
        NBTTagCompound result = new NBTTagCompound();

        ResourceLocation resourcelocation = Item.REGISTRY.getNameForObject(stack.getItem());
        result.setString("id", resourcelocation == null ? "minecraft:air" : resourcelocation.toString());
        result.setByte("Count", (byte) stack.getCount());
        result.setShort("Damage", (short) stack.getItemDamage());

        NBTTagCompound compound = stack.getTagCompound();
        if (compound != null) result.setTag("tag", compound.copy());

        CapabilityDispatcher capabilities = (CapabilityDispatcher) ReflectionTool.get(ITEMSTACK_CAPABILITIES_FIELD, stack);
        if (capabilities != null)
        {
            NBTTagCompound cnbt = capabilities.serializeNBT();
            if (!cnbt.hasNoTags()) result.setTag("ForgeCaps", cnbt);
        }

        return new ItemStack(result);
    }


    public static void give(EntityPlayer player, ItemStack stack)
    {
        give(player, stack, player.getName());
    }

    public static void give(EntityPlayer player, ItemStack stack, String owner)
    {
        EntityItem entityitem = player.dropItem(stack, false);

        if (entityitem != null)
        {
            entityitem.setNoPickupDelay();
            entityitem.setOwner(owner);
            entityitem.onCollideWithPlayer(player);

            if (!entityitem.isDead)
            {
                //If the item was not (fully) picked up
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
        return Tools.rotatedQuaternion(v, axis, theta);
    }


    public static EntitySnowball spawnDebugSnowball(World world, Vec3d position)
    {
        return spawnDebugSnowball(world, position.x, position.y, position.z);
    }

    public static EntitySnowball spawnDebugSnowball(World world, double x, double y, double z)
    {
        EntitySnowball snowball = new EntitySnowball(world, x, y, z);
        snowball.setVelocity(0, 0, 0);
        snowball.setNoGravity(true);
        world.spawnEntity(snowball);
        return snowball;
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


    public static void playSimpleSoundForAll(ResourceLocation rl)
    {
        playSimpleSoundForSpecific(rl, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, Entity entity)
    {
        playSimpleSoundForSpecific(rl, entity, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, Entity entity, double maxDistance)
    {
        playSimpleSoundForSpecific(rl, entity, maxDistance, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, Entity entity, double maxDistance, int attenuationType, float volume, float pitch)
    {
        playSimpleSoundForSpecific(rl, entity, maxDistance, attenuationType, volume, pitch, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, Entity entity, double maxDistance, int attenuationType, float volume, float pitch, SoundCategory soundCategory)
    {
        playSimpleSoundForSpecific(rl, entity, maxDistance, attenuationType, volume, pitch, soundCategory, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, int dimension, double x, double y, double z)
    {
        playSimpleSoundForSpecific(rl, dimension, x, y, z, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance)
    {
        playSimpleSoundForSpecific(rl, dimension, x, y, z, maxDistance, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, int attenuationType, float volume, float pitch)
    {
        playSimpleSoundForSpecific(rl, dimension, x, y, z, maxDistance, attenuationType, volume, pitch, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }

    public static void playSimpleSoundForAll(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, int attenuationType, float volume, float pitch, SoundCategory soundCategory)
    {
        playSimpleSoundForSpecific(rl, dimension, x, y, z, maxDistance, attenuationType, volume, pitch, soundCategory, FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers().toArray(new EntityPlayerMP[0]));
    }


    public static void playSimpleSoundForSpecific(ResourceLocation rl, EntityPlayerMP... players)
    {
        for (EntityPlayerMP player : players) Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl), player);
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, Entity following, EntityPlayerMP... players)
    {
        playSimpleSoundForSpecific(rl, following, 16, players);
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, Entity following, double maxDistance, EntityPlayerMP... players)
    {
        double maxDistSquared = maxDistance * maxDistance;
        Vec3d pos = following == null ? null : new Vec3d(following.posX, following.posY, following.posZ);

        for (EntityPlayerMP player : players)
        {
            if (following == null || (player.dimension == following.dimension && player.getPositionVector().squareDistanceTo(pos) < maxDistSquared))
            {
                Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl, following), player);
            }
        }
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, Entity following, double maxDistance, int attenuationType, float volume, float pitch, EntityPlayerMP... players)
    {
        double maxDistSquared = maxDistance * maxDistance;
        Vec3d pos = new Vec3d(following.posX, following.posY, following.posZ);

        for (EntityPlayerMP player : players)
        {
            if (player.dimension == following.dimension && player.getPositionVector().squareDistanceTo(pos) < maxDistSquared)
            {
                Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl, following, attenuationType, volume, pitch), player);
            }
        }
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, Entity following, double maxDistance, int attenuationType, float volume, float pitch, SoundCategory soundCategory, EntityPlayerMP... players)
    {
        double maxDistSquared = maxDistance * maxDistance;
        Vec3d pos = new Vec3d(following.posX, following.posY, following.posZ);

        for (EntityPlayerMP player : players)
        {
            if (player.dimension == following.dimension && player.getPositionVector().squareDistanceTo(pos) < maxDistSquared)
            {
                Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl, following, attenuationType, volume, pitch, soundCategory), player);
            }
        }
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, int dimension, double x, double y, double z, EntityPlayerMP... players)
    {
        playSimpleSoundForSpecific(rl, dimension, x, y, z, 16, players);
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, EntityPlayerMP... players)
    {
        double maxDistSquared = maxDistance * maxDistance;
        Vec3d pos = new Vec3d(x, y, z);

        for (EntityPlayerMP player : players)
        {
            if (player.dimension == dimension && player.getPositionVector().squareDistanceTo(pos) < maxDistSquared)
            {
                Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl, (float) x, (float) y, (float) z), player);
            }
        }
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, int attenuationType, float volume, float pitch, EntityPlayerMP... players)
    {
        double maxDistSquared = maxDistance * maxDistance;
        Vec3d pos = new Vec3d(x, y, z);

        for (EntityPlayerMP player : players)
        {
            if (player.dimension == dimension && player.getPositionVector().squareDistanceTo(pos) < maxDistSquared)
            {
                Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl, (float) x, (float) y, (float) z, attenuationType, volume, pitch), player);
            }
        }
    }

    public static void playSimpleSoundForSpecific(ResourceLocation rl, int dimension, double x, double y, double z, double maxDistance, int attenuationType, float volume, float pitch, SoundCategory soundCategory, EntityPlayerMP... players)
    {
        double maxDistSquared = maxDistance * maxDistance;
        Vec3d pos = new Vec3d(x, y, z);

        for (EntityPlayerMP player : players)
        {
            if (player.dimension == dimension && player.getPositionVector().squareDistanceTo(pos) < maxDistSquared)
            {
                Network.WRAPPER.sendTo(new Network.PlaySimpleSoundPacket(rl, (float) x, (float) y, (float) z, attenuationType, volume, pitch, soundCategory), player);
            }
        }
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

    public static double entityMatchesDoubleMapOrDefault(Entity entity, LinkedHashMap<Class<? extends Entity>, LinkedHashMap<String, Double>> populatedMap, double defaultValue)
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


    public static void populateEntityIntMap(String[] regexArray, LinkedHashMap<Class<? extends Entity>, LinkedHashMap<String, Integer>> mapToPopulate)
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

                    mapToPopulate.computeIfAbsent(entry.getValue().getEntityClass(), o -> new LinkedHashMap<>()).put(specificName, value);
                }
            }
        }
    }

    public static int entityMatchesIntMapOrDefault(Entity entity, LinkedHashMap<Class<? extends Entity>, LinkedHashMap<String, Integer>> populatedMap, int defaultValue)
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


    public static void populateEntityMap(String[] regexArray, LinkedHashMap<Class<? extends Entity>, HashSet<String>> mapToPopulate)
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

                    mapToPopulate.computeIfAbsent(entry.getValue().getEntityClass(), o -> new HashSet<>()).add(specificName);
                }
            }
        }
    }

    public static boolean entityMatchesMap(Entity entity, LinkedHashMap<Class<? extends Entity>, HashSet<String>> populatedMap)
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
        return ((Map<String, Configuration>) CONFIG_MANAGER_CONFIGS_FIELD.get(null)).get(getConfigDir() + modid + ".cfg");
    }

    public static void saveConfig(String modid) throws IllegalAccessException
    {
        Configuration config = getConfig(modid);
        ReflectionTool.set(CONFIGURATION_CHANGED_FIELD, config, true);
        ConfigManager.sync(modid, Config.Type.INSTANCE);
        config.save();
    }

    public static void reloadConfig(String modid) throws IllegalAccessException
    {
        //TODO This wipes config tooltips and doesn't fully load configs
        reloadConfig(getConfigDir() + modid + ".cfg", modid);
    }

    public static void reloadConfig(String configFilename, String modid) throws IllegalAccessException
    {
        //TODO This wipes config tooltips and doesn't fully load configs
        ((Map<String, Configuration>) CONFIG_MANAGER_CONFIGS_FIELD.get(null)).remove(configFilename);
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

    public static void crash(Exception e, boolean hardExit)
    {
        crash(e, 700, hardExit);
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
            str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getSimpleName();
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

            str = ai.getClass().getName();
            if (str.equals("")) str = ai.getClass().getSimpleName();
            if (str.equals("")) str = ai.getClass().getPackage().getName() + ".???????";

            result.add(priority + "\t" + (isNPE ? "NpeAttackTargetTaskHolder (" + str + ")" : str));
        }
        result.add("===================================");
        result.add("");

        return result;
    }
}
