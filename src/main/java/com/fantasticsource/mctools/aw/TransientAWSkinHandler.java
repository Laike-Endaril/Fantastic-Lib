package com.fantasticsource.mctools.aw;

import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
    @GameRegistry.ObjectHolder("armourers_workshop:skin")
    public static Item awSkinItem;

    public static void addTransientAWSkin(ItemStack stack, String libraryFile, String skinType, Color... dyes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("AWSkins")) compound.setTag("AWSkins", new NBTTagList());
        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        list.appendTag(compound);

        compound.setString("file", libraryFile);
        compound.setString("type", skinType);

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


    private static void applyNBTToTransientSkin(ItemStack stack, String libraryFile, String skinType, Color... dyes)
    {
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();


        //Mark as "transient"
        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setBoolean("awTransient", true);


        //Apply normal AW skin NBT
        compound = new NBTTagCompound();
        stack.getTagCompound().setTag("armourersWorkshop", compound);

        NBTTagCompound compound2 = new NBTTagCompound();
        compound.setTag("identifier", compound2);

        compound2.setString("libraryFile", libraryFile);
        compound2.setString("skinType", skinType);

        compound2 = new NBTTagCompound();
        compound.setTag("dyeData", compound2);

        int i = 0;
        for (Color color : dyes)
        {
            compound2.setByte("dye" + i + "r", (byte) color.r());
            compound2.setByte("dye" + i + "g", (byte) color.g());
            compound2.setByte("dye" + i + "b", (byte) color.b());
            compound2.setByte("dye" + i + "t", (byte) color.a());
            i++;
        }
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

        ItemStack skin;
        NBTTagList list2;
        ArrayList<Color> dyes;
        for (int i = 0; i < list.tagCount(); i++)
        {
            compound = list.getCompoundTagAt(i);
            dyes = new ArrayList<>();
            if (compound.hasKey("dyes"))
            {
                list2 = compound.getTagList("dyes", Constants.NBT.TAG_INT);
                for (int i2 = 0; i2 < list2.tagCount(); i2++)
                {
                    dyes.add(new Color(list.getIntAt(i2)));
                }
            }

            skin = new ItemStack(awSkinItem);
            applyNBTToTransientSkin(skin, compound.getString("libraryFile"), compound.getString("skinType"), dyes.toArray(new Color[0]));


            //TODO search slots of correct type, searching for an empty one while tracking ones filled with transient items.  Fill empty slot if possible, replace other transient as a fallback, and do nothing otherwise
        }
    }

    public static void tryAddAllTransientSkins(Entity entity)
    {
        //TODO
    }

    public static void removeAllTransientSkins(Entity entity)
    {
        //TODO
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        EntityLivingBase livingBase = event.getEntityLiving();
        removeAllTransientSkins(livingBase);
        //TODO loop through all items and apply transient skins if they meet "activated" criteria
    }
}
