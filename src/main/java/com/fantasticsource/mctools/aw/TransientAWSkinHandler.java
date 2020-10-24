package com.fantasticsource.mctools.aw;

import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class TransientAWSkinHandler
{
    @GameRegistry.ObjectHolder("armourers_workshop:item.skin")
    public static Item awSkinItem;

    public static void addTransientAWSkin(ItemStack equipmentStack, String skinType, int indexWithinSkinType, ItemStack skinStack)
    {
        if (!equipmentStack.hasTagCompound()) equipmentStack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = equipmentStack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        if (!compound.hasKey("AWSkins")) compound.setTag("AWSkins", new NBTTagList());
        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        compound = new NBTTagCompound();
        compound.setString("type", skinType);
        compound.setInteger("index", indexWithinSkinType);
        compound.setTag("skinCompound", skinStack.serializeNBT());
        list.appendTag(compound);
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
        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setBoolean("AWTransient", true);
    }

    public static boolean isTransientSkin(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return false;
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) return false;
        return compound.getCompoundTag(DOMAIN).getBoolean("AWTransient");
    }


    //The methods below are the core part of adding/removing transient skin ItemStacks to/from the AW wardrobe
    public static boolean tryApplyTransientSkinsFromStack(ItemStack stack, Entity target)
    {
        if (!stack.hasTagCompound()) return false;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return false;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("AWSkins")) return false;


        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        ItemStack newSkin, oldSkin;
        for (int i = 0; i < list.tagCount(); i++)
        {
            compound = list.getCompoundTagAt(i);


            String skinType = compound.getString("type");
            int index = compound.getInteger("index");

            oldSkin = GlobalInventory.getAWSkin(target, skinType, index);
            if (!oldSkin.isEmpty() && !isTransientSkin(oldSkin))
            {
                System.err.println(TextFormatting.RED + "Failed to place skin into slot: " + skinType + " (" + index + ")");
                System.err.println(TextFormatting.RED + "Skin: " + stack.getDisplayName());
                System.err.println(TextFormatting.RED + "Entity: " + target.getDisplayName() + " in world " + target.dimension + " (" + target.getPosition() + ")");
                return false;
            }

            newSkin = new ItemStack(compound.getCompoundTag("skinCompound"));
            applyTransientTag(newSkin);
            GlobalInventory.setAWSkin(target, compound.getString("type"), compound.getInteger("index"), newSkin);
        }
        return list.tagCount() > 0;
    }

    public static boolean removeAllTransientSkins(Entity entity)
    {
        boolean changed = false;
        for (ItemStack skin : GlobalInventory.getAWSkins(entity))
        {
            if (isTransientSkin(skin))
            {
                skin.setTagCompound(null);
                skin.setCount(0);
                changed = true;
            }
        }
        return changed;
    }


    @SubscribeEvent
    public static void inventoryChanged(InventoryChangedEvent event)
    {
        Entity entity = event.getEntity();
        if (entity instanceof EntityPlayer) refresh((EntityPlayer) entity);
    }


    public static void refresh(EntityPlayer player)
    {
        boolean changed = removeAllTransientSkins(player);

        for (ItemStack stack : GlobalInventory.getValidEquippedItems(player))
        {
            changed |= tryApplyTransientSkinsFromStack(stack, player);
        }

        if (changed) GlobalInventory.syncAWWardrobeSkins(player, true, true);
    }
}
