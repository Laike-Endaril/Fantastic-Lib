package com.fantasticsource.mctools;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.mctools.aw.RenderModes;
import com.fantasticsource.tiamatrpg.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatrpg.api.TiamatRPGAPI;
import com.fantasticsource.tools.ReflectionTool;
import moe.plushie.armourers_workshop.api.ArmourersWorkshopApi;
import moe.plushie.armourers_workshop.api.common.capability.IEntitySkinCapability;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinTypeRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GlobalInventory
{
    public static ISkinTypeRegistry skinTypeRegistry = ArmourersWorkshopApi.skinTypeRegistry;


    protected static LinkedHashMap<UUID, IInventory> tiamatServerInventories = null;

    static
    {
        if (Compat.tiamatrpg)
        {
            Class tiamatPlayerInventoryClass = ReflectionTool.getClassByName("com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory");
            Field tiamatServerInventoriesField = ReflectionTool.getField(tiamatPlayerInventoryClass, "tiamatServerInventories");
            tiamatServerInventories = (LinkedHashMap<UUID, IInventory>) ReflectionTool.get(tiamatServerInventoriesField, null);
        }
    }


    public static ArrayList<ItemStack> getAllItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        //Vanilla
        ItemStack stack = getVanillaMainhandItem(entity);
        if (stack != null) result.add(stack);
        result.addAll(getVanillaOffhandItems(entity));
        result.addAll(getVanillaArmorItems(entity));
        result.addAll(getVanillaOtherInventoryItems(entity));

        //Baubles
        result.addAll(getBaubles(entity));

        //Tiamat RPG
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        if (inventory != null) result.addAll(inventory.getAllItems());

        //Armourer's Workshop
        result.addAll(getAWSkins(entity));

        return result;
    }

    public static ArrayList<ItemStack> getAllEquippedItems(Entity entity)
    {
        ArrayList<ItemStack> result = getAllEquippedNonAWItems(entity);

        //Armourer's Workshop
        result.addAll(getAWSkins(entity));

        return result;
    }

    public static ArrayList<ItemStack> getAllEquippedNonAWItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        //Sheathed status
        ITiamatPlayerInventory tiamatInventory = getTiamatInventory(entity);
        boolean sheathed = tiamatInventory != null && !tiamatInventory.unsheathed();

        //Vanilla
        if (!sheathed)
        {
            ItemStack stack = getVanillaMainhandItem(entity);
            if (stack != null) result.add(stack);
            result.addAll(getVanillaOffhandItems(entity));
        }
        result.addAll(getVanillaArmorItems(entity));

        //Baubles
        result.addAll(getBaubles(entity));

        //Tiamat RPG
        tiamatInventory = getTiamatInventory(entity);
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


        //Tiamat RPG
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        if (inventory != null)
        {
            stack = getTiamatInactiveMainhand(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Inactive Weaponset Mainhand", list);
                list = new ArrayList<>();
            }

            stack = getTiamatInactiveOffhand(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Inactive Weaponset Offhand", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatArmor(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Armor", list);
                list = new ArrayList<>();
            }

            stack = getTiamatPet(inventory);
            if (stack != null)
            {
                list.add(stack);
                result.put("Tiamat Pet", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatClasses(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Classes", list);
                list = new ArrayList<>();
            }

            list.addAll(getTiamatSkills(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Skills", list);
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

            list.addAll(getTiamatReadySkills(inventory));
            if (list.size() > 0)
            {
                result.put("Tiamat Ready Skills", list);
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
        ArrayList<ItemStack> result = GlobalInventory.getAllEquippedNonAWItems(player);

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
        if (Compat.tiamatrpg)
        {
            int slot = 0;
            for (ItemStack stack : GlobalInventory.getAllTiamatItems(player))
            {
                if (!Slottings.slotValidForSlotting(getItemSlotting(stack), slot + Slottings.TIAMAT_OFFSET, player)) continue;

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

    public static ItemStack getVanillaMainhandItem(Entity entity)
    {
        if (entity instanceof EntityLivingBase) return ((EntityLivingBase) entity).getHeldItemMainhand();
        for (ItemStack stack : entity.getHeldEquipment()) return stack;
        return null;
    }

    public static ArrayList<ItemStack> getVanillaOffhandItems(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();

        ItemStack mainhand = getVanillaMainhandItem(entity);
        for (ItemStack stack : entity.getHeldEquipment())
        {
            if (stack != mainhand && !result.contains(stack)) result.add(stack);
        }

        return result;
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


    //Tiamat RPG

    public static ITiamatPlayerInventory getTiamatInventory(Entity entity)
    {
        if (tiamatServerInventories == null || !(entity instanceof EntityPlayer)) return null;
        return TiamatRPGAPI.getTiamatPlayerInventory((EntityPlayer) entity);
    }

    public static ItemStack getTiamatInactiveMainhand(Entity entity)
    {
        return getTiamatInactiveMainhand(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatInactiveMainhand(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getInactiveWeaponsetMainhand();
    }

    public static ItemStack getTiamatInactiveOffhand(Entity entity)
    {
        return getTiamatInactiveOffhand(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatInactiveOffhand(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getInactiveWeaponsetOffhand();
    }

    public static ArrayList<ItemStack> getTiamatArmor(Entity entity)
    {
        return getTiamatArmor(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatArmor(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getTiamatArmor());
    }

    public static ItemStack getTiamatPet(Entity entity)
    {
        return getTiamatPet(getTiamatInventory(entity));
    }

    public static ItemStack getTiamatPet(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? null : inventory.getPet();
    }

    public static ArrayList<ItemStack> getTiamatClasses(Entity entity)
    {
        return getTiamatClasses(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatClasses(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getPlayerClasses());
    }

    public static ArrayList<ItemStack> getTiamatSkills(Entity entity)
    {
        return getTiamatSkills(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatSkills(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getSkills());
    }

    public static ArrayList<ItemStack> getTiamatGatheringProfessions(Entity entity)
    {
        return getTiamatGatheringProfessions(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatGatheringProfessions(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getGatheringProfessions());
    }

    public static ArrayList<ItemStack> getTiamatCraftingProfessions(Entity entity)
    {
        return getTiamatCraftingProfessions(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatCraftingProfessions(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getCraftingProfessions());
    }

    public static ArrayList<ItemStack> getTiamatRecipes(Entity entity)
    {
        return getTiamatRecipes(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatRecipes(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getCraftingRecipes());
    }

    public static ArrayList<ItemStack> getTiamatReadySkills(Entity entity)
    {
        return getTiamatReadySkills(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getTiamatReadySkills(ITiamatPlayerInventory inventory)
    {
        return inventory == null ? new ArrayList<>() : new ArrayList<>(inventory.getReadySkills());
    }

    public static ArrayList<ItemStack> getAllTiamatItems(Entity entity)
    {
        return getAllTiamatItems(getTiamatInventory(entity));
    }

    public static ArrayList<ItemStack> getAllTiamatItems(ITiamatPlayerInventory inventory)
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        if (inventory == null) return result;

        for (int i = 0; i < inventory.getSizeInventory(); i++)
        {
            result.add(inventory.getStackInSlot(i));
        }
        return result;
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
}
