package com.fantasticsource.mctools.aw;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.GlobalInventory;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Slottings;
import com.fantasticsource.mctools.event.InventoryChangedEvent;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.google.common.cache.Cache;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinDescriptor;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinIdentifier;
import moe.plushie.armourers_workshop.api.common.skin.data.ISkinProperty;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinPartTypeTextured;
import moe.plushie.armourers_workshop.client.skin.cache.ClientSkinCache;
import moe.plushie.armourers_workshop.common.painting.PaintTypeRegistry;
import moe.plushie.armourers_workshop.common.painting.PaintingHelper;
import moe.plushie.armourers_workshop.common.skin.data.*;
import moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry;
import moe.plushie.armourers_workshop.utils.SkinNBTHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

import static com.fantasticsource.fantasticlib.FantasticLib.DOMAIN;

public class TransientAWSkinHandler
{
    protected static Field
            ITEM_TOOLTIP_EVENT_ITEMSTACK_FIELD = MCTools.isClient() ? ReflectionTool.getField(ItemTooltipEvent.class, "itemStack") : null,
            CLIENT_SKIN_CACHE_SKIN_CACHE_FIELD = MCTools.isClient() ? ReflectionTool.getField(ClientSkinCache.class, "skinCache") : null;

    protected static Cache<ISkinIdentifier, Skin> skinCache = CLIENT_SKIN_CACHE_SKIN_CACHE_FIELD == null ? null : (Cache<ISkinIdentifier, Skin>) ReflectionTool.get(CLIENT_SKIN_CACHE_SKIN_CACHE_FIELD, ClientSkinCache.INSTANCE);
    protected static ItemStack oldTooltipStack;


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
        if (entity instanceof EntityPlayerMP) refresh((EntityPlayerMP) entity);
    }


    public static void refresh(EntityPlayerMP player)
    {
        boolean changed = removeAllTransientSkins(player);

        for (ItemStack stack : GlobalInventory.getValidEquippedItems(player))
        {
            changed |= tryApplyTransientSkinsFromStack(stack, player);
        }

        if (changed) GlobalInventory.syncAWWardrobeSkins(player, true, true);
    }


    public static ArrayList<ItemStack> getTransientSkins(ItemStack stack)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        if (!stack.hasTagCompound()) return result;

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return result;

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("AWSkins")) return result;


        NBTTagList list = compound.getTagList("AWSkins", Constants.NBT.TAG_COMPOUND);

        ItemStack newSkin;
        for (int i = 0; i < list.tagCount(); i++)
        {
            newSkin = new ItemStack(list.getCompoundTagAt(i).getCompoundTag("skinCompound"));
            applyTransientTag(newSkin);
            result.add(newSkin);
        }
        return result;
    }


    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOW)
    public static void preTooltip(ItemTooltipEvent event)
    {
        ItemStack newTooltipStack = getTransientSkinsOutfitWithRenderModes(event.getItemStack(), Minecraft.getMinecraft().player);
        if (newTooltipStack.isEmpty()) oldTooltipStack = null;
        else
        {
            oldTooltipStack = MCTools.cloneItemStack(event.getItemStack());
            ReflectionTool.set(ITEM_TOOLTIP_EVENT_ITEMSTACK_FIELD, event, newTooltipStack);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void postTooltip(ItemTooltipEvent event)
    {
        if (oldTooltipStack != null) ReflectionTool.set(ITEM_TOOLTIP_EVENT_ITEMSTACK_FIELD, event, oldTooltipStack);
    }

    @SideOnly(Side.CLIENT)
    public static ItemStack getTransientSkinsOutfitWithRenderModes(ItemStack stack, EntityPlayer player)
    {
        ArrayList<ItemStack> transientSkins = getTransientSkins(stack);
        if (transientSkins.size() == 0) return ItemStack.EMPTY;

        //Do render mode transforms and remove skins that end up empty
        HashMap<String, String> overrides = new HashMap<>();
        String slotting = Slottings.getItemSlotting(stack);
        for (String fullOverrideString : FantasticConfig.previewRenderModeOverrides)
        {
            String[] tokens = Tools.fixedSplit(fullOverrideString, ",");
            if (slotting.equals(tokens[0].trim()))
            {
                for (int i = 1; i < tokens.length; i++)
                {
                    String[] tokens2 = Tools.fixedSplit(tokens[i].trim(), "@");
                    overrides.put(tokens2[0], tokens2[1]);
                }
                break;
            }
        }
        transientSkins.removeIf(skin ->
        {
            RenderModes.tryTransformRenderMode(skin, player, overrides);
            return (!skin.getTagCompound().hasKey("armourersWorkshop"));
        });

        return makeClientOutfit(player, transientSkins);
    }

    @SideOnly(Side.CLIENT)
    protected static ItemStack makeClientOutfit(EntityPlayer player, ArrayList<ItemStack> skinStacks)
    {
        ArrayList<SkinPart> skinParts = new ArrayList<>();
        SkinProperties skinProperties = new SkinProperties();
        StringBuilder partIndices = new StringBuilder();
        int[] paintData = null;
        int skinIndex = 0;
        NBTTagCompound dyeCompound = null;
        for (ItemStack stack : skinStacks)
        {
            ISkinDescriptor descriptor = SkinNBTHelper.getSkinDescriptorFromStack(stack);
            if (descriptor == null) continue;

            Skin skin = ClientSkinCache.INSTANCE.getSkin(descriptor, true);
            if (skin == null) continue;


            //Apply dyes
            if (dyeCompound == null) dyeCompound = MCTools.getSubCompoundIfExists(stack.getTagCompound(), "armourersWorkshop", "dyeData");


            for (int partIndex = 0; partIndex < skin.getPartCount(); partIndex++)
            {
                SkinPart part = skin.getParts().get(partIndex);
                skinParts.add(part);
            }

            if (skin.hasPaintData())
            {
                if (paintData == null) paintData = new int[64 * 32];
                for (int partIndex = 0; partIndex < skin.getSkinType().getSkinParts().size(); partIndex++)
                {
                    ISkinPartType partType = skin.getSkinType().getSkinParts().get(partIndex);
                    if (partType instanceof ISkinPartTypeTextured)
                    {
                        paintPart(((ISkinPartTypeTextured) partType), paintData, skin.getPaintData());
                    }
                }
            }

            if (partIndices.length() == 0) partIndices.append(skinParts.size());
            else partIndices.append(":").append(skinParts.size());

            for (ISkinProperty prop : skin.getSkinType().getProperties())
            {
                SkinProperty<SkinProperty> p = (SkinProperty) prop;
                if (p.getKey().startsWith("wings")) p.setValue(skinProperties, p.getValue(skin.getProperties()), skinIndex);
                else p.setValue(skinProperties, p.getValue(skin.getProperties()));
            }
            skinIndex++;
        }
        if (skinParts.size() == 0) return ItemStack.EMPTY;


        SkinProperties.PROP_OUTFIT_PART_INDEXS.setValue(skinProperties, partIndices.toString());
        SkinProperties.PROP_ALL_AUTHOR_NAME.setValue(skinProperties, player.getName());
        if (player.getGameProfile() != null && player.getGameProfile().getId() != null) SkinProperties.PROP_ALL_AUTHOR_UUID.setValue(skinProperties, player.getGameProfile().getId().toString());
        SkinProperties.PROP_ALL_CUSTOM_NAME.setValue(skinProperties, "");
        SkinProperties.PROP_ALL_FLAVOUR_TEXT.setValue(skinProperties, "");
        Skin skin = new Skin(skinProperties, SkinTypeRegistry.skinOutfit, paintData, skinParts);
        try
        {
            skinCache.put(new SkinIdentifier(skin), skin);
            ItemStack outfit = SkinNBTHelper.makeEquipmentSkinStack(new SkinDescriptor(skin));
            if (dyeCompound != null) MCTools.getOrGenerateSubCompound(outfit.getTagCompound(), "armourersWorkshop").setTag("dyeData", dyeCompound);
            return outfit;
        }
        catch (NullPointerException e)
        {
            return ItemStack.EMPTY;
        }
    }

    protected static int[] paintPart(ISkinPartTypeTextured texType, int[] desPaint, int[] srcPaint)
    {
        Point pos = texType.getTextureSkinPos();
        int width = (texType.getTextureModelSize().getX() * 2) + (texType.getTextureModelSize().getZ() * 2);
        int height = texType.getTextureModelSize().getY() + texType.getTextureModelSize().getZ();

        for (int ix = 0; ix < width; ix++)
        {
            for (int iy = 0; iy < height; iy++)
            {
                int x = pos.x + ix;
                int y = pos.y + iy;
                byte[] rgbt = PaintingHelper.intToBytes(srcPaint[x + (y * 64)]);
                if ((rgbt[3] & 0xFF) != PaintTypeRegistry.PAINT_TYPE_NONE.getId())
                {
                    desPaint[x + (y * 64)] = srcPaint[x + (y * 64)];
                }
            }
        }

        return desPaint;
    }
}
