package com.fantasticsource.mctools.particles;

import com.fantasticsource.tools.SpriteMetaData;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;

public class PathedParticle
{
    //Cloned
    public final PathedParticleSharedRenderData sharedRenderData;

    protected int maxAge = 20;

    protected CPath.CPathData basePath, rgbPath = null, hsvPath = null, alphaPath = null, scale3DPath = null, rotationPath = null, animationPath = null;
    protected ArrayList<CPath.CPathData> morePaths = new ArrayList<>();

    protected ArrayList<PathedParticleFactory>[] onDeathParticles = new ArrayList[2];
    public SpriteMetaData spriteMetaData = null;


    //Uncloned
    protected int age = 0;
    protected VectorN offset = new VectorN(0, 0, 0);


    public PathedParticle(PathedParticleSharedRenderData sharedRenderData, CPath basePath, CPath... morePaths)
    {
        this.sharedRenderData = sharedRenderData;

        this.basePath = new CPath.CPathData(basePath, 0);
        for (CPath path : morePaths) applyPath(path);

        PathedParticleManager.add(this);
    }


    public PathedParticle kill()
    {
        age = maxAge;
        return this;
    }


    public PathedParticle setMaxAge(int maxAge)
    {
        this.maxAge = maxAge;
        return this;
    }


    public PathedParticle applyPath(CPath path)
    {
        morePaths.add(new CPath.CPathData(path, 0));
        return this;
    }


    public PathedParticle rgbPath(CPath path)
    {
        rgbPath = new CPath.CPathData(path, 0);
        return this;
    }

    public PathedParticle hsvPath(CPath path)
    {
        hsvPath = new CPath.CPathData(path, 0);
        return this;
    }

    public PathedParticle alphaPath(CPath path)
    {
        alphaPath = new CPath.CPathData(path, 0);
        return this;
    }


    public PathedParticle scale3DPath(CPath path)
    {
        scale3DPath = new CPath.CPathData(path, 0);
        return this;
    }


    public PathedParticle rotationPath(CPath path)
    {
        rotationPath = new CPath.CPathData(path, 0);
        return this;
    }


    public PathedParticle animationPath(CPath path)
    {
        animationPath = new CPath.CPathData(path);
        return this;
    }


    public PathedParticle addOnDeathParticles(boolean atDeathPosition, PathedParticleFactory... particleFactories)
    {
        int index = atDeathPosition ? 0 : 1;
        ArrayList<PathedParticleFactory> list = onDeathParticles[index];
        if (list == null)
        {
            list = new ArrayList<>();
            onDeathParticles[index] = list;
        }

        list.addAll(Arrays.asList(particleFactories));

        return this;
    }


    public void onUpdate()
    {
        if (++age == maxAge)
        {
            //Natural death
            if (onDeathParticles[0] != null)
            {
                VectorN pos = currentPos(0);
                PathedParticle particle;
                for (PathedParticleFactory particleFactory : onDeathParticles[0])
                {
                    particle = particleFactory.create();
                    particle.offset = pos.copy().subtract(particle.currentPos(0));
                }
            }
            if (onDeathParticles[1] != null)
            {
                for (PathedParticleFactory particleFactory : onDeathParticles[1]) particleFactory.create();
            }
        }
    }


    protected VectorN currentPos(float partialTick)
    {
        long millis = (long) ((partialTick + age) * 1000 / maxAge);

        VectorN pos = basePath.getRelativePosition(millis), pathPos;
        if (pos == null) return null;

        for (CPath.CPathData data : morePaths)
        {
            pathPos = data.getRelativePosition(millis);
            if (pathPos == null) return null;

            pos.add(pathPos);
        }
        return pos.add(offset);
    }


