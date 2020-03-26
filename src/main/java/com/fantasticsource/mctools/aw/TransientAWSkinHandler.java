package com.fantasticsource.mctools.aw;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class TransientAWSkinHandler
{
    @GameRegistry.ObjectHolder("armourers_workshop:item.skin")
    public static Item awSkinItem;

    public static void addTransientAWSkin(ItemStack stack, String libraryFile, String skinType, Color... dyes)
    {
        addTransientAWSkin(stack, libraryFile, skinType, null, null, dyes);
    }

    public static void addTransientAWSkin(ItemStack stack, String libraryFile, String skinType, String renderChannel, String renderMode, Color... dyes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("AWSkins")) compound.setTag("AWSkins", new NBTTagList());
        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        list.appendTag(compound);

        compound.setString("file", libraryFile.replace(".armour", ""));
        compound.setString("type", skinType);
        if (renderChannel != null && renderMode != null)
        {
            compound.setString("renderChannel", renderChannel);
            compound.setString("renderMode", renderMode);
        }

        if (dyes.length > 0)
        {
            list = new NBTTagList();
            compound.setTag("dyes", list);

            for (Color dye : dyes)
            {
                list.appendTag(new NBTTagInt(dye.color()));
            }
        }
    }

    public static void clearTransientAWSkins(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;
        NBTTagCompound mainTag = stack.getTagCompound();

        if (!mainTag.hasKey(DOMAIN)) return;
        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);

        compound.removeTag("AWSkins");

        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }


    protected static void applyTransientTag(ItemStack stack)
    {
        //Mark as "transient"
        NBTTagCompound compound = stack.getTagCompound();

        compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setBoolean("awTransient", true);
    }

    public static boolean isTransientSkin(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) return false;
        return compound.getCompoundTag(DOMAIN).getBoolean("awTransient");
    }


    //These three methods are the core part of adding/removing transient skin ItemStacks to/from the AW wardrobe
    public static void tryApplyTransientSkinsFromStack(ItemStack stack, Entity target)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("AWSkins")) return;


        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        ItemStack newSkin, oldSkin;
        NBTTagList list2;
        ArrayList<Color> dyes;
        for (int i = 0; i < list.tagCount(); i++)
        {
            compound = list.getCompoundTagAt(i);

            //Only render if the render mode of the render channel matches the entity's current render mode for that render channel
            if (compound.hasKey("renderChannel"))
            {
                String targetRenderMode = RenderModes.getRenderMode(target, compound.getString("renderChannel"));
                if (targetRenderMode == null && compound.hasKey("renderMode")) continue;
                if (!compound.getString("renderMode").equals(targetRenderMode)) continue;
            }

            dyes = new ArrayList<>();
            if (compound.hasKey("dyes"))
            {
                list2 = compound.getTagList("dyes", Constants.NBT.TAG_INT);
                for (int i2 = 0; i2 < list2.tagCount(); i2++)
                {
                    dyes.add(new Color(list2.getIntAt(i2)));
                }
            }


            String skinType = compound.getString("type");
            int size = GlobalInventory.getAWSkinSlotCount(target, skinType);
            int transientSkinAt = -1;
            for (int i2 = 0; i2 < size; i2++)
            {
                oldSkin = GlobalInventory.getAWSkin(target, skinType, i2);
                if (oldSkin.isEmpty())
                {
                    newSkin = AWSkinGenerator.generate(compound.getString("file"), skinType, dyes.toArray(new Color[0]));
                    applyTransientTag(newSkin);
                    GlobalInventory.setAWSkin(target, skinType, i2, newSkin);
                    transientSkinAt = -1;
                    break;
                }
                else if (transientSkinAt == -1 && isTransientSkin(oldSkin))
                {
                    transientSkinAt = i2;
                }
            }

            if (transientSkinAt >= 0)
            {
                newSkin = AWSkinGenerator.generate(compound.getString("file"), skinType, dyes.toArray(new Color[0]));
                applyTransientTag(newSkin);
                GlobalInventory.setAWSkin(target, skinType, transientSkinAt, newSkin);
            }
        }
    }

    public static void removeAllTransientSkins(Entity entity)
    {
        for (ItemStack skin : GlobalInventory.getAWSkins(entity))
        {
            if (isTransientSkin(skin))
            {
                skin.setTagCompound(null);
                skin.setCount(0);
            }
        }
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        refresh(event.getEntity());
    }


    public static void refresh(Entity entity)
    {
        removeAllTransientSkins(entity);

        for (ItemStack stack : GlobalInventory.getAllEquippedItems(entity))
        {
            tryApplyTransientSkinsFromStack(stack, entity);
        }

        GlobalInventory.syncAWWardrobeSkins(entity, true, true);
    }
}
