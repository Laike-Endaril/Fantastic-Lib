package com.fantasticsource.mctools.aw;

import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class AWSkinGenerator
{
    @GameRegistry.ObjectHolder("armourers_workshop:item.skin")
    public static Item awSkinItem;

    public static ItemStack generate(String libraryFile, String skinType, Color... dyes)
    {
        ItemStack result = new ItemStack(awSkinItem);
        NBTTagCompound compound = new NBTTagCompound();
        result.setTagCompound(compound);

        compound.setTag("armourersWorkshop", new NBTTagCompound());
        compound = compound.getCompoundTag("armourersWorkshop");

        NBTTagCompound compound2 = new NBTTagCompound();
        compound2.setString("libraryFile", libraryFile);
        compound2.setString("skinType", skinType);
        compound.setTag("identifier", compound2);

        compound2 = new NBTTagCompound();
        int i = 0;
        for (Color color : dyes)
        {
            compound2.setByte("dye" + i + "r", (byte) color.r());
            compound2.setByte("dye" + i + "g", (byte) color.g());
            compound2.setByte("dye" + i + "b", (byte) color.b());
            compound2.setByte("dye" + i + "t", (byte) color.a());
            i++;
        }
        compound.setTag("dyeData", compound2);

        return result;
    }
}
