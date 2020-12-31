package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
import com.fantasticsource.tools.Tools;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.LinkedHashMap;

public class Slottings
{
    protected static final String DOMAIN = "tiamatrpg";

    public static final LinkedHashMap<String, String[]> SLOTS_VANILLA = new LinkedHashMap<>();
    public static final LinkedHashMap<String, String[]> SLOTS_BAUBLES = new LinkedHashMap<>();
    public static final LinkedHashMap<String, String[]> SLOTS_TIAMAT_INVENTORY = new LinkedHashMap<>();

    public static final LinkedHashMap<String, String[]> SLOTS_ALL = new LinkedHashMap<>();
    public static final LinkedHashMap<String, String[]> SLOTS_AVAILABLE = new LinkedHashMap<>();

    static
    {
        //Itemstack slotting -> valid slot types
        SLOTS_VANILLA.put("None", new String[]{});

        SLOTS_VANILLA.put("Mainhand", new String[]{"Mainhand"});
        SLOTS_VANILLA.put("Offhand", new String[]{"Offhand"});
        SLOTS_VANILLA.put("Hand", new String[]{"Mainhand", "Offhand"});

        SLOTS_VANILLA.put("Head", new String[]{"Head"});
        SLOTS_VANILLA.put("Chest", new String[]{"Chest"});
        SLOTS_VANILLA.put("Legs", new String[]{"Legs"});
        SLOTS_VANILLA.put("Feet", new String[]{"Feet"});
        SLOTS_VANILLA.put("Armor", new String[]{"Head", "Chest", "Legs", "Feet"});

        SLOTS_VANILLA.put("Hotbar", new String[]{"Hotbar", "Mainhand"});
        SLOTS_VANILLA.put("Cargo", new String[]{"Cargo"});
        SLOTS_VANILLA.put("Any", new String[]{"Mainhand", "Offhand", "Head", "Chest", "Legs", "Feet", "Hotbar", "Cargo", "Baubles Amulet", "Baubles Ring", "Baubles Belt", "Baubles Head", "Baubles Body", "Baubles Charm", "Tiamat Shoulders", "Tiamat Cape", "Tiamat Quickslot", "Tiamat Backpack", "Tiamat Pet", "Tiamat Deck", "Tiamat Class", "Tiamat Offensive Skill", "Tiamat Utility Skill", "Tiamat Ultimate Skill", "Tiamat Passive Skill", "Tiamat Gathering Profession", "Tiamat Crafting Profession", "Tiamat Recipe"});


        SLOTS_BAUBLES.put("Baubles Amulet", new String[]{"Baubles Amulet"});
        SLOTS_BAUBLES.put("Baubles Ring", new String[]{"Baubles Ring"});
        SLOTS_BAUBLES.put("Baubles Belt", new String[]{"Baubles Belt"});
        SLOTS_BAUBLES.put("Baubles Head", new String[]{"Baubles Head"});
        SLOTS_BAUBLES.put("Baubles Body", new String[]{"Baubles Body"});
        SLOTS_BAUBLES.put("Baubles Charm", new String[]{"Baubles Charm"});
        SLOTS_BAUBLES.put("Baubles Trinket", new String[]{"Baubles Amulet", "Baubles Ring", "Baubles Belt", "Baubles Head", "Baubles Body", "Baubles Charm"});


        SLOTS_TIAMAT_INVENTORY.put("Tiamat 2H", new String[]{"Mainhand", "Offhand"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Shoulders", new String[]{"Tiamat Shoulders"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Cape", new String[]{"Tiamat Cape"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Quickslot", new String[]{"Tiamat Quickslot"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Backpack", new String[]{"Tiamat Backpack"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Pet", new String[]{"Tiamat Pet"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Deck", new String[]{"Tiamat Deck"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Class", new String[]{"Tiamat Class"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Offensive Skill", new String[]{"Tiamat Offensive Skill"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Utility Skill", new String[]{"Tiamat Utility Skill"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Ultimate Skill", new String[]{"Tiamat Ultimate Skill"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Passive Skill", new String[]{"Tiamat Passive Skill"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Skill", new String[]{"Tiamat Offensive Skill", "Tiamat Utility Skill", "Tiamat Ultimate Skill", "Tiamat Passive Skill"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Gathering Profession", new String[]{"Tiamat Gathering Profession"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Crafting Profession", new String[]{"Tiamat Crafting Profession"});
        SLOTS_TIAMAT_INVENTORY.put("Tiamat Profession", new String[]{"Tiamat Gathering Profession", "Tiamat Crafting Profession"});

        SLOTS_TIAMAT_INVENTORY.put("Tiamat Recipe", new String[]{"Tiamat Recipe"});


        SLOTS_ALL.putAll(SLOTS_VANILLA);
        SLOTS_ALL.putAll(SLOTS_BAUBLES);
        SLOTS_ALL.putAll(SLOTS_TIAMAT_INVENTORY);


        SLOTS_AVAILABLE.putAll(SLOTS_VANILLA);
        if (Compat.baubles) SLOTS_AVAILABLE.putAll(SLOTS_BAUBLES);
        if (Compat.tiamatinventory) SLOTS_AVAILABLE.putAll(SLOTS_TIAMAT_INVENTORY);
    }

    public static String[] availableSlottings()
    {
        return SLOTS_AVAILABLE.keySet().toArray(new String[0]);
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


    public static boolean slottingIsValidForSlot(String itemSlotting, String slotType)
    {
        return SLOTS_AVAILABLE.containsKey(itemSlotting) && Tools.contains(SLOTS_AVAILABLE.get(itemSlotting), slotType);
    }

    public static boolean itemIsValidForSlot(ItemStack stack, String slotType)
    {
        return slottingIsValidForSlot(getItemSlotting(stack), slotType);
    }
}
