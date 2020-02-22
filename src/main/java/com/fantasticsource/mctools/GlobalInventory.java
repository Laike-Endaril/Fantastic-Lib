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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GlobalInventory
{
    public static LinkedHashMap<UUID, IInventory> tiamatServerInventories = null;

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

        return result;
    }


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


    public static ITiamatPlayerInventory getTiamatInventory(Entity entity)
    {
        if (tiamatServerInventories == null || !(entity instanceof EntityPlayer)) return null;
        return TiamatRPGAPI.getTiamatPlayerInventory((EntityPlayer) entity);
    }

    public static ItemStack getTiamatInactiveMainhand(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : inventory.getInactiveWeaponsetMainhand();
    }

    public static ItemStack getTiamatInactiveOffhand(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : inventory.getInactiveWeaponsetOffhand();
    }

    public static ArrayList<ItemStack> getTiamatArmor(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getTiamatArmor());
    }

    public static ItemStack getTiamatPet(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : inventory.getPet();
    }

    public static ArrayList<ItemStack> getTiamatClasses(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getPlayerClasses());
    }

    public static ArrayList<ItemStack> getTiamatSkills(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getSkills());
    }

    public static ArrayList<ItemStack> getTiamatGatheringProfessions(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getGatheringProfessions());
    }

    public static ArrayList<ItemStack> getTiamatCraftingProfessions(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getCraftingProfessions());
    }

    public static ArrayList<ItemStack> getTiamatRecipes(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getCraftingRecipes());
    }

    public static ArrayList<ItemStack> getTiamatReadySkills(Entity entity)
    {
        ITiamatPlayerInventory inventory = getTiamatInventory(entity);
        return inventory == null ? null : new ArrayList<>(inventory.getReadySkills());
    }
}
