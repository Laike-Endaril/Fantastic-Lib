package com.fantasticsource.mctools.items;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class ItemFilter
{
    private ItemStack itemStack = null;
    private LinkedHashMap<String, String> tagsRequired = new LinkedHashMap<>();
    private LinkedHashMap<String, String> tagsDisallowed = new LinkedHashMap<>();


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
    public ItemFilter(String itemStackString)
    {
        this(itemStackString, false);
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
    public ItemFilter(String itemStackString, boolean suppressItemMissingError)
    {
        String[] registryAndNBT = itemStackString.trim().split(Pattern.quote(">"));
        String token;

        if (registryAndNBT.length == 0)
        {
            System.err.println("Not enough arguments for item filter: " + itemStackString);
            return;
        }
        if (registryAndNBT.length > 2)
        {
            System.err.println("Too many arguments for item filter: " + itemStackString);
            return;
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
                System.err.println("Bad item name: " + token);
                return;
            }
            if (innerTokens.length == 3)
            {
                resourceLocation = new ResourceLocation(innerTokens[0], innerTokens[1]);
                meta = Integer.parseInt(innerTokens[2]);
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
                itemStack = new ItemStack(item, 1, meta);
            }
            else
            {
                Block block = ForgeRegistries.BLOCKS.containsKey(resourceLocation) ? ForgeRegistries.BLOCKS.getValue(resourceLocation) : null;
                if (block != null) itemStack = new ItemStack(block, 1, Integer.parseInt(innerTokens[2]));
            }

            if (itemStack == null)
            {
                if (!suppressItemMissingError) System.err.println("Item for item filter not found: " + token);
                return;
            }
        }


        //NBT
        if (registryAndNBT.length > 1)
        {
            String[] tags = registryAndNBT[1].trim().split(Pattern.quote("&"));
            for (String tag : tags)
            {
                tag = tag.trim();
                if (tag.equals("")) continue;

                String[] keyValue = tag.split(Pattern.quote("="));
                if (keyValue.length > 2)
                {
                    System.err.println("Each NBT tag can only be set to one value!  Error in item filter: " + itemStackString);
                    return;
                }

                String key = keyValue[0].trim();
                if (!key.equals(""))
                {
                    if (key.charAt(0) == '!') tagsDisallowed.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
                    else tagsRequired.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
                }
            }
        }
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
                if (!(!compound.hasKey(entry.getKey()) || (entry.getValue() != null && !compound.getTag(entry.getKey()).toString().equals(entry.getValue()))))
                {
                    return false;
                }
            }
        }


        //Required NBT
        Set<Map.Entry<String, String>> entrySet = tagsRequired.entrySet();
        if (entrySet.size() > 0)
        {
            if (compound == null) return false;

            for (Map.Entry<String, String> entry : entrySet)
            {
                if (!compound.hasKey(entry.getKey()) || (entry.getValue() != null && !compound.getTag(entry.getKey()).toString().equals(entry.getValue())))
                {
                    return false;
                }
            }
        }


        //Passed all filters
        return true;
    }
}
