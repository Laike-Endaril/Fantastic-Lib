package com.fantasticsource.mctools.aw;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;
import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class RenderModes
{
    public static void init()
    {
        FLibAPI.attachNBTCapToEntityIf(MODID, entity -> true);
    }


    public static void setRenderMode(Entity entity, String renderModeChannel, String renderMode)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) compound.setTag("renderModes", new NBTTagCompound());
        compound = compound.getCompoundTag("renderModes");

        compound.setString(renderModeChannel, renderMode);

        refresh(entity);
    }

    public static String getRenderMode(Entity entity, String renderModeChannel)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) return null;

        compound = compound.getCompoundTag("renderModes");
        if (!compound.hasKey(renderModeChannel)) return null;

        return compound.getString(renderModeChannel);
    }


    public static ItemStack addRenderModeToSkin(ItemStack skinStack, ArrayList<Pair<String, String>> renderChannelAndModeRequirementPairs, String skinType, String libraryFile, Color... dyes)
    {
        NBTTagCompound identifier = new NBTTagCompound();
        identifier.setString("skinType", skinType);
        identifier.setString("libraryFile", libraryFile);


        NBTTagCompound dyeData = new NBTTagCompound();
        int i = 0;
        for (Color dye : dyes)
        {
            dyeData.setByte("dye" + i + "r", (byte) dye.r());
            dyeData.setByte("dye" + i + "g", (byte) dye.g());
            dyeData.setByte("dye" + i + "b", (byte) dye.b());
            dyeData.setByte("dye" + i + "t", (byte) dye.a());
            i++;
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
        if (entity instanceof EntityPlayer)
        {
            System.out.println("Test");
        }

        for (ItemStack stack : GlobalInventory.getAWSkins(entity))
        {
            tryTransformRenderMode(stack, entity);
        }

        GlobalInventory.syncAWWardrobeSkins(entity, true, true);
    }


    protected static void tryTransformRenderMode(ItemStack stack, Entity target)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("renderModes")) return;

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
                if (!tokens[1].equals(getRenderMode(target, tokens[0])))
                {
                    failed = true;
                    break;
                }
            }

            if (failed) continue;


            conditionsMet = requirementArray.length;
            newCompound = compound.getCompoundTag(requirements);
        }

        if (newCompound == null) removeSkin(stack);
        else setSkin(stack, newCompound);
    }


    protected static void removeSkin(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;
        stack.getTagCompound().removeTag("armourersWorkshop");
    }

    protected static void setSkin(ItemStack stack, NBTTagCompound skinInfo)
    {
        if (skinInfo == null || !skinInfo.hasKey("identifier"))
        {
            removeSkin(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey("armourersWorkshop")) compound.setTag("armourersWorkshop", new NBTTagCompound());
        compound = compound.getCompoundTag("armourersWorkshop");


        NBTTagCompound infoCompound = skinInfo.getCompoundTag("identifier");
        NBTTagCompound stackCompound = new NBTTagCompound();
        stackCompound.setString("skinType", infoCompound.getString("skinType"));
        stackCompound.setString("libraryFile", infoCompound.getString("libraryFile"));
        compound.setTag("identifier", stackCompound);


        infoCompound = skinInfo.getCompoundTag("dyeData");
        stackCompound = new NBTTagCompound();
        for (String key : infoCompound.getKeySet())
        {
            stackCompound.setByte(key, infoCompound.getByte(key));
        }
        compound.setTag("dyeData", stackCompound);
    }
}
