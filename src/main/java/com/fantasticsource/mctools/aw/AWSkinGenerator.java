package com.fantasticsource.mctools.aw;

import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class AWSkinGenerator
{
    protected static final String LIB_DIR = MCTools.getConfigDir() + ".." + File.separator + "armourers_workshop" + File.separator + "skin-library" + File.separator;

    @GameRegistry.ObjectHolder("armourers_workshop:item.skin")
    public static Item awSkinItem;

    public static ItemStack generate(String libraryFile, String skinType)
    {
        return generate(libraryFile, skinType, new LinkedHashMap<>());
    }

    public static ItemStack generate(String libraryFile, String skinType, LinkedHashMap<Integer, Color> dyes)
    {
        if (libraryFile == null || libraryFile.toLowerCase().equals("null")) return ItemStack.EMPTY;


        libraryFile = Tools.fixFileSeparators(libraryFile);


        ArrayList<String> otherFileNames = new ArrayList<>();
        File file = new File(LIB_DIR + libraryFile);
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files != null)
            {
                boolean defaultFound = false;
                for (File file2 : files)
                {
                    String name = file2.getAbsolutePath().replace(LIB_DIR, "").replace(".armour", "");
                    if (name.contains("@")) otherFileNames.add(name);
                    else if (!defaultFound)
                    {
                        libraryFile = name;
                        defaultFound = true;
                    }
                }
            }
        }
        else if (libraryFile.contains("@"))
        {
            otherFileNames.add(libraryFile);
            libraryFile = "";
        }


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
        for (Map.Entry<Integer, Color> entry : dyes.entrySet())
        {
            int i = entry.getKey();
            Color dye = entry.getValue();

            compound2.setByte("dye" + i + "r", (byte) dye.r());
            compound2.setByte("dye" + i + "g", (byte) dye.g());
            compound2.setByte("dye" + i + "b", (byte) dye.b());
            compound2.setByte("dye" + i + "t", (byte) dye.a());
        }
        compound.setTag("dyeData", compound2);


        for (String name : otherFileNames)
        {
            String renderModeTags = name.substring(name.indexOf("@") + 1);
            ArrayList<Pair<String, String>> reqs = new ArrayList<>();
            for (String requirement : Tools.fixedSplit(renderModeTags, "@"))
            {
                String[] tokens = Tools.fixedSplit(requirement, "[.]");
                reqs.add(new Pair<>(tokens[0], tokens[1]));
            }
            RenderModes.addRenderModeToSkin(result, reqs, skinType, name, dyes);
        }


        return result;
    }
}
