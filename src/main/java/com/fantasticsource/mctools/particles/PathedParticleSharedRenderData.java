package com.fantasticsource.mctools.particles;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;

public class PathedParticleSharedRenderData
{
    public static final ResourceLocation PARTICLE_TEXTURE_ATLAS = new ResourceLocation("textures/particle/particles.png");
    public static final ResourceLocation BLOCK_TEXTURE_ATLAS = TextureMap.LOCATION_BLOCKS_TEXTURE;

    public final boolean useBlockLight;
    public final GlStateManager.SourceFactor sourceFactor;
    public final GlStateManager.DestFactor destFactor;
    public final ResourceLocation texture;
    public final int hash;

    public PathedParticleSharedRenderData(boolean useBlockLight, GlStateManager.SourceFactor sourceFactor, GlStateManager.DestFactor destFactor, ResourceLocation texture)
    {
        this.useBlockLight = useBlockLight;
        this.sourceFactor = sourceFactor;
        this.destFactor = destFactor;
        this.texture = texture;
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
