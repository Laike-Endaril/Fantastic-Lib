package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.Compat;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.ArrayList;

public class Slottings
{
    protected static final String DOMAIN = "tiamatrpg";

    public static final ArrayList<String> SLOTS_VANILLA = new ArrayList<>();
    public static final ArrayList<String> SLOTS_BAUBLES = new ArrayList<>();
    public static final ArrayList<String> SLOTS_TIAMAT_INVENTORY = new ArrayList<>();

    static
    {
        SLOTS_VANILLA.add("None");

        SLOTS_VANILLA.add("Mainhand");
        SLOTS_VANILLA.add("Offhand");
        SLOTS_VANILLA.add("Hand");

        SLOTS_VANILLA.add("Head");
        SLOTS_VANILLA.add("Chest");
        SLOTS_VANILLA.add("Legs");
        SLOTS_VANILLA.add("Feet");
        SLOTS_VANILLA.add("Armor");

        SLOTS_VANILLA.add("Hotbar");
        SLOTS_VANILLA.add("Cargo");
        SLOTS_VANILLA.add("Any");


        SLOTS_BAUBLES.add("Baubles Amulet");
        SLOTS_BAUBLES.add("Baubles Ring");
        SLOTS_BAUBLES.add("Baubles Belt");
        SLOTS_BAUBLES.add("Baubles Head");
        SLOTS_BAUBLES.add("Baubles Body");
        SLOTS_BAUBLES.add("Baubles Charm");
        SLOTS_BAUBLES.add("Baubles Trinket");


        SLOTS_TIAMAT_INVENTORY.add("Tiamat 2H");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Shoulders");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Cape");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Quickslot");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Backpack");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Pet");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Deck");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Class");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Offensive Skill");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Utility Skill");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Ultimate Skill");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Passive Skill");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Skill");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Gathering Profession");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Crafting Profession");
        SLOTS_TIAMAT_INVENTORY.add("Tiamat Profession");

        SLOTS_TIAMAT_INVENTORY.add("Tiamat Recipe");
    }

    public static ArrayList<String> availableSlottings()
    {
        ArrayList<String> result = new ArrayList<>(SLOTS_VANILLA);
        if (Compat.baubles) result.addAll(SLOTS_BAUBLES);
        if (Compat.tiamatinventory) result.addAll(SLOTS_TIAMAT_INVENTORY);
        return result;
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
