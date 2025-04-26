package com.fantasticsource.mctools.particles;

import com.fantasticsource.tools.Tools;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class PathedParticleSharedRenderData
{
    public static final ResourceLocation PARTICLE_TEXTURE_ATLAS = new ResourceLocation("textures/particle/particles.png");
    public static final ResourceLocation BLOCK_TEXTURE_ATLAS = TextureMap.LOCATION_BLOCKS_TEXTURE;

    //Data that requires openGL state change
    public final boolean useBlockLight;
    public final GlStateManager.SourceFactor sourceFactor;
    public final GlStateManager.DestFactor destFactor;
    public final ResourceLocation texture;
    public final int hash;

    //Data that DOES NOT require openGL state change
    public final TextureAtlasSprite sprite;
    public final double u1, v1, u2, v2;

    public PathedParticleSharedRenderData(boolean useBlockLight, GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, String texture)
    {
        this.useBlockLight = useBlockLight;
        this.sourceFactor = sourceFactor;
        this.destFactor = destFactor;

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
            u1 = 0;
            v1 = 0;
            u2 = 0;
            v2 = 0;
        }
        else if (texture.startsWith("particle"))
        {
            String[] tokens = Tools.fixedSplit(texture, ",");
            this.texture = PARTICLE_TEXTURE_ATLAS;
            u1 = Double.parseDouble(tokens[1].trim()) / 128;
            v1 = Double.parseDouble(tokens[2].trim()) / 128;
            u2 = Double.parseDouble(tokens[3].trim()) / 128;
            v2 = Double.parseDouble(tokens[4].trim()) / 128;
        }
        else
        {
            this.texture = new ResourceLocation(texture);
            u1 = 0;
            v1 = 0;
            u2 = 1;
            v2 = 1;
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
