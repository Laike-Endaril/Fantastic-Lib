package com.fantasticsource.mctools.particles;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@SideOnly(Side.CLIENT)
public class PathedParticleManager
{
    protected static final Profiler profiler = Minecraft.getMinecraft().mcProfiler;
    protected static final TextureManager renderer = Minecraft.getMinecraft().renderEngine;
    protected static LinkedHashMap<PathedParticleSharedRenderData, ArrayList<PathedParticle>> particles = new LinkedHashMap<>();

    static
    {
        MinecraftForge.EVENT_BUS.register(PathedParticleManager.class);
    }

    public static void add(PathedParticle particle)
    {
        particles.computeIfAbsent(particle.sharedRenderData, o -> new ArrayList<>()).add(particle);
    }

    public static void update()
    {
        ArrayList<PathedParticle> list;
        for (Map.Entry<PathedParticleSharedRenderData, ArrayList<PathedParticle>> entry : particles.entrySet())
        {
            list = entry.getValue();
            list.removeIf(particle ->
            {
                particle.onUpdate();
                return !particle.isAlive();
            });
            if (list.size() == 0) particles.remove(entry.getKey());
        }
    }

    public static void render(float partialTick)
    {
        if (particles.size() == 0) return;


        Entity renderEntity = Minecraft.getMinecraft().getRenderViewEntity();
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();


        Particle.interpPosX = renderEntity.lastTickPosX + (renderEntity.posX - renderEntity.lastTickPosX) * partialTick;
        Particle.interpPosY = renderEntity.lastTickPosY + (renderEntity.posY - renderEntity.lastTickPosY) * partialTick;
        Particle.interpPosZ = renderEntity.lastTickPosZ + (renderEntity.posZ - renderEntity.lastTickPosZ) * partialTick;
        Particle.cameraViewDir = renderEntity.getLook(partialTick);


        float yawRadians = renderEntity.rotationYaw, pitchRadians = renderEntity.rotationPitch;
        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2)
        {
            yawRadians += 180;
            if (yawRadians > 180) yawRadians -= 360;
            pitchRadians = -pitchRadians;
        }
        yawRadians *= 0.017453292f;
        pitchRadians *= 0.017453292f;

        float f1 = MathHelper.cos(yawRadians);
        float f2 = MathHelper.sin(yawRadians);
        float f3 = -f2 * MathHelper.sin(pitchRadians);
        float f4 = f1 * MathHelper.sin(pitchRadians);
        float f5 = MathHelper.cos(pitchRadians);


        GlStateManager.depthMask(false);
        GlStateManager.enableBlend();
        GlStateManager.enableLighting();

        PathedParticleSharedRenderData data;
        for (Map.Entry<PathedParticleSharedRenderData, ArrayList<PathedParticle>> entry : particles.entrySet())
        {
            data = entry.getKey();
            GlStateManager.blendFunc(data.sourceFactor, data.destFactor);
            renderer.bindTexture(data.texture);
            if (data.useBlockLight)
            {
                Minecraft.getMinecraft().entityRenderer.enableLightmap();
                RenderHelper.enableStandardItemLighting();
            }
            else
            {
                Minecraft.getMinecraft().entityRenderer.disableLightmap();
                RenderHelper.disableStandardItemLighting();
            }

            bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
            for (PathedParticle particle : entry.getValue())
            {
                particle.renderParticle(bufferbuilder, renderEntity, partialTick, f1, f5, f2, f3, f4);
            }
            tessellator.draw();
        }

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        Minecraft.getMinecraft().entityRenderer.disableLightmap();
        GlStateManager.disableBlend();
        GlStateManager.depthMask(true);
    }


    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START) return;

        if (!Minecraft.getMinecraft().isGamePaused())
        {
            profiler.startSection("FLib: Pathed Particles Update");
            update();
            profiler.endSection();
        }
    }

    @SubscribeEvent
    public static void renderLast(RenderWorldLastEvent event)
    {
        profiler.startSection("FLib: Pathed Particles Render");
        render(event.getPartialTicks());
        profiler.endSection();
    }
}