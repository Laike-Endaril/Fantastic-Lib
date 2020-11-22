package com.fantasticsource.mctools;

import baubles.api.BaubleType;
import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.tiamatitems.api.IPartSlot;
import com.fantasticsource.tiamatitems.api.TiamatItemsAPI;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;

public class Slottings
{
    protected static final String DOMAIN = "tiamatrpg";

    public static final int
            BAUBLES_OFFSET = Integer.MIN_VALUE,
            TIAMAT_OFFSET = -500;

    public static final LinkedHashMap<String, int[]> SLOTS_VANILLA = new LinkedHashMap<>();
    public static final LinkedHashMap<String, int[]> SLOTS_BAUBLES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, int[]> SLOTS_TIAMAT_INVENTORY = new LinkedHashMap<>();

    public static final LinkedHashMap<String, int[]> SLOTS = new LinkedHashMap<>();

    static
    {
        SLOTS_VANILLA.put("None", new int[0]);

        SLOTS_VANILLA.put("Mainhand", new int[]{-1});
        SLOTS_VANILLA.put("Offhand", new int[]{40});
        SLOTS_VANILLA.put("Hand", new int[]{-1, 40});

        SLOTS_VANILLA.put("Head", new int[]{39});
        SLOTS_VANILLA.put("Chest", new int[]{38});
        SLOTS_VANILLA.put("Legs", new int[]{37});
        SLOTS_VANILLA.put("Feet", new int[]{36});

        SLOTS_VANILLA.put("Hotbar", new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8});
        SLOTS_VANILLA.put("Inventory", new int[]{9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35});
        SLOTS_VANILLA.put("Any", new int[]{-2});

        SLOTS.putAll(SLOTS_VANILLA);


        int[] temp = BaubleType.AMULET.getValidSlots();
        int[] slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Amulet", slots);

        temp = BaubleType.RING.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Ring", slots);

        temp = BaubleType.BELT.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Belt", slots);

        temp = BaubleType.HEAD.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Head", slots);

        temp = BaubleType.BODY.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Body", slots);

        temp = BaubleType.CHARM.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Charm", slots);

        temp = BaubleType.TRINKET.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = BAUBLES_OFFSET + temp[i];
        SLOTS_BAUBLES.put("Baubles Trinket", slots);

        SLOTS.putAll(SLOTS_BAUBLES);


