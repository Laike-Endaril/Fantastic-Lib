package com.fantasticsource.mctools.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class PathedParticleSharedRenderData
{
    public static final ResourceLocation BLOCK_TEXTURE_ATLAS = TextureMap.LOCATION_BLOCKS_TEXTURE;

    //Data that requires openGL state change
    public final boolean useBlockLight;
    public final GlStateManager.SourceFactor sourceFactor;
    public final GlStateManager.DestFactor destFactor;
    public final String textureString;
    public final ResourceLocation texture;
    public final int hash;

    //Data that DOES NOT require openGL state change
    public final TextureAtlasSprite sprite;

    public PathedParticleSharedRenderData(boolean useBlockLight, GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, String texture)
    {
        this.useBlockLight = useBlockLight;
        this.sourceFactor = sourceFactor;
        this.destFactor = destFactor;
        textureString = texture;

        try
        {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getTextureExtry(texture);
        }
        catch (NullPointerException e)
        {
            throw new IllegalStateException("Tried to create PathedParticleSharedRenderData too early!  Cannot be created before FMLInitializationEvent!");
        }

        if (sprite != null)
        {
            this.texture = BLOCK_TEXTURE_ATLAS;
        }
        else
        {
            this.texture = new ResourceLocation(texture);
        }

        hash = ((useBlockLight ? 1 : 0) << 31) | (sourceFactor.ordinal() << 27) | (destFactor.ordinal() << 23) | (texture.hashCode() & 0b00000000_01111111_11111111_11111111);
    }

    @Override
    public int hashCode()
    {
        return hash;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj == this) return true;
        if (!(obj instanceof PathedParticleSharedRenderData)) return false;
        return (((PathedParticleSharedRenderData) obj).hash == hash && ((PathedParticleSharedRenderData) obj).texture.equals(texture));
    }
}
