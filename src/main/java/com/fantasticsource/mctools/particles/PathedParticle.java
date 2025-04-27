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

    protected ArrayList<PathedParticle>[] onDeathParticles = new ArrayList[2];
    public SpriteMetaData spriteMetaData = null;


    //Uncloned
    protected int age = 0;
    protected VectorN offset = new VectorN(0, 0, 0);


    public PathedParticle(PathedParticleSharedRenderData sharedRenderData, CPath basePath, CPath... morePaths)
    {
        this.sharedRenderData = sharedRenderData;

        this.basePath = new CPath.CPathData(basePath, 0);
        for (CPath path : morePaths) applyPath(path);
    }


    public PathedParticle clone()
    {
        PathedParticle other = new PathedParticle(sharedRenderData, basePath.path);
        for (CPath.CPathData data : morePaths) other.applyPath(data.path);

        if (rgbPath != null) other.rgbPath(rgbPath.path);
        if (hsvPath != null) other.hsvPath(hsvPath.path);
        if (alphaPath != null) other.alphaPath(alphaPath.path);
        if (scale3DPath != null) other.scale3DPath(scale3DPath.path);
        if (rotationPath != null) other.rotationPath(rotationPath.path);
        if (animationPath != null) other.animationPath(animationPath.path);

        other.maxAge = maxAge;
        other.spriteMetaData = spriteMetaData;

        if (onDeathParticles[0] != null) other.onDeathParticles[0] = new ArrayList<>(onDeathParticles[0]);
        if (onDeathParticles[1] != null) other.onDeathParticles[1] = new ArrayList<>(onDeathParticles[1]);

        return other;
    }


    public PathedParticle create()
    {
        PathedParticleManager.add(this);
        return this;
    }

    public PathedParticle createClone()
    {
        return clone().create();
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


    public PathedParticle addOnDeathParticles(boolean atDeathPosition, PathedParticle... particles)
    {
        int index = atDeathPosition ? 0 : 1;
        ArrayList<PathedParticle> list = onDeathParticles[index];
        if (list == null)
        {
            list = new ArrayList<>();
            onDeathParticles[index] = list;
        }

        list.addAll(Arrays.asList(particles));

        return this;
    }


    public void onUpdate()
    {
        if (++age == maxAge)
        {
            //Natural death
            if (onDeathParticles[0] != null)
            {
                age = 0;
                VectorN pos = currentPos();
                age = maxAge;

                for (PathedParticle particle : onDeathParticles[0])
                {
                    particle = particle.createClone();
                    particle.offset = pos.copy().subtract(particle.currentPos());
                }
            }
            if (onDeathParticles[1] != null)
            {
                for (PathedParticle particle : onDeathParticles[1]) particle.createClone();
            }
        }
    }


    protected VectorN currentPos()
    {
        long tickStartMillis = (long) (age * 1000 / maxAge);

        VectorN pos = basePath.getRelativePosition(tickStartMillis), pathPos;
        if (pos == null) return null;

        for (CPath.CPathData data : morePaths)
        {
            pathPos = data.getRelativePosition(tickStartMillis);
            if (pathPos == null) return null;

            pos.add(pathPos);
        }
        return pos.add(offset);
    }


    public void renderParticle(BufferBuilder buffer, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        if (Minecraft.getMinecraft().world == null) age = maxAge;
        if (age >= maxAge) return;


        //Normalize all path progress over the course of the particle lifetime
        long renderMillis = (long) ((age * 50 + partialTicks * 50) * 20 / maxAge);


        VectorN pos = currentPos();
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

        Vec3d[] vecs = new Vec3d[]
                {
                        new Vec3d((-rotationX - rotationXY) * xScale3D, -rotationZ * yScale3D, (-rotationYZ - rotationXZ) * zScale3D),
                        new Vec3d((-rotationX + rotationXY) * xScale3D, rotationZ * yScale3D, (-rotationYZ + rotationXZ) * zScale3D),
                        new Vec3d((rotationX + rotationXY) * xScale3D, rotationZ * yScale3D, (rotationYZ + rotationXZ) * zScale3D),
                        new Vec3d((rotationX - rotationXY) * xScale3D, -rotationZ * yScale3D, (rotationYZ - rotationXZ) * zScale3D)
                };

        if (rotationPath != null)
        {
            float theta = (float) (rotationPath.getRelativePosition(renderMillis).values[0] * 0.5f);
            float cosTheta = MathHelper.cos(theta);
            Vec3d vec3d = new Vec3d(MathHelper.sin(theta) * Particle.cameraViewDir.x, MathHelper.sin(theta) * Particle.cameraViewDir.y, MathHelper.sin(theta) * Particle.cameraViewDir.z);

            for (int i = 0; i < 4; ++i)
            {
                vecs[i] = vec3d.scale(2 * vecs[i].dotProduct(vec3d)).add(vecs[i].scale(cosTheta * cosTheta - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vecs[i]).scale(2 * cosTheta));
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


        buffer.pos(x + vecs[0].x, y + vecs[0].y, z + vecs[0].z).tex(u2, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + vecs[1].x, y + vecs[1].y, z + vecs[1].z).tex(u2, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + vecs[2].x, y + vecs[2].y, z + vecs[2].z).tex(u1, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + vecs[3].x, y + vecs[3].y, z + vecs[3].z).tex(u1, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
    }
}