        SLOTS_TIAMAT_INVENTORY.put("Tiamat 2H", new int[]{-1, 40});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Shoulders", new int[]{TIAMAT_OFFSET + 4});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Cape", new int[]{TIAMAT_OFFSET + 5});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Quickslot", new int[]{TIAMAT_OFFSET + 6, TIAMAT_OFFSET + 7, TIAMAT_OFFSET + 8});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Backpack", new int[]{TIAMAT_OFFSET + 9});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Pet", new int[]{TIAMAT_OFFSET + 10});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Deck", new int[]{TIAMAT_OFFSET + 11});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Class", new int[]{TIAMAT_OFFSET + 12, TIAMAT_OFFSET + 13});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Offensive Skill", new int[]{TIAMAT_OFFSET + 14, TIAMAT_OFFSET + 15});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Utility Skill", new int[]{TIAMAT_OFFSET + 16, TIAMAT_OFFSET + 17});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Ultimate Skill", new int[]{TIAMAT_OFFSET + 18});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Passive Skill", new int[]{TIAMAT_OFFSET + 19, TIAMAT_OFFSET + 20});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Gathering Profession", new int[]{TIAMAT_OFFSET + 21, TIAMAT_OFFSET + 22});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Crafting Profession", new int[]{TIAMAT_OFFSET + 23, TIAMAT_OFFSET + 24});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Recipe", new int[]{TIAMAT_OFFSET + 25, TIAMAT_OFFSET + 26, TIAMAT_OFFSET + 27, TIAMAT_OFFSET + 28, TIAMAT_OFFSET + 29, TIAMAT_OFFSET + 30, TIAMAT_OFFSET + 31, TIAMAT_OFFSET + 32, TIAMAT_OFFSET + 33, TIAMAT_OFFSET + 34, TIAMAT_OFFSET + 35, TIAMAT_OFFSET + 36, TIAMAT_OFFSET + 37, TIAMAT_OFFSET + 38, TIAMAT_OFFSET + 39});

        SLOTS.putAll(SLOTS_TIAMAT_INVENTORY);


        SLOTS.put("Armor", new int[]{36, 37, 38, 39, TIAMAT_OFFSET + 4, TIAMAT_OFFSET + 5});
    }

    public static String[] availableSlottings()
    {
        int size = SLOTS_VANILLA.size();
        if (Compat.baubles) size += SLOTS_BAUBLES.size();
        if (Compat.tiamatinventory) size += SLOTS_TIAMAT_INVENTORY.size();

        String[] result = new String[size];
        int i = 0;

        String[] temp = SLOTS_VANILLA.keySet().toArray(new String[0]);
        System.arraycopy(temp, 0, result, i, temp.length);
        i += temp.length;

        if (Compat.baubles)
        {
            temp = SLOTS_BAUBLES.keySet().toArray(new String[0]);
            System.arraycopy(temp, 0, result, i, temp.length);
            i += temp.length;
        }
        if (Compat.tiamatinventory)
        {
            temp = SLOTS_TIAMAT_INVENTORY.keySet().toArray(new String[0]);
            System.arraycopy(temp, 0, result, i, temp.length);
            i += temp.length;
        }

        return result;
    }


    public static boolean slotTypeValidForSlotting(String slotting, String slotType, EntityPlayer player)
    {
        return slotValidForSlotting(slotting, SLOTS.get(slotType)[0], player);
    }

    public static boolean slotValidForSlotting(String slotting, int slot, EntityPlayer player)
    {
        if (!SLOTS.containsKey(slotting)) return false;

        for (int i : SLOTS.get(slotting))
        {
            if (i == -2) return true;
            if (i == -1)
            {
                if (slot == player.inventory.currentItem && (!Compat.tiamatinventory || slot == 0 || player.isCreative())) return true;
            }
            else if (i == slot && (i != 40 || !Compat.tiamatinventory || player.inventory.currentItem == 0 || player.isCreative())) return true;
        }

        return false;
    }


    public static boolean slotTypeValidForItemstack(ItemStack stack, String slotType, EntityPlayer player)
    {
        if (slotType.equals("Head") && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.HEAD, player)) return true;
        if (slotType.equals("Chest") && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.CHEST, player)) return true;
        if (slotType.equals("Legs") && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.LEGS, player)) return true;
        if (slotType.equals("Feet") && stack.getItem().isValidArmor(stack, EntityEquipmentSlot.FEET, player)) return true;

        int[] slots = SLOTS.get(slotType);
        return slots != null && slots.length > 0 && slotValidForItemstack(stack, SLOTS.get(slotType)[0], player);
    }

    protected static boolean slotValidForItemstack(ItemStack stack, int slot, EntityPlayer player)
    {
        if (!slotValidForSlotting(getItemSlotting(stack), slot, player)) return false;

        for (IPartSlot partSlot : TiamatItemsAPI.getPartSlots(stack))
        {
            if (partSlot.getRequired() && partSlot.getPart().isEmpty()) return false;
        }

        return true;
    }


    public static void setItemSlotting(ItemStack stack, String slotting)
    {
        if (slotting == null || slotting.equals("") || slotting.equals("None"))
        {
            clearItemSlotting(stack);
            return;
        }

        if (!stack.hasTagCompound()) stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound compound = stack.getTagCompound();

        if (!compound.hasKey(DOMAIN)) compound.setTag(DOMAIN, new NBTTagCompound());
        compound = compound.getCompoundTag(DOMAIN);

        compound.setString("slotting", slotting);
    }

    public static String getItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return "None";

        NBTTagCompound compound = stack.getTagCompound();
        if (!compound.hasKey(DOMAIN)) return "None";

        compound = compound.getCompoundTag(DOMAIN);
        if (!compound.hasKey("slotting")) return "None";

        return compound.getString("slotting");
    }

    public static void clearItemSlotting(ItemStack stack)
    {
        if (!stack.hasTagCompound()) return;

        NBTTagCompound mainTag = stack.getTagCompound();
        if (!mainTag.hasKey(DOMAIN)) return;

        NBTTagCompound compound = mainTag.getCompoundTag(DOMAIN);
        if (!compound.hasKey("slotting")) return;

        compound.removeTag("slotting");
        if (compound.hasNoTags())
        {
            mainTag.removeTag(DOMAIN);
            if (mainTag.hasNoTags()) stack.setTagCompound(null);
        }
    }

    public static boolean isTwoHanded(ItemStack stack)
    {
        return stack.getItem() == Items.BOW || getItemSlotting(stack).equals("Tiamat 2H");
    }
}
