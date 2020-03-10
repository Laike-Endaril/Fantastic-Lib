package com.fantasticsource.mctools.items;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ItemFilter
{
    private ItemStack itemStack = null;
    private LinkedHashMap<String, String> tagsRequired = new LinkedHashMap<>();
    private LinkedHashMap<String, String> tagsDisallowed = new LinkedHashMap<>();


    private ItemFilter()
    {
    }


    /**
     * Syntax is domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2
     * All of these are optional except item
     * <p>
     * Each nbt value requires a key, but not necessarily a value (if no value is specified, it just checks if the key exists)
     * Each NBT entry can be negated by starting it with a !
     * eg...
     * !generic.attackDamage
     * !generic.attackDamage = 4
     * <p>
     * Examples...
     * diamond_sword
     * dye:0
     * tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material
     */
    public static ItemFilter getInstance(String itemStackString)
    {
        return getInstance(itemStackString, false);
    }

    /**
     * Syntax is domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2
     * All of these are optional except item
     * <p>
     * Each nbt value requires a key, but not necessarily a value (if no value is specified, it just checks if the key exists)
     * Each NBT entry can be negated by starting it with a !
     * eg...
     * !generic.attackDamage
     * !generic.attackDamage = 4
     * <p>
     * Examples...
     * diamond_sword
     * dye:0
     * tetra:duplex_tool_modular > duplex/sickle_left_material & duplex/butt_right_material
     */
    public static ItemFilter getInstance(String itemStackString, boolean suppressItemMissingError)
    {
        ItemFilter result = new ItemFilter();

        String[] registryAndNBT = itemStackString.trim().split(Pattern.quote(">"));
        String token;

        if (registryAndNBT.length == 0)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.notEnoughItemFilterArgs", itemStackString));
            return null;
        }
        if (registryAndNBT.length > 2)
        {
            System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.tooManyItemFilterArgs", itemStackString));
            return null;
        }


        //Item and meta
        token = registryAndNBT[0].trim();
        if (!token.equals(""))
        {
            ResourceLocation resourceLocation;
            int meta = 0;

            String[] innerTokens = token.split(Pattern.quote(":"));
            if (innerTokens.length > 3)
            {
                System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.badItemName", token));
                return null;
            }
            if (innerTokens.length == 3)
            {
                resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);

                try
                {
                    meta = Integer.parseInt(innerTokens[2]);
                }
                catch (NumberFormatException e)
                {
                    System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.badItemMeta", token));
                    return null;
                }
            }
            else if (innerTokens.length == 1) resourceLocation = new ResourceLocation("minecraft", innerTokens[0]);
            else
            {
                try
                {
                    meta = Integer.parseInt(innerTokens[1]);
                    resourceLocation = new ResourceLocation("minecraft", innerTokens[0]);
                }
                catch (NumberFormatException e)
                {
                    meta = 0;
                    resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);
                }
            }


            Item item = ForgeRegistries.ITEMS.getValue(resourceLocation);
            if (item != null)
            {
                result.itemStack = new ItemStack(item, 1, meta);
            }
            else
            {
                Block block = ForgeRegistries.BLOCKS.containsKey(resourceLocation) ? ForgeRegistries.BLOCKS.getValue(resourceLocation) : null;
                if (block != null) result.itemStack = new ItemStack(block, 1, meta);
            }

            if (result.itemStack == null)
            {
                if (!suppressItemMissingError) System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.itemForFilterNotFound", token));
                return null;
            }
        }


        //NBT
        if (registryAndNBT.length > 1)
        {
            String nbt = registryAndNBT[1].trim();
            for (Map.Entry<ResourceLocation, Enchantment> entry : ForgeRegistries.ENCHANTMENTS.getEntries())
            {
                nbt = nbt.replaceAll(entry.getKey().toString(), "id:" + Enchantment.getEnchantmentID(entry.getValue()) + "s");
            }

            String[] tags = nbt.split(Pattern.quote("&"));
            for (String tag : tags)
            {
                tag = tag.trim();
                if (tag.equals("")) continue;

                String[] keyValue = tag.split(Pattern.quote("="));
                if (keyValue.length > 2)
                {
                    System.err.println(I18n.translateToLocalFormatted(FantasticLib.MODID + ".error.tooManyNBTValues", itemStackString));
                    return null;
                }

                String key = keyValue[0].trim();
                if (!key.equals(""))
                {
                    LinkedHashMap<String, String> map;
                    if (key.charAt(0) == '!')
                    {
                        key = key.substring(1);
                        map = result.tagsDisallowed;
                    }
                    else map = result.tagsRequired;

                    map.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
                }
            }
        }

        return result;
    }


    public ItemStack getItemStack()
    {
        return itemStack;
    }

    public boolean matches(ItemStack stack)
    {
        //Domain, item, and meta
        if (!(itemStack.getItem().equals(stack.getItem()) && (stack.isItemStackDamageable() || itemStack.getMetadata() == stack.getMetadata()))) return false;


        //Disallowed NBT
        NBTTagCompound compound = stack.getTagCompound();

        if (compound != null)
        {
            for (Map.Entry<String, String> entry : tagsDisallowed.entrySet())
            {
                if (checkNBT(compound, entry.getKey().split(":", -1), entry.getValue())) return false;
            }
        }


        //Required NBT
        Set<Map.Entry<String, String>> entrySet = tagsRequired.entrySet();
        if (entrySet.size() > 0)
        {
            if (compound == null) return false;

            for (Map.Entry<String, String> entry : entrySet)
            {
                if (!checkNBT(compound, entry.getKey().split(":", -1), entry.getValue())) return false;
            }
        }


        //Passed all filters
        return true;
    }

    private boolean checkNBT(NBTBase base, String[] keymap, String value)
    {
        if (keymap == null || keymap.length == 0)
        {
            return checkValue(base, value);
        }


        String key = keymap[0].trim();

        if (key.equals(""))
        {
            if (base instanceof NBTTagList)
            {
                for (NBTBase base1 : (NBTTagList) base)
                {
                    if (checkNBT(base1, Arrays.copyOfRange(keymap, 1, keymap.length), value)) return true;
                }
            }

            return false;
        }

        if (base instanceof NBTTagCompound)
        {
            NBTTagCompound compound = (NBTTagCompound) base;
            if (!compound.hasKey(key)) return false;

            return checkNBT(compound.getTag(key), Arrays.copyOfRange(keymap, 1, keymap.length), value);
        }

        return false;
    }

    private boolean checkValue(NBTBase base, String value)
    {
        if (value == null || value.trim().equals(base.toString())) return true;


        value = value.replace(";", ",");
        if (value.equals(base.toString())) return true;

        String[] newValues = value.split(",");
        if (newValues.length < 2 && !value.contains(":")) return false;

        for (String newValue : newValues)
        {
            String[] tokens = newValue.split(":");
            if (!checkNBT(base, Arrays.copyOf(tokens, tokens.length - 1), tokens[tokens.length - 1])) return false;
        }

        return true;
    }
}
