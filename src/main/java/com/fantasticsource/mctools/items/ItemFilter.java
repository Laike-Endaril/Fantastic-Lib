package com.fantasticsource.mctools.items;

import net.minecraft.block.Block;
import net.minecraft.init.Items;
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
    public ItemStack itemStack = null;
    private LinkedHashMap<String, String> tags = new LinkedHashMap<>();


    /**
     * Syntax is domain:item:meta > nbtkey1 = nbtvalue1 & nbtkey2 = nbtvalue2
     * All of these are optional, except that each nbt value requires a key
     * <p>
     * Domain and meta are optional as well
     * If you only want to specify a domain, add a : after it, eg....
     * minecraft:
     * <p>
     * Examples...
     * diamond_sword
     * dye:0
     * > generic.attackDamage = 10
     * minecraft: > generic.attackDamage
     */
    public ItemFilter(String itemStackString)
    {
        //Defaults
        this(type);


        String[] tokens = configEntry.split(Pattern.quote(","));
        String token;

        if (tokens.length < 2)
        {
            System.err.println("Not enough arguments for weapon entry: " + configEntry);
            return;
        }
        if (((type == TYPE_NORMAL || type == TYPE_STEALTH) && tokens.length > 6) || (type == TYPE_ASSASSINATION && tokens.length > 2))
        {
            System.err.println("Too many arguments for weapon entry: " + configEntry);
            return;
        }

        String[] registryAndNBT = tokens[0].trim().split(Pattern.quote(">"));
        if (registryAndNBT.length > 2)
        {
            System.err.println("Too many arguments for name/NBT in weapon entry: " + tokens[0]);
            return;
        }


        //Item and meta
        token = registryAndNBT[0].trim();
        if (token.equals("")) itemStack = new ItemStack(Items.AIR);
        else
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
        }

        if (itemStack == null)
        {
            if (type == TYPE_NORMAL && !AttackDefaults.normalAttackDefaults.contains(configEntry)) System.err.println("Item for normal attack weapon entry not found: " + token);
            if (type == TYPE_STEALTH && !AttackDefaults.stealthAttackDefaults.contains(configEntry)) System.err.println("Item for stealth attack weapon entry not found: " + token);
            if (type == TYPE_ASSASSINATION && !AttackDefaults.assassinationDefaults.contains(configEntry)) System.err.println("Item for assassination weapon entry not found: " + token);
            return;
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
                    System.err.println("Each NBT tag can only be set to one value!  Error in weapon entry: " + configEntry);
                    return;
                }

                String key = keyValue[0].trim();
                if (!key.equals("")) this.tags.put(key, keyValue.length == 2 ? keyValue[1].trim() : null);
            }
        }
    }

    public boolean matches(ItemStack itemStack)
    {

        if (!(this.itemStack.getItem().equals(itemStack.getItem()) && (itemStack.isItemStackDamageable() || this.itemStack.getMetadata() == itemStack.getMetadata()))) return false;

        Set<Map.Entry<String, String>> entrySet = tags.entrySet();
        if (entrySet.size() == 0) return true;

        NBTTagCompound compound = itemStack.getTagCompound();
        if (compound == null) return false;

        for (Map.Entry<String, String> entry : entrySet)
        {
            if (!compound.hasKey(entry.getKey()) || (entry.getValue() != null && !compound.getTag(entry.getKey()).toString().equals(entry.getValue())))
            {
                return false;
            }
        }
        return true;
    }
}
