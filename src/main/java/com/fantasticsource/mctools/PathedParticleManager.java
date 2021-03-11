package com.fantasticsource.mctools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PathedParticleManager
{
    protected static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
    protected static final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
    protected static final TextureManager renderer = Minecraft.getMinecraft().renderEngine;
    protected static final ArrayList<Particle> particles = new ArrayList<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(PathedParticleManager.class);
    }

    public static void add(Particle particle)
    {
        particles.add(particle);
    }

    public static void update()
    {
        particles.removeIf(particle ->
        {
            particle.onUpdate();
            return !particle.isAlive();
        });
    }

    public static void render(Entity entityIn, float partialTick)
    {
        if (particles.size() == 0) return;

        float f1 = MathHelper.cos(entityIn.rotationYaw * 0.017453292f);
        float f2 = MathHelper.sin(entityIn.rotationYaw * 0.017453292f);
        float f3 = -f2 * MathHelper.sin(entityIn.rotationPitch * 0.017453292f);
        float f4 = f1 * MathHelper.sin(entityIn.rotationPitch * 0.017453292f);
        float f5 = MathHelper.cos(entityIn.rotationPitch * 0.017453292f);

        renderer.bindTexture(PARTICLE_TEXTURES);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
        for (Particle particle : particles)
        {
            particle.renderParticle(bufferbuilder, entityIn, partialTick, f1, f5, f2, f3, f4);
        }
        tessellator.draw();
    }


    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;

        if (!Minecraft.getMinecraft().isGamePaused()) update();
    }

    @SubscribeEvent
    public static void renderLast(RenderWorldLastEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        Profiler profiler = mc.mcProfiler;

        profiler.startSection("FLib: litParticles");
        render(Minecraft.getMinecraft().getRenderViewEntity(), event.getPartialTicks());
        profiler.endSection();
    }
}