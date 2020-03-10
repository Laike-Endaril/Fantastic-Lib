package com.fantasticsource.mctools.aw;

import com.fantasticsource.fantasticlib.api.FLibAPI;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class RenderModes
{
    public static void init()
    {
        FLibAPI.attachNBTCapToEntityIf(MODID, entity -> true);
    }


    public static void setRenderMode(Entity entity, String renderModeChannel, String renderMode)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) compound.setTag("renderModes", new NBTTagCompound());
        compound = compound.getCompoundTag("renderModes");

        compound.setString(renderModeChannel, renderMode);

        TransientAWSkinHandler.refresh(entity);
    }

    public static String getRenderMode(Entity entity, String renderModeChannel)
    {
        NBTTagCompound compound = FLibAPI.getNBTCap(entity).getCompound(MODID);

        if (!compound.hasKey("renderModes")) return null;

        compound = compound.getCompoundTag("renderModes");
        if (!compound.hasKey(renderModeChannel)) return null;

        return compound.getString(renderModeChannel);
    }
}
