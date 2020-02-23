package com.fantasticsource.mctools;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.tiamatrpg.api.ITiamatPlayerInventory;
import com.fantasticsource.tiamatrpg.api.TiamatRPGAPI;
import com.fantasticsource.tools.ReflectionTool;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GlobalInventory
{
    protected static Profiler profiler = null;

    protected static LinkedHashMap<UUID, IInventory> tiamatServerInventories = null;

    protected static Object awSkinTypeRegistry = null;
    protected static Method awGetSkinTypeFromRegistryNameMethod = null, awEntitySkinCapabilityGetMethod = null, awGetSlotCountForSkinTypeMethod = null, awGetSkinStackMethod = null, awSetSkinStackMethod;
    protected static Field awValidSkinTypesField = null;

    static
    {
        MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
        if (server != null) profiler = server.profiler;


        if (Compat.tiamatrpg)
        {
            Class tiamatPlayerInventoryClass = ReflectionTool.getClassByName("com.fantasticsource.tiamatrpg.inventory.TiamatPlayerInventory");
            Field tiamatServerInventoriesField = ReflectionTool.getField(tiamatPlayerInventoryClass, "tiamatServerInventories");
            tiamatServerInventories = (LinkedHashMap<UUID, IInventory>) ReflectionTool.get(tiamatServerInventoriesField, null);
        }

        if (Compat.armourers_workshop)
        {
            Class awSkinTypeRegistryClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.skin.type.SkinTypeRegistry");
            Field awSkinTypeRegistryInstanceField = ReflectionTool.getField(awSkinTypeRegistryClass, "INSTANCE");
            awSkinTypeRegistry = ReflectionTool.get(awSkinTypeRegistryInstanceField, null);
            awGetSkinTypeFromRegistryNameMethod = ReflectionTool.getMethod(awSkinTypeRegistryClass, "getSkinTypeFromRegistryName");

            Class awEntitySkinCapabilityClass = ReflectionTool.getClassByName("moe.plushie.armourers_workshop.common.capability.entityskin.EntitySkinCapability");
            awEntitySkinCapabilityGetMethod = ReflectionTool.getMethod(awEntitySkinCapabilityClass, "get");
            awGetSlotCountForSkinTypeMethod = ReflectionTool.getMethod(awEntitySkinCapabilityClass, "getSlotCountForSkinType");
            awGetSkinStackMethod = ReflectionTool.getMethod(awEntitySkinCapabilityClass, "getSkinStack");
            awValidSkinTypesField = ReflectionTool.getField(awEntitySkinCapabilityClass, "validSkinTypes");
            awSetSkinStackMethod = ReflectionTool.getMethod(awEntitySkinCapabilityClass, "setSkinStack");
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
        //TODO exclude sheathed items
        ArrayList<ItemStack> result = new ArrayList<>();

        //Vanilla
        ItemStack stack = getVanillaMainhandItem(entity);
        if (stack != null) result.add(stack);
        result.addAll(getVanillaOffhandItems(entity));
        result.addAll(getVanillaArmorItems(entity));

        //Baubles
        result.addAll(getBaubles(entity));

        //Tiamat RPG
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        if (inventory != null) result.addAll(inventory.getAllEquippedItems());

        //Armourer's Workshop
        result.addAll(getAWSkins(entity));

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


    //Armourer's Workshop

    public static int getAWSkinSlotCount(Entity entity, String skinType)
    {
        if (!Compat.armourers_workshop) return 0;


        if (profiler != null) profiler.startSection("Fantastic Lib: getAWSkinSlotCount");


        Object skinCapabilityObject = ReflectionTool.invoke(awEntitySkinCapabilityGetMethod, null, entity);
        if (skinCapabilityObject == null)
        {
            if (profiler != null) profiler.endSection();
            return 0;
        }

        Object skinTypeObject = ReflectionTool.invoke(awGetSkinTypeFromRegistryNameMethod, awSkinTypeRegistry, skinType);

        int result = (int) ReflectionTool.invoke(awGetSlotCountForSkinTypeMethod, skinCapabilityObject, skinTypeObject);


        if (profiler != null) profiler.endSection();
        return result;
    }

    public static ItemStack getAWSkin(Entity entity, String skinType, int index)
    {
        if (!Compat.armourers_workshop) return null;


        if (profiler != null) profiler.startSection("Fantastic Lib: getAWSkin");


        Object skinCapabilityObject = ReflectionTool.invoke(awEntitySkinCapabilityGetMethod, null, entity);
        if (skinCapabilityObject == null)
        {
            if (profiler != null) profiler.endSection();
            return null;
        }

        Object skinTypeObject = ReflectionTool.invoke(awGetSkinTypeFromRegistryNameMethod, awSkinTypeRegistry, skinType);

        ItemStack result = (ItemStack) ReflectionTool.invoke(awGetSkinStackMethod, skinCapabilityObject, skinTypeObject, index);


        if (profiler != null) profiler.endSection();
        return result;
    }

    public static ArrayList<ItemStack> getAWSkinsOfType(Entity entity, String skinType)
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        if (!Compat.armourers_workshop) return result;


        if (profiler != null) profiler.startSection("Fantastic Lib: getAWSkinsOfType");


        Object skinCapabilityObject = ReflectionTool.invoke(awEntitySkinCapabilityGetMethod, null, entity);
        if (skinCapabilityObject == null)
        {
            if (profiler != null) profiler.endSection();
            return result;
        }

        Object skinTypeObject = ReflectionTool.invoke(awGetSkinTypeFromRegistryNameMethod, awSkinTypeRegistry, skinType);

        int size = (int) ReflectionTool.invoke(awGetSlotCountForSkinTypeMethod, skinCapabilityObject, skinTypeObject);
        for (int i = 0; i < size; i++)
        {
            result.add((ItemStack) ReflectionTool.invoke(awGetSkinStackMethod, skinCapabilityObject, skinTypeObject, i));
        }


        if (profiler != null) profiler.endSection();
        return result;
    }

    public static ArrayList<ItemStack> getAWSkins(Entity entity)
    {
        ArrayList<ItemStack> result = new ArrayList<>();
        if (!Compat.armourers_workshop) return result;


        if (profiler != null) profiler.startSection("Fantastic Lib: getAWSkins");


        Object skinCapabilityObject = ReflectionTool.invoke(awEntitySkinCapabilityGetMethod, null, entity);
        if (skinCapabilityObject == null)
        {
            if (profiler != null) profiler.endSection();
            return result;
        }

        int size;
        for (Object skinTypeObject : (Object[]) ReflectionTool.get(awValidSkinTypesField, skinCapabilityObject))
        {
            size = (int) ReflectionTool.invoke(awGetSlotCountForSkinTypeMethod, skinCapabilityObject, skinTypeObject);
            for (int i = 0; i < size; i++)
            {
                result.add((ItemStack) ReflectionTool.invoke(awGetSkinStackMethod, skinCapabilityObject, skinTypeObject, i));
            }
        }


        if (profiler != null) profiler.endSection();
        return result;
    }

    public static ItemStack setAWSkin(Entity entity, String skinType, int index, ItemStack newSkin)
    {
        if (!Compat.armourers_workshop) return null;


        if (profiler != null) profiler.startSection("Fantastic Lib: setAWSkin");


        Object skinCapabilityObject = ReflectionTool.invoke(awEntitySkinCapabilityGetMethod, null, entity);
        if (skinCapabilityObject == null)
        {
            if (profiler != null) profiler.endSection();
            return null;
        }

        Object skinTypeObject = ReflectionTool.invoke(awGetSkinTypeFromRegistryNameMethod, awSkinTypeRegistry, skinType);

        ItemStack result = (ItemStack) ReflectionTool.invoke(awSetSkinStackMethod, skinCapabilityObject, skinTypeObject, index, newSkin);


        if (profiler != null) profiler.endSection();
        return result;
    }
}
