package com.fantasticsource.mctools.particles;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.tools.SpriteMetaData;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class PathedParticle
{
    //Cloned
    public final PathedParticleSharedRenderData sharedRenderData;

    public int maxAge = 20;

    public CPath.CPathData basePath, rgbPath = null, hsvPath = null, alphaPath = null, scale2DPath = null, scale3DPath = null, rotationPath = null, animationPath = null;
    public ArrayList<CPath.CPathData> morePaths = new ArrayList<>();

    public ArrayList<Predicate<PathedParticle>> deathConditions = new ArrayList<>();
    public ArrayList<PathedParticleFactory>[] onDeathParticles = new ArrayList[2];

    public SpriteMetaData spriteMetaData = null;


    //Uncloned
    protected boolean dead = false;
    protected int age = 0;
    protected VectorN offset = new VectorN(0, 0, 0);


    public PathedParticle(PathedParticleSharedRenderData sharedRenderData, CPath basePath, CPath... morePaths)
    {
        this.sharedRenderData = sharedRenderData;

        this.basePath = new CPath.CPathData(basePath, 0);
        for (CPath path : morePaths) applyPath(path);

        PathedParticleManager.add(this);
    }


    public PathedParticle die()
    {
        //Natural death
        dead = true;

        if (onDeathParticles[0] != null)
        {
            VectorN pos = currentPos(0);
            PathedParticle particle;
            for (PathedParticleFactory particleFactory : onDeathParticles[0])
            {
                particle = particleFactory.create(this);
                particle.offset = pos.copy().subtract(particle.currentPos(0));
            }
        }
        if (onDeathParticles[1] != null)
        {
            for (PathedParticleFactory particleFactory : onDeathParticles[1]) particleFactory.create(this);
        }

        return this;
    }

    public PathedParticle delete()
    {
        //Deletion, not death; don't call on-death stuff
        dead = true;
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


    public PathedParticle scale2DPath(CPath path)
    {
        if (path.getRelativePosition(0).values.length != 2) throw new IllegalArgumentException("The 2D scale path must be 2D!");

        scale2DPath = new CPath.CPathData(path, 0);
        return this;
    }

    public PathedParticle scale3DPath(CPath path)
    {
        if (path.getRelativePosition(0).values.length != 3) throw new IllegalArgumentException("The 3D scale path must be 3D!");

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


    public PathedParticle dieOnSolids()
    {
        deathConditions.add(particle ->
        {
            double[] from = currentPos(0).values, to = nextPosition(0).values;
            return !ImprovedRayTracing.isUnobstructed(Minecraft.getMinecraft().world, new Vec3d(from[0], from[1], from[2]), new Vec3d(to[0], to[1], to[2]), true);
        });
        return this;
    }

    public PathedParticle dieOnLiquids()
    {
        return this;
    }

    public PathedParticle addDeathConditions(Predicate<PathedParticle>... conditions)
    {
        deathConditions.addAll(Arrays.asList(conditions));
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
        boolean shouldDie = ++age >= maxAge;
        for (Predicate<PathedParticle> predicate : deathConditions)
        {
            if (predicate.test(this)) shouldDie = true;
        }

        if (shouldDie) die();
    }


    public long currentRenderMillis(float partialTick)
    {
        return (long) ((partialTick + age) * 1000 / maxAge);
    }


    public VectorN prevPosition(float partialTick)
    {
        return positionAtAge(age - 1, partialTick);
    }

    public VectorN nextPosition(float partialTick)
    {
        return positionAtAge(age + 1, partialTick);
    }

    public VectorN positionAtAge(int age, float partialTick)
    {
        int a = this.age;
        this.age = age;
        VectorN result = currentPos(partialTick);
        this.age = a;
        return result;
    }

    public VectorN currentPos(float partialTick)
    {
        long millis = currentRenderMillis(partialTick);

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


    //The letter before "Scale" is the axis scaling will happen on in the original 2D texture
    //The letter before "Factor" is what coordinate of the normalized rotated scalar vector is factoring into the equation
    public void renderParticle(BufferBuilder buffer, float partialTick, float xScaleXFactor, float yScaleYFactor, float xScaleZFactor, float yScaleZFactor, float yScaleXFactor)
    {
        if (Minecraft.getMinecraft().world == null) dead = true;
        if (dead) return;


        //Normalize all path progress over the course of the particle lifetime
        long renderMillis = currentRenderMillis(partialTick);


        VectorN pos = currentPos(partialTick);
        if (pos == null)
        {
            dead = true;
            return;
        }


        double x = pos.values[0] - Particle.interpPosX;
        double y = pos.values[1] - Particle.interpPosY;
        double z = pos.values[2] - Particle.interpPosZ;


        //DO NOT try to change block texture animation (it won't work "correctly"); if someone wants per-particle animation using a block texture, they'll need to reference it as an "other" texture
        double u1, v1, u2, v2, xOrigin, yOrigin;
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

            xOrigin = frame.relativeOriginX;
            yOrigin = frame.relativeOriginY;
        }
        else
        {
            TextureAtlasSprite sprite = sharedRenderData.sprite;
            if (sprite != null)
            {
                u1 = sprite.getMinU();
                v1 = sprite.getMinV();
                u2 = sprite.getMaxU();
                v2 = sprite.getMaxV();
            }
            else
            {
                u1 = 0;
                v1 = 0;
                u2 = 1;
                v2 = 1;
            }

            xOrigin = 0.5;
            yOrigin = 0.5;
        }


        double xScale3D = 0.05, yScale3D = 0.05, zScale3D = 0.05;
        if (scale3DPath != null)
        {
            VectorN scalar = scale3DPath.getRelativePosition(renderMillis);
            xScale3D *= scalar.values[0];
            yScale3D *= scalar.values[1];
            zScale3D *= scalar.values[2];
        }

        if (scale2DPath != null)
        {
            VectorN scalar = scale2DPath.getRelativePosition(renderMillis);
            xScaleXFactor *= scalar.values[0];
            xScaleZFactor *= scalar.values[0];
            yScaleYFactor *= scalar.values[1];
            yScaleXFactor *= scalar.values[1];
            yScaleZFactor *= scalar.values[1];
        }

        VectorN[] posOffsets;
        if (xOrigin == 0.5 && yOrigin == 0.5) posOffsets = new VectorN[]
                {
                        new VectorN(-xScaleXFactor - yScaleZFactor, -yScaleYFactor, -xScaleZFactor - yScaleXFactor),
                        new VectorN(-xScaleXFactor + yScaleZFactor, yScaleYFactor, -xScaleZFactor + yScaleXFactor),
                        new VectorN(xScaleXFactor + yScaleZFactor, yScaleYFactor, xScaleZFactor + yScaleXFactor),
                        new VectorN(xScaleXFactor - yScaleZFactor, -yScaleYFactor, xScaleZFactor - yScaleXFactor)
                };
        else posOffsets = new VectorN[]
                {
                        new VectorN(-xScaleXFactor * xOrigin - yScaleZFactor * yOrigin, -yScaleYFactor * yOrigin, -xScaleZFactor * xOrigin - yScaleXFactor * yOrigin),
                        new VectorN(-xScaleXFactor * xOrigin + yScaleZFactor * (1 - yOrigin), yScaleYFactor * (1 - yOrigin), -xScaleZFactor * xOrigin + yScaleXFactor * (1 - yOrigin)),
                        new VectorN(xScaleXFactor * (1 - xOrigin) + yScaleZFactor * (1 - yOrigin), yScaleYFactor * (1 - yOrigin), xScaleZFactor * (1 - xOrigin) + yScaleXFactor * (1 - yOrigin)),
                        new VectorN(xScaleXFactor * (1 - xOrigin) - yScaleZFactor * yOrigin, -yScaleYFactor * yOrigin, xScaleZFactor * (1 - xOrigin) - yScaleXFactor * yOrigin)
                };

        if (rotationPath != null)
        {
            float theta = (float) (rotationPath.getRelativePosition(renderMillis).values[0] * 0.5f);
            float sinTheta = MathHelper.sin(theta), cosTheta = MathHelper.cos(theta);
            VectorN rotationScalars = new VectorN(sinTheta * Particle.cameraViewDir.x, sinTheta * Particle.cameraViewDir.y, sinTheta * Particle.cameraViewDir.z);
            double rotScalMagSqr = rotationScalars.getMagnitudeSquared();

            for (int i = 0; i < 4; ++i)
            {
                posOffsets[i] = rotationScalars.copy().scale(2 * posOffsets[i].dotProduct(rotationScalars))
                        .add(posOffsets[i].copy().scale(cosTheta * cosTheta - rotScalMagSqr))
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


        buffer.pos(x + posOffsets[0].values[0] * xScale3D, y + posOffsets[0].values[1] * yScale3D, z + posOffsets[0].values[2] * zScale3D).tex(u2, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[1].values[0] * xScale3D, y + posOffsets[1].values[1] * yScale3D, z + posOffsets[1].values[2] * zScale3D).tex(u2, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[2].values[0] * xScale3D, y + posOffsets[2].values[1] * yScale3D, z + posOffsets[2].values[2] * zScale3D).tex(u1, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[3].values[0] * xScale3D, y + posOffsets[3].values[1] * yScale3D, z + posOffsets[3].values[2] * zScale3D).tex(u1, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
    }
}
