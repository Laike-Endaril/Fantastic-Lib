package com.fantasticsource.mctools.aw;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.Network;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;
import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class RenderModes
{
    public static void init()
    {
        FLibAPI.attachNBTCapToEntityIf(MODID, entity -> true);
        MinecraftForge.EVENT_BUS.register(RenderModes.class);
    }


    public static void setRenderMode(Entity entity, String renderModeChannel, String renderMode)
    {
        SetRenderModeEvent event = new SetRenderModeEvent.Pre(entity, renderModeChannel, renderMode);
        if (MinecraftForge.EVENT_BUS.post(event)) return;


        renderModeChannel = event.renderModeChannel;
        renderMode = event.renderMode;

        if (renderMode == null)
        {
            clearRenderMode(entity, renderModeChannel);
            return;
        }


        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) compound.setTag("renderModes", new NBTTagCompound());
        compound = compound.getCompoundTag("renderModes");

        compound.setString(renderModeChannel, renderMode);

        if (entity instanceof EntityPlayerMP) Network.WRAPPER.sendTo(new Network.RenderModesPacket(entity), (EntityPlayerMP) entity);

        refresh(entity);


        if (MinecraftForge.EVENT_BUS.post(new SetRenderModeEvent.Post(entity, renderModeChannel, renderMode))) return;
    }

    public static String getRenderMode(Entity entity, String renderModeChannel)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) return null;

        compound = compound.getCompoundTag("renderModes");
        if (!compound.hasKey(renderModeChannel)) return null;

        return compound.getString(renderModeChannel);
    }

    public static LinkedHashMap<String, String> getRenderModes(Entity entity)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) return null;

        LinkedHashMap<String, String> result = new LinkedHashMap<>();
        compound = compound.getCompoundTag("renderModes");
        for (String key : compound.getKeySet())
        {
            result.put(key, compound.getString(key));
        }
        return result;
    }

    public static void clearRenderMode(Entity entity, String renderModeChannel)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) return;

        compound = compound.getCompoundTag("renderModes");
        compound.removeTag(renderModeChannel);
    }

    public static void clearRenderModes(Entity entity)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);
        compound.removeTag("renderModes");
    }


    public static ItemStack addRenderModeToSkin(ItemStack skinStack, ArrayList<Pair<String, String>> renderChannelAndModeRequirementPairs, String skinType, String libraryFile, LinkedHashMap<Integer, Color> dyes)
    {
        NBTTagCompound identifier = new NBTTagCompound();
        identifier.setString("skinType", skinType);
        identifier.setString("libraryFile", libraryFile);


        NBTTagCompound dyeData = new NBTTagCompound();
        for (Map.Entry<Integer, Color> entry : dyes.entrySet())
        {
            int i = entry.getKey();
            Color dye = entry.getValue();

            dyeData.setByte("dye" + i + "r", (byte) dye.r());
            dyeData.setByte("dye" + i + "g", (byte) dye.g());
            dyeData.setByte("dye" + i + "b", (byte) dye.b());
            dyeData.setByte("dye" + i + "t", (byte) dye.a());
        }


        if (!skinStack.hasTagCompound()) skinStack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = skinStack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("renderModes")) compound.setTag("renderModes", new NBTTagCompound());
        compound = compound.getCompoundTag("renderModes");

        if (renderChannelAndModeRequirementPairs == null || renderChannelAndModeRequirementPairs.size() == 0)
        {
            compound.setTag("default", new NBTTagCompound());
            compound = compound.getCompoundTag("default");

            compound.setTag("identifier", identifier);
            compound.setTag("dyeData", dyeData);
        }
        else
        {
            StringBuilder key = new StringBuilder();
            boolean start = true;
            for (Pair<String, String> requirement : renderChannelAndModeRequirementPairs)
            {
                key.append(start ? "" : ",").append(requirement.getKey()).append(":").append(requirement.getValue());
                start = false;
            }

            compound.setTag(key.toString(), new NBTTagCompound());
            compound = compound.getCompoundTag(key.toString());

            compound.setTag("identifier", identifier);
            compound.setTag("dyeData", dyeData);
        }


        return skinStack;
    }


    @SubscribeEvent(priority = EventPriority.LOW)
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        refresh(event.getEntity());
    }

    public static void refresh(Entity entity)
    {
        if (entity.world.isRemote) return;

        boolean changed = false;
        for (ItemStack stack : GlobalInventory.getAWSkins(entity))
        {
            changed |= tryTransformRenderMode(stack, entity);
        }

        if (changed) GlobalInventory.syncAWWardrobeSkins(entity, true, true);
    }


    protected static boolean tryTransformRenderMode(ItemStack stack, Entity target)
    {
        return tryTransformRenderMode(stack, target, new HashMap<>());
    }

    protected static boolean tryTransformRenderMode(ItemStack stack, Entity target, HashMap<String, String> overrides)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("renderModes")) return false;

        compound = compound.getCompoundTag("renderModes");

        int conditionsMet = 0;
        NBTTagCompound newCompound = null;
        for (String requirements : compound.getKeySet())
        {
            if (requirements.equals("default"))
            {
                if (conditionsMet == 0) newCompound = compound.getCompoundTag("default");
                continue;
            }


            boolean failed = false;
            String[] requirementArray = Tools.fixedSplit(requirements, ",");
            if (requirementArray.length <= conditionsMet) continue;


            for (String pair : requirementArray)
            {
                String[] tokens = Tools.fixedSplit(pair, ":");
                String foundMode = overrides.get(tokens[0]);
                if (foundMode == null) foundMode = getRenderMode(target, tokens[0]);
                if (!tokens[1].equals(foundMode))
                {
                    failed = true;
                    break;
                }
            }

            if (failed) continue;


            conditionsMet = requirementArray.length;
            newCompound = compound.getCompoundTag(requirements);
        }

        if (newCompound == null) return removeSkin(stack);
        return setSkin(stack, newCompound);
    }


    protected static boolean removeSkin(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (compound.hasKey("armourersWorkshop"))
        {
            stack.getTagCompound().removeTag("armourersWorkshop");
            return true;
        }

        return false;
    }

    protected static boolean setSkin(ItemStack stack, NBTTagCompound skinInfo)
    {
        if (skinInfo == null || !skinInfo.hasKey("identifier")) return removeSkin(stack);


        boolean changed = false;

        if (!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
            changed = true;
        }
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey("armourersWorkshop"))
        {
            compound.setTag("armourersWorkshop", new NBTTagCompound());
            changed = true;
        }
        compound = compound.getCompoundTag("armourersWorkshop");


        NBTTagCompound infoCompound = skinInfo.getCompoundTag("identifier");
        NBTTagCompound newCompound = new NBTTagCompound();
        newCompound.setString("skinType", infoCompound.getString("skinType"));
        newCompound.setString("libraryFile", infoCompound.getString("libraryFile"));

        if (!compound.hasKey("identifier") || !compound.getCompoundTag("identifier").equals(newCompound)) changed = true;
        compound.setTag("identifier", newCompound);


        infoCompound = skinInfo.getCompoundTag("dyeData");
        newCompound = new NBTTagCompound();
        for (String key : infoCompound.getKeySet())
        {
            newCompound.setByte(key, infoCompound.getByte(key));
        }

        if (!compound.hasKey("dyeData") || !compound.getCompoundTag("dyeData").equals(newCompound)) changed = true;
        compound.setTag("dyeData", newCompound);

        return changed;
    }


    @SubscribeEvent
    public static void playerLogin(PlayerEvent.PlayerLoggedInEvent event)
    {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        Network.WRAPPER.sendTo(new Network.RenderModesPacket(player), player);
    }
}
