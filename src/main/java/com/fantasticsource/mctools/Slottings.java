package com.fantasticsource.mctools;

import baubles.api.BaubleType;
import com.fantasticsource.fantasticlib.Compat;
import net.minecraft.entity.player.EntityPlayer;

import java.util.LinkedHashMap;

public class Slottings
{
    public static final int
            BAUBLES_OFFSET = Integer.MIN_VALUE,
            TIAMAT_OFFSET = -500;

    public static final LinkedHashMap<String, int[]> SLOTS_VANILLA = new LinkedHashMap<>();
    public static final LinkedHashMap<String, int[]> SLOTS_BAUBLES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, int[]> SLOTS_TIAMATRPG = new LinkedHashMap<>();

    public static final LinkedHashMap<String, int[]> SLOTS = new LinkedHashMap<>();

    static
    {
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


        SLOTS_TIAMATRPG.put("Tiamat 2H", new int[]{-1, 40});

        SLOTS_TIAMATRPG.put("Tiamat Shoulders", new int[]{TIAMAT_OFFSET + 2});
        SLOTS_TIAMATRPG.put("Tiamat Cape", new int[]{TIAMAT_OFFSET + 3});

        SLOTS_TIAMATRPG.put("Tiamat Pet", new int[]{TIAMAT_OFFSET + 4});

        SLOTS_TIAMATRPG.put("Tiamat Class", new int[]{TIAMAT_OFFSET + 5, TIAMAT_OFFSET + 6});

        SLOTS_TIAMATRPG.put("Tiamat Skill", new int[]{TIAMAT_OFFSET + 7, TIAMAT_OFFSET + 8, TIAMAT_OFFSET + 9, TIAMAT_OFFSET + 10, TIAMAT_OFFSET + 11, TIAMAT_OFFSET + 12, TIAMAT_OFFSET + 13, TIAMAT_OFFSET + 14, TIAMAT_OFFSET + 15, TIAMAT_OFFSET + 16, TIAMAT_OFFSET + 17, TIAMAT_OFFSET + 18, TIAMAT_OFFSET + 19, TIAMAT_OFFSET + 20, TIAMAT_OFFSET + 21, TIAMAT_OFFSET + 22, TIAMAT_OFFSET + 23, TIAMAT_OFFSET + 24});
        SLOTS_TIAMATRPG.put("Tiamat Active Skill", new int[]{TIAMAT_OFFSET + 44, TIAMAT_OFFSET + 45, TIAMAT_OFFSET + 46, TIAMAT_OFFSET + 47, TIAMAT_OFFSET + 48, TIAMAT_OFFSET + 49, TIAMAT_OFFSET + 7, TIAMAT_OFFSET + 8, TIAMAT_OFFSET + 9, TIAMAT_OFFSET + 10, TIAMAT_OFFSET + 11, TIAMAT_OFFSET + 12, TIAMAT_OFFSET + 13, TIAMAT_OFFSET + 14, TIAMAT_OFFSET + 15, TIAMAT_OFFSET + 16, TIAMAT_OFFSET + 17, TIAMAT_OFFSET + 18, TIAMAT_OFFSET + 19, TIAMAT_OFFSET + 20, TIAMAT_OFFSET + 21, TIAMAT_OFFSET + 22, TIAMAT_OFFSET + 23, TIAMAT_OFFSET + 24});

        SLOTS_TIAMATRPG.put("Tiamat Gathering Profession", new int[]{TIAMAT_OFFSET + 25, TIAMAT_OFFSET + 26});

        SLOTS_TIAMATRPG.put("Tiamat Crafting Profession", new int[]{TIAMAT_OFFSET + 27, TIAMAT_OFFSET + 28});

        SLOTS_TIAMATRPG.put("Tiamat Recipe", new int[]{TIAMAT_OFFSET + 29, TIAMAT_OFFSET + 30, TIAMAT_OFFSET + 31, TIAMAT_OFFSET + 32, TIAMAT_OFFSET + 33, TIAMAT_OFFSET + 34, TIAMAT_OFFSET + 35, TIAMAT_OFFSET + 36, TIAMAT_OFFSET + 37, TIAMAT_OFFSET + 38, TIAMAT_OFFSET + 39, TIAMAT_OFFSET + 40, TIAMAT_OFFSET + 41, TIAMAT_OFFSET + 42, TIAMAT_OFFSET + 43});

        SLOTS.putAll(SLOTS_TIAMATRPG);


        SLOTS.put("Armor", new int[]{36, 37, 38, 39, TIAMAT_OFFSET + 2, TIAMAT_OFFSET + 3});
    }

    public static String[] availableSlottings()
    {
        int size = SLOTS_VANILLA.size();
        if (Compat.baubles) size += SLOTS_BAUBLES.size();
        if (Compat.tiamatrpg) size += SLOTS_TIAMATRPG.size();

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
        if (Compat.tiamatrpg)
        {
            temp = SLOTS_TIAMATRPG.keySet().toArray(new String[0]);
            System.arraycopy(temp, 0, result, i, temp.length);
            i += temp.length;
        }

        return result;
    }

    public static boolean slotValidForSlotting(String slotting, int slot, EntityPlayer player)
    {
        if (!SLOTS.containsKey(slotting)) return false;

        int[] slots = SLOTS.get(slotting);
        for (int i : slots)
        {
            if (i == -2) return true;
            if (i == slot) return true;
            if (i == -1 && slot == player.inventory.currentItem && (!Compat.tiamatrpg || slot == 0 || player.isCreative())) return true;
        }

        return false;
    }
}
