package com.fantasticsource.mctools;

import baubles.api.BaubleType;
import com.fantasticsource.fantasticlib.Compat;

import java.util.LinkedHashMap;

public class Slottings
{
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
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Amulet", slots);

        temp = BaubleType.RING.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Ring", slots);

        temp = BaubleType.BELT.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Belt", slots);

        temp = BaubleType.HEAD.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Head", slots);

        temp = BaubleType.BODY.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Body", slots);

        temp = BaubleType.CHARM.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Charm", slots);

        temp = BaubleType.TRINKET.getValidSlots();
        slots = new int[temp.length];
        for (int i = 0; i < slots.length; i++) slots[i] = Integer.MIN_VALUE + temp[i];
        SLOTS_BAUBLES.put("Baubles Trinket", slots);

        SLOTS.putAll(SLOTS_BAUBLES);


        SLOTS_TIAMATRPG.put("Tiamat 2H", new int[]{-500 - 1});

        SLOTS_TIAMATRPG.put("Tiamat Shoulders", new int[]{-500 + 2});
        SLOTS_TIAMATRPG.put("Tiamat Cape", new int[]{-500 + 3});

        SLOTS_TIAMATRPG.put("Tiamat Pet", new int[]{-500 + 4});

        SLOTS_TIAMATRPG.put("Tiamat Class", new int[]{-500 + 5, -500 + 6});

        SLOTS_TIAMATRPG.put("Tiamat Skill", new int[]{-500 + 7, -500 + 8, -500 + 9, -500 + 10, -500 + 11, -500 + 12, -500 + 13, -500 + 14, -500 + 15, -500 + 16, -500 + 17, -500 + 18, -500 + 19, -500 + 20, -500 + 21, -500 + 22, -500 + 23, -500 + 24});
        SLOTS_TIAMATRPG.put("Tiamat Active Skill", new int[]{-500 + 44, -500 + 45, -500 + 46, -500 + 47, -500 + 48, -500 + 49, -500 + 7, -500 + 8, -500 + 9, -500 + 10, -500 + 11, -500 + 12, -500 + 13, -500 + 14, -500 + 15, -500 + 16, -500 + 17, -500 + 18, -500 + 19, -500 + 20, -500 + 21, -500 + 22, -500 + 23, -500 + 24});

        SLOTS_TIAMATRPG.put("Tiamat Gathering Profession", new int[]{-500 + 25, -500 + 26});

        SLOTS_TIAMATRPG.put("Tiamat Crafting Profession", new int[]{-500 + 27, -500 + 28});

        SLOTS_TIAMATRPG.put("Tiamat Recipe", new int[]{-500 + 29, -500 + 30, -500 + 31, -500 + 32, -500 + 33, -500 + 34, -500 + 35, -500 + 36, -500 + 37, -500 + 38, -500 + 39, -500 + 40, -500 + 41, -500 + 42, -500 + 43});

        SLOTS.putAll(SLOTS_TIAMATRPG);


        SLOTS.put("Armor", new int[]{36, 37, 38, 39, -500 + 2, -500 + 3});
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
}