    public void renderParticle(BufferBuilder buffer, float partialTick, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        if (Minecraft.getMinecraft().world == null) age = maxAge;
        if (age >= maxAge) return;


        //Normalize all path progress over the course of the particle lifetime
        long renderMillis = (long) ((partialTick + age) * 1000 / maxAge);


        VectorN pos = currentPos(partialTick);
        if (pos == null)
        {
            age = maxAge;
            return;
        }


        double x = pos.values[0] - Particle.interpPosX;
        double y = pos.values[1] - Particle.interpPosY;
        double z = pos.values[2] - Particle.interpPosZ;

        double xScale3D = 0.05, yScale3D = 0.05, zScale3D = 0.05;
        if (scale3DPath != null)
        {
            VectorN scalar = scale3DPath.getRelativePosition(renderMillis);
            xScale3D *= scalar.values[0];
            yScale3D *= scalar.values[1];
            zScale3D *= scalar.values[2];
        }

        VectorN[] posOffsets = new VectorN[]
                {
                        new VectorN((-rotationX - rotationXY), -rotationZ, (-rotationYZ - rotationXZ)),
                        new VectorN((-rotationX + rotationXY), rotationZ, (-rotationYZ + rotationXZ)),
                        new VectorN((rotationX + rotationXY), rotationZ, (rotationYZ + rotationXZ)),
                        new VectorN((rotationX - rotationXY), -rotationZ, (rotationYZ - rotationXZ))
                };

        if (rotationPath != null)
        {
            float theta = (float) (rotationPath.getRelativePosition(renderMillis).values[0] * 0.5f);
            float sinTheta = MathHelper.sin(theta), cosTheta = MathHelper.cos(theta);
            VectorN rotationScalars = new VectorN(sinTheta * Particle.cameraViewDir.x, sinTheta * Particle.cameraViewDir.y, sinTheta * Particle.cameraViewDir.z);

            for (int i = 0; i < 4; ++i)
            {
                posOffsets[i] = rotationScalars.copy().scale(2 * posOffsets[i].dotProduct(rotationScalars))
                        .add(posOffsets[i].copy().scale(cosTheta * cosTheta - rotationScalars.dotProduct(rotationScalars)))
                        .add(rotationScalars.copy().crossProduct(posOffsets[i]).scale(2 * cosTheta));
            }
        }


        World world = Minecraft.getMinecraft().world;
        BlockPos blockpos = new BlockPos(x, y, z);
        int lightmapIndex = world.isBlockLoaded(blockpos) ? world.getCombinedLight(blockpos, 0) : 0;
        int skyLight = lightmapIndex >> 16 & 65535;
        int blockLight = lightmapIndex & 65535;


        float r, g, b;
        if (rgbPath != null)
        {
            VectorN rgb = rgbPath.getRelativePosition(renderMillis);
            r = (float) rgb.values[0];
            g = (float) rgb.values[1];
            b = (float) rgb.values[2];
        }
        else if (hsvPath != null)
        {
            VectorN hsv = hsvPath.getRelativePosition(renderMillis);
            Color c = new Color(0).setColorHSV((float) hsv.values[0], (float) hsv.values[1], (float) hsv.values[2]);
            r = c.rf();
            g = c.gf();
            b = c.bf();
        }
        else
        {
            r = 1;
            g = 1;
            b = 1;
        }

        float a = alphaPath == null ? 1 : (float) alphaPath.getRelativePosition(renderMillis).values[0];


        //DO NOT try to change block texture animation (it won't work "correctly"); if someone wants per-particle animation using a block texture, they'll need to reference it as an "other" texture
        double u1, v1, u2, v2;
        if (spriteMetaData != null)
        {
            SpriteMetaData.FrameMetaData frame;
            if (animationPath != null)
            {
                frame = spriteMetaData.frames.get((int) (spriteMetaData.frames.size() * animationPath.getRelativePosition(renderMillis).values[0]));
            }
            else
            {
                frame = spriteMetaData.frames.get(spriteMetaData.frames.size() * age / maxAge);
            }
            u1 = frame.u1;
            v1 = frame.v1;
            u2 = frame.u2;
            v2 = frame.v2;
        }
        else if (sharedRenderData.sprite != null)
        {
            u1 = sharedRenderData.sprite.getMinU();
            v1 = sharedRenderData.sprite.getMinV();
            u2 = sharedRenderData.sprite.getMaxU();
            v2 = sharedRenderData.sprite.getMaxV();
        }
        else
        {
            u1 = 0;
            v1 = 0;
            u2 = 1;
            v2 = 1;
        }


        buffer.pos(x + posOffsets[0].values[0] * xScale3D, y + posOffsets[0].values[1] * yScale3D, z + posOffsets[0].values[2] * zScale3D).tex(u2, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[1].values[0] * xScale3D, y + posOffsets[1].values[1] * yScale3D, z + posOffsets[1].values[2] * zScale3D).tex(u2, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[2].values[0] * xScale3D, y + posOffsets[2].values[1] * yScale3D, z + posOffsets[2].values[2] * zScale3D).tex(u1, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[3].values[0] * xScale3D, y + posOffsets[3].values[1] * yScale3D, z + posOffsets[3].values[2] * zScale3D).tex(u1, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
    }
}
