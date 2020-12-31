package com.fantasticsource.mctools;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tiamatinventory.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatinventory.api.TiamatInventoryAPI;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class GlobalInventory
{
    public static ISkinTypeRegistry skinTypeRegistry = ArmourersWorkshopApi.skinTypeRegistry;


    public static ArrayList<ItemStack> getAllItems(Entity entity)
    {
        ArrayList<ItemStack> result = getAllNonSkinItems(entity);

        //Armourer's Workshop
        result.addAll(getAWSkins(entity));

        return result;
    }

    public static ArrayList<ItemStack> getAllNonSkinItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        //Vanilla
        result.addAll(getVanillaItems(entity));

        //Baubles
        result.addAll(getBaubles(entity));

        //Tiamat Inventory
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        if (inventory != null) result.addAll(inventory.getAllItems());

        return result;
    }

    public static ArrayList<ItemStack> getAllEquippedItems(Entity entity)
    {
        ArrayList<ItemStack> result = getAllEquippedNonSkinItems(entity);

        //Armourer's Workshop
        result.addAll(getAWSkins(entity));

        return result;
    }

    public static ArrayList<ItemStack> getAllEquippedNonSkinItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        //Vanilla
        ItemStack stack = getVanillaMainhandItem(entity);
        if (stack != null) result.add(stack);
        result.addAll(getVanillaOffhandItems(entity));
        result.addAll(getVanillaArmorItems(entity));

        //Baubles
        result.addAll(getBaubles(entity));

        //Tiamat Inventory
        ITiamatPlayerInventory tiamatInventory = getTiamatInventory(entity);
        if (tiamatInventory != null) result.addAll(tiamatInventory.getAllEquippedItems());

        return result;
    }

    public static LinkedHashMap<String, ArrayList<ItemStack>> getAllItemsCategorized(Entity entity)
    {
        LinkedHashMap<String, ArrayList<ItemStack>> result = new LinkedHashMap<>();
        ArrayList<ItemStack> list = new ArrayList<>();


        //Vanilla
        ItemStack stack = getVanillaMainhandItem(entity);
        if (stack != null)
        {
            list.add(stack);
            result.put("Vanilla Mainhand", list);
            list = new ArrayList<>();
        }

        list.addAll(getVanillaOffhandItems(entity));
        if (list.size() > 0)
        {
            result.put("Vanilla Offhands", list);
            list = new ArrayList<>();
        }

        list.addAll(getVanillaArmorItems(entity));
        if (list.size() > 0)
        {
            result.put("Vanilla Armor", list);
            list = new ArrayList<>();
        }

        list.addAll(getVanillaOtherInventoryItems(entity));
        if (list.size() > 0)
        {
            result.put("Vanilla Other Inventory", list);
            list = new ArrayList<>();
        }


        //Baubles
        list.addAll(getBaubles(entity));
        if (list.size() > 0)
        {
            result.put("Baubles Inventory", list);
            list = new ArrayList<>();
        }


        //Tiamat Inventory
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        if (inventory != null)
        {
            stack = getTiamatSheathedMainhand1(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Sheathed Mainhand 1", list);
                list = new ArrayList<>();
            }

            stack = getTiamatSheathedOffhand1(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Sheathed Offhand 1", list);
                list = new ArrayList<>();
            }

            stack = getTiamatSheathedMainhand2(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Sheathed Mainhand 2", list);
                list = new ArrayList<>();
            }

            stack = getTiamatSheathedOffhand2(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Sheathed Offhand 2", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatArmor(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Armor", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatQuickslots(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Quickslots", list);
                list = new ArrayList<>();
            }

            stack = getTiamatBackpack(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Backpack", list);
                list = new ArrayList<>();
            }

            stack = getTiamatPet(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Pet", list);
                list = new ArrayList<>();
            }

            stack = getTiamatDeck(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Deck", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatClasses(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Classes", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatOffensiveSkills(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Offensive Skills", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatUtilitySkills(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Utility Skills", list);
                list = new ArrayList<>();
            }

            stack = getTiamatUltimateSkill(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Ultimate Skill", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatPassiveSkills(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Passive Skills", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatGatheringProfessions(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Gathering Professions", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatCraftingProfessions(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Crafting Professions", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatRecipes(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Recipes", list);
                list = new ArrayList<>();
            }
        }


        //Armourer's Workshop
        list.addAll(getAWSkins(entity));
        if (list.size() > 0)
        {
            result.put("Armourer's Workshop Skins", list);
            list = new ArrayList<>();
        }


        return result;
    }

    public static ArrayList<ItemStack> getValidEquippedItems(EntityPlayer player)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        //Vanilla slots
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++)
        {
            ItemStack stack = player.inventory.getStackInSlot(slot);
            if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot, player)) continue;

            result.add(stack);
        }

        //Baubles slots
        if (Compat.baubles)
        {
            IBaublesItemHandler inventory = BaublesApi.getBaublesHandler(player);
            for (int slot = 0; slot < inventory.getSlots(); slot++)
            {
                ItemStack stack = inventory.getStackInSlot(slot);
                if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot + Slottings.BAUBLES_OFFSET, player)) continue;

                result.add(stack);
            }
        }

        //Tiamat slots
        if (Compat.tiamatinventory)
        {
            int slot = 0;
            for (ItemStack stack : GlobalInventory.getAllTiamatItems(player))
            {
                if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot++ + Slottings.TIAMAT_OFFSET, player)) continue;

                result.add(stack);
            }
        }

        return result;
    }

    public static String getItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey("tiamatrpg")) return "None";

        compound = compound.getCompoundTag("tiamatrpg");
        if (!compound.hasKey("slotting")) return "None";

        return compound.getString("slotting");
    }


    //Vanilla

    public static ArrayList<ItemStack> getVanillaItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        ItemStack stack = getVanillaMainhandItem(entity);
        if (stack != null) result.add(stack);
        result.addAll(getVanillaOffhandItems(entity));
        result.addAll(getVanillaArmorItems(entity));
        result.addAll(getVanillaOtherInventoryItems(entity));

        return result;
    }

    public static ItemStack getVanillaMainhandItem(Entity entity)
    {
        if (entity instanceof EntityLivingBase) return ((EntityLivingBase) entity).getHeldItemMainhand();
        for (ItemStack stack : entity.getHeldEquipment()) return stack;
        return null;
    }

    public static void setVanillaMainhandItem(Entity entity, ItemStack stack)
    {
        entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
    }

    public static ArrayList<ItemStack> getVanillaOffhandItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        ItemStack mainhand = getVanillaMainhandItem(entity);
        if (mainhand == ItemStack.EMPTY)
        {
            int emptiesFound = 0;
            for (ItemStack stack : entity.getHeldEquipment())
            {
                if (stack != ItemStack.EMPTY || ++emptiesFound > 1) result.add(stack);
            }
        }
        else for (ItemStack stack : entity.getHeldEquipment())
        {
            if (stack != mainhand && !result.contains(stack)) result.add(stack);
        }

        return result;
    }

    public static void setVanillaOffhandItem(Entity entity, ItemStack stack)
    {
        //Don't see a good way of being able to set indexed offhand items
        entity.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, stack);
    }

    public static ArrayList<ItemStack> getVanillaArmorItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        for (ItemStack stack : entity.getArmorInventoryList())
        {
            if (!result.contains(stack)) result.add(stack);
        }

        return result;
    }

    public static ItemStack getVanillaHeadItem(Entity entity)
    {
        ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
        if (armor == null || armor.size() != 4) return null;

        return armor.get(EntityEquipmentSlot.HEAD.getIndex());
    }

    public static void setVanillaHeadItem(Entity entity, ItemStack stack)
    {
        entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, stack);
    }

    public static ItemStack getVanillaChestItem(Entity entity)
    {
        ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
        if (armor == null || armor.size() != 4) return null;

        return armor.get(EntityEquipmentSlot.CHEST.getIndex());
    }

    public static void setVanillaChestItem(Entity entity, ItemStack stack)
    {
        entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, stack);
    }

    public static ItemStack getVanillaLegItem(Entity entity)
    {
        ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
        if (armor == null || armor.size() != 4) return null;

        return armor.get(EntityEquipmentSlot.LEGS.getIndex());
    }

    public static void setVanillaLegItem(Entity entity, ItemStack stack)
    {
        entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, stack);
    }

    public static ItemStack getVanillaFootItem(Entity entity)
    {
        ArrayList<ItemStack> armor = getVanillaArmorItems(entity);
        if (armor == null || armor.size() != 4) return null;

        return armor.get(EntityEquipmentSlot.FEET.getIndex());
    }

    public static void setVanillaFootItem(Entity entity, ItemStack stack)
    {
        entity.setItemStackToSlot(EntityEquipmentSlot.FEET, stack);
    }

    public static ArrayList<ItemStack> getVanillaOtherInventoryItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        if (!(entity instanceof EntityPlayer)) return result;

        //Construct list of items to ignore
        ArrayList<ItemStack> ignore = new ArrayList<>();
        ItemStack stack = getVanillaMainhandItem(entity);
        if (stack != null) ignore.add(stack);
        ignore.addAll(getVanillaOffhandItems(entity));
        ignore.addAll(getVanillaArmorItems(entity));

        InventoryPlayer inventory = ((EntityPlayer) entity).inventory;
        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            stack = inventory.getStackInSlot(i);
            if (result.contains(stack) || ignore.contains(stack)) continue;

            result.add(stack);
        }

        return result;
    }


    //Baubles

    public static ArrayList<ItemStack> getBaubles(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        if (!(Compat.baubles && entity instanceof EntityPlayer)) return result;

        IBaublesItemHandler inventory = BaublesApi.getBaublesHandler((EntityPlayer) entity);
        for (int i = 0; i < inventory.getSlots(); i++)
        {
            result.add(inventory.getStackInSlot(i));
        }

        return result;
    }


    //Tiamat Inventory

    public static ITiamatPlayerInventory getTiamatInventory(Entity entity)
    {
        if (!(entity instanceof EntityPlayer)) return null;
        return TiamatInventoryAPI.getTiamatPlayerInventory((EntityPlayer) entity);
    }

    public static ItemStack getTiamatSheathedMainhand1(Entity entity)
    {
        return getTiamatSheathedMainhand1(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatSheathedMainhand1(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getSheathedMainhand1();
    }

    public static void setTiamatSheathedMainhand1(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setSheathedMainhand1(stack);
    }

    public static ItemStack getTiamatSheathedOffhand1(Entity entity)
    {
        return getTiamatSheathedOffhand1(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatSheathedOffhand1(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getSheathedOffhand1();
    }

    public static void setTiamatSheathedOffhand1(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setSheathedOffhand1(stack);
    }

    public static ItemStack getTiamatSheathedMainhand2(Entity entity)
    {
        return getTiamatSheathedMainhand2(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatSheathedMainhand2(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getSheathedMainhand2();
    }

    public static void setTiamatSheathedMainhand2(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setSheathedMainhand2(stack);
    }

    public static ItemStack getTiamatSheathedOffhand2(Entity entity)
    {
        return getTiamatSheathedOffhand2(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatSheathedOffhand2(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getSheathedOffhand2();
    }

    public static void setTiamatSheathedOffhand2(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setSheathedOffhand2(stack);
    }

    public static ArrayList<ItemStack> getTiamatArmor(Entity entity)
    {
        return getTiamatArmor(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatArmor(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getTiamatArmor());
    }

    public static ItemStack getTiamatShoulderItem(Entity entity)
    {
        return getTiamatShoulderItem(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatShoulderItem(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getTiamatArmor().get(0);
    }

    public static void setTiamatShoulderItem(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setShoulders(stack);
    }

    public static ItemStack getTiamatCapeItem(Entity entity)
    {
        return getTiamatCapeItem(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatCapeItem(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getTiamatArmor().get(1);
    }

    public static void setTiamatCapeItem(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setCape(stack);
    }

    public static ArrayList<ItemStack> getTiamatQuickslots(Entity entity)
    {
        return getTiamatQuickslots(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatQuickslots(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getQuickSlots());
    }

    public static void setTiamatQuickslot(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setQuickSlot(index, stack);
    }

    public static ItemStack getTiamatBackpack(Entity entity)
    {
        return getTiamatBackpack(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatBackpack(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getBackpack();
    }

    public static void setTiamatBackpack(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setBackpack(stack);
    }

    public static ItemStack getTiamatPet(Entity entity)
    {
        return getTiamatPet(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatPet(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getPet();
    }

    public static void setTiamatPet(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setPet(stack);
    }

    public static ItemStack getTiamatDeck(Entity entity)
    {
        return getTiamatDeck(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatDeck(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getDeck();
    }

    public static void setTiamatDeck(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setDeck(stack);
    }

    public static ArrayList<ItemStack> getTiamatClasses(Entity entity)
    {
        return getTiamatClasses(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatClasses(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getPlayerClasses());
    }

    public static void setTiamatClass(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setPlayerClass(index, stack);
    }

    public static ArrayList<ItemStack> getTiamatOffensiveSkills(Entity entity)
    {
        return getTiamatOffensiveSkills(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatOffensiveSkills(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getOffensiveSkills());
    }

    public static void setTiamatOffensiveSkill(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setOffensiveSkill(index, stack);
    }

    public static ArrayList<ItemStack> getTiamatUtilitySkills(Entity entity)
    {
        return getTiamatUtilitySkills(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatUtilitySkills(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getUtilitySkills());
    }

    public static void setTiamatUtilitySkill(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setUtilitySkill(index, stack);
    }

    public static ItemStack getTiamatUltimateSkill(Entity entity)
    {
        return getTiamatUltimateSkill(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatUltimateSkill(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getUltimateSkill();
    }

    public static void setTiamatUltimateSkill(Entity entity, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setUltimateSkill(stack);
    }

    public static ArrayList<ItemStack> getTiamatPassiveSkills(Entity entity)
    {
        return getTiamatPassiveSkills(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatPassiveSkills(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getPassiveSkills());
    }

    public static void setTiamatPassiveSkill(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setPassiveSkill(index, stack);
    }

    public static ArrayList<ItemStack> getTiamatGatheringProfessions(Entity entity)
    {
        return getTiamatGatheringProfessions(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatGatheringProfessions(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getGatheringProfessions());
    }

    public static void setTiamatGatheringProfession(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setGatheringProfession(index, stack);
    }

    public static ArrayList<ItemStack> getTiamatCraftingProfessions(Entity entity)
    {
        return getTiamatCraftingProfessions(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatCraftingProfessions(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getCraftingProfessions());
    }

    public static void setTiamatCraftingProfession(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setCraftingProfession(index, stack);
    }

    public static ArrayList<ItemStack> getTiamatRecipes(Entity entity)
    {
        return getTiamatRecipes(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatRecipes(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getCraftingRecipes());
    }

    public static void setTiamatRecipe(Entity entity, int index, ItemStack stack)
    {
        ITiamatPlayerInventory inv = getTiamatInventory(entity);
        if (inv != null) inv.setCraftingRecipe(index, stack);
    }

    public static ArrayList<ItemStack> getAllTiamatItems(Entity entity)
    {
        return getAllTiamatItems(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getAllTiamatItems(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : inventory.getAllItems();
    }


    //Armourer's Workshop

    public static ISkinType getSkinType(String skinTypeName)
    {
        if (skinTypeRegistry == null) return null;

        return skinTypeRegistry.getSkinTypeFromRegistryName(skinTypeName);
    }


    public static int getAWSkinSlotCount(Entity entity, String skinTypeName)
    {
        return getAWSkinSlotCount(entity, getSkinType(skinTypeName));
    }

    public static int getAWSkinSlotCount(Entity entity, ISkinType skinType)
    {
        IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
        if (wardrobeSkinHandler == null) return 0;


        return wardrobeSkinHandler.getSlotCountForSkinType(skinType);
    }


    public static ItemStack getAWSkin(Entity entity, String skinTypeName, int index)
    {
        return getAWSkin(entity, getSkinType(skinTypeName), index);
    }

    public static ItemStack getAWSkin(Entity entity, ISkinType skinType, int index)
    {
        IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
        if (wardrobeSkinHandler == null) return null;


        return wardrobeSkinHandler.getSkinStack(skinType, index);
    }


    public static ArrayList<ItemStack> getAWSkinsOfType(Entity entity, String skinTypeName)
    {
        return getAWSkinsOfType(entity, getSkinType(skinTypeName));
    }

    public static ArrayList<ItemStack> getAWSkinsOfType(Entity entity, ISkinType skinType)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        int size = getAWSkinSlotCount(entity, skinType);
        for (int i = 0; i < size; i++) result.add(getAWSkin(entity, skinType, i));

        return result;
    }


    public static ArrayList<ItemStack> getAWSkins(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
        if (wardrobeSkinHandler == null) return result;


        for (ISkinType skinTypeObject : wardrobeSkinHandler.getValidSkinTypes())
        {
            result.addAll(getAWSkinsOfType(entity, skinTypeObject));
        }

        return result;
    }


    public static ItemStack setAWSkin(Entity entity, String skinTypeName, int index, ItemStack newSkin)
    {
        return setAWSkin(entity, getSkinType(skinTypeName), index, newSkin);
    }

    public static ItemStack setAWSkin(Entity entity, ISkinType skinType, int index, ItemStack newSkin)
    {
        IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
        if (wardrobeSkinHandler == null) return null;


        ItemStack result = wardrobeSkinHandler.setSkinStack(skinType, index, newSkin);
        RenderModes.refresh(entity);
        return result;
    }


    public static ISkinType[] getValidSkinTypes(Entity entity)
    {
        IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
        if (wardrobeSkinHandler == null) return null;


        return wardrobeSkinHandler.getValidSkinTypes();
    }

    public static ArrayList<String> getValidSkinTypeNames(Entity entity)
    {
        ArrayList<String> result = new ArrayList<>();

        for (ISkinType skinTypeObject : getValidSkinTypes(entity)) result.add(skinTypeObject.getName());

        return result;
    }


    public static void syncAWWardrobeSkins(Entity entity, boolean syncToSelf, boolean syncToOthers)
    {
        IEntitySkinCapability wardrobeSkinHandler = ArmourersWorkshopApi.getEntitySkinCapability(entity);
        if (wardrobeSkinHandler == null) return;


        if (syncToSelf && entity instanceof EntityPlayerMP) wardrobeSkinHandler.syncToPlayer((EntityPlayerMP) entity);
        if (syncToOthers) wardrobeSkinHandler.syncToAllTracking();
    }


    public static ItemStack getItem(Entity entity, String slot)
    {
        slot = slot.toLowerCase();
        ArrayList<ItemStack> list;


        //Baubles
        if (slot.contains("bauble"))
        {
            list = getBaubles(entity);
            if (list.size() == 0) return null;

            try
            {
                int index = Integer.parseInt(slot.replace("bauble", "")) - 1;
                if (index < 0 || index >= list.size()) return null;
                return list.get(index);
            }
            catch (NumberFormatException e)
            {
                return null;
            }
        }


        switch (slot)
        {
            //Vanilla
            case "mainhand":
                return getVanillaMainhandItem(entity);

            case "offhand":
                list = getVanillaOffhandItems(entity);
                return list.size() == 0 ? null : list.get(0);

            case "head":
                return getVanillaHeadItem(entity);

            case "chest":
                return getVanillaChestItem(entity);

            case "leg":
                return getVanillaLegItem(entity);

            case "feet":
                return getVanillaFootItem(entity);

            case "hotbar1":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(0);
                return null;

            case "hotbar2":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(1);
                return null;

            case "hotbar3":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(2);
                return null;

            case "hotbar4":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(3);
                return null;

            case "hotbar5":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(4);
                return null;

            case "hotbar6":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(5);
                return null;

            case "hotbar7":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(6);
                return null;

            case "hotbar8":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(7);
                return null;

            case "hotbar9":
                if (entity instanceof EntityPlayer) return ((EntityPlayer) entity).inventory.getStackInSlot(8);
                return null;


            //Tiamat
            case "mainhand1":
                return getTiamatSheathedMainhand1(entity);

            case "offhand1":
                return getTiamatSheathedOffhand1(entity);

            case "mainhand2":
                return getTiamatSheathedMainhand2(entity);

            case "offhand2":
                return getTiamatSheathedOffhand2(entity);

            case "shoulder":
                return getTiamatShoulderItem(entity);

            case "cape":
                return getTiamatCapeItem(entity);

            case "quickslot1":
                list = getTiamatQuickslots(entity);
                return list.size() == 0 ? null : list.get(0);

            case "quickslot2":
                list = getTiamatQuickslots(entity);
                return list.size() == 0 ? null : list.get(1);

            case "quickslot3":
                list = getTiamatQuickslots(entity);
                return list.size() == 0 ? null : list.get(2);

            case "backpack":
                return getTiamatBackpack(entity);

            case "pet":
                return getTiamatPet(entity);

            case "deck":
                return getTiamatDeck(entity);

            case "class1":
                list = getTiamatClasses(entity);
                return list.size() == 0 ? null : list.get(0);

            case "class2":
                list = getTiamatClasses(entity);
                return list.size() == 0 ? null : list.get(1);

            case "offensive1":
                list = getTiamatOffensiveSkills(entity);
                return list.size() == 0 ? null : list.get(0);

            case "offensive2":
                list = getTiamatOffensiveSkills(entity);
                return list.size() == 0 ? null : list.get(1);

            case "utility1":
                list = getTiamatUtilitySkills(entity);
                return list.size() == 0 ? null : list.get(0);

            case "utility2":
                list = getTiamatUtilitySkills(entity);
                return list.size() == 0 ? null : list.get(1);

            case "ultimate":
                return getTiamatUltimateSkill(entity);

            case "passive1":
                list = getTiamatPassiveSkills(entity);
                return list.size() == 0 ? null : list.get(0);

            case "passive2":
                list = getTiamatPassiveSkills(entity);
                return list.size() == 0 ? null : list.get(1);

            case "gathering1":
                list = getTiamatGatheringProfessions(entity);
                return list.size() == 0 ? null : list.get(0);

            case "gathering2":
                list = getTiamatGatheringProfessions(entity);
                return list.size() == 0 ? null : list.get(1);

            case "crafting1":
                list = getTiamatCraftingProfessions(entity);
                return list.size() == 0 ? null : list.get(0);

            case "crafting2":
                list = getTiamatCraftingProfessions(entity);
                return list.size() == 0 ? null : list.get(1);

            case "recipe1":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(0);

            case "recipe2":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(1);

            case "recipe3":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(2);

            case "recipe4":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(3);

            case "recipe5":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(4);

            case "recipe6":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(5);

            case "recipe7":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(6);

            case "recipe8":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(7);

            case "recipe9":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(8);

            case "recipe10":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(9);

            case "recipe11":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(10);

            case "recipe12":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(11);

            case "recipe13":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(12);

            case "recipe14":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(13);

            case "recipe15":
                list = getTiamatRecipes(entity);
                return list.size() == 0 ? null : list.get(14);

            default:
                return null;
        }
    }
}
