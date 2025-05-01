package com.fantasticsource.mctools.particles;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.tools.SpriteMetaData;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeColorHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class PathedParticle
{
    public final PathedParticleSharedRenderData sharedRenderData;

    public int maxAge = 20;

    public CPath.CPathData basePath, rgbPath = null, hsvPath = null, alphaPath = null, scale2DPath = null, scale3DPath = null, rotationPath = null, animationPath = null;
    public ArrayList<CPath.CPathData> morePaths = new ArrayList<>();

    public ArrayList<Predicate<PathedParticle>> deathConditions = new ArrayList<>();
    public ArrayList<PathedParticleFactory> onDeathParticles = null;

    public SpriteMetaData spriteMetaData = null;


    public Vec3d deathPos = null;
    public Object[] extraDeathArgs = null;
    public boolean useFoliageColor = false, useGrassColor = false;
    protected int age = 0;
    protected boolean dead = false;


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

        if (onDeathParticles != null)
        {
            for (PathedParticleFactory particleFactory : onDeathParticles) particleFactory.create(this, extraDeathArgs);
        }

        return this;
    }

    public boolean isDead()
    {
        return dead;
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
        int count = path.getRelativePosition(0).values.length;
        if (count != 1 && count != 3) throw new IllegalArgumentException("Rotation path must be 1D (rotation facing player) or 3D (manual rotation; yaw, pitch, roll)");

        rotationPath = new CPath.CPathData(path, 0);
        return this;
    }


    public PathedParticle animationPath(CPath path)
    {
        animationPath = new CPath.CPathData(path, 0);
        return this;
    }


    public PathedParticle dieOnSolids()
    {
        deathConditions.add(particle ->
        {
            World world = Minecraft.getMinecraft().world;
            if (world == null) return true;

            double[] fromVals = currentPos(0).values, toVals = nextPosition(0).values;
            Vec3d from = new Vec3d(fromVals[0], fromVals[1], fromVals[2]), to = new Vec3d(toVals[0], toVals[1], toVals[2]);
            RayTraceResult result = ImprovedRayTracing.rayTraceBlocks(world, from, to, true);
            if (result.typeOfHit == RayTraceResult.Type.MISS) return false;

            deathPos = result.hitVec;
            return true;
        });
        return this;
    }

    public PathedParticle dieOnSolidsAndLiquids()
    {
        deathConditions.add(particle ->
        {
            World world = Minecraft.getMinecraft().world;
            if (world == null) return true;

            double[] fromVals = currentPos(0).values, toVals = nextPosition(0).values;
            Vec3d from = new Vec3d(fromVals[0], fromVals[1], fromVals[2]), to = new Vec3d(toVals[0], toVals[1], toVals[2]);
            RayTraceResult result = ImprovedRayTracing.rayTraceBlocks(world, from, to, true, true);
            if (result.typeOfHit == RayTraceResult.Type.MISS) return false;

            deathPos = result.hitVec;
            return true;
        });
        return this;
    }

    public PathedParticle addDeathConditions(Predicate<PathedParticle>... conditions)
    {
        deathConditions.addAll(Arrays.asList(conditions));
        return this;
    }

    public PathedParticle addOnDeathParticles(PathedParticleFactory... particleFactories)
    {
        if (onDeathParticles == null) onDeathParticles = new ArrayList<>();
        onDeathParticles.addAll(Arrays.asList(particleFactories));
        return this;
    }


    public void update()
    {
        //Don't expire from age if max age is highest possible value, but still age up to that value - 1, and still do other death checks
        if (maxAge != Integer.MAX_VALUE || age < maxAge - 1) age++;

        for (Predicate<PathedParticle> predicate : deathConditions)
        {
            if (predicate.test(this))
            {
                if (deathPos == null)
                {
                    VectorN pos = currentPos(0);
                    deathPos = new Vec3d(pos.values[0], pos.values[1], pos.values[2]);
                }
                die();
                return;
            }
        }

        if (age >= maxAge)
        {
            VectorN pos = currentPos(0);
            deathPos = new Vec3d(pos.values[0], pos.values[1], pos.values[2]);
            die();
        }
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
        return pos;
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
                frame = spriteMetaData.frames.get(Tools.posMod((int) (spriteMetaData.frames.size() * animationPath.getRelativePosition(renderMillis).values[0]), spriteMetaData.frames.size()));
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


        double xScale3D = 0.1, yScale3D = 0.1, zScale3D = 0.1;
        if (scale3DPath != null)
        {
            VectorN scalar = scale3DPath.getRelativePosition(renderMillis);
            xScale3D *= scalar.values[0];
            yScale3D *= scalar.values[1];
            zScale3D *= scalar.values[2];
        }


        VectorN[] posOffsets;
        VectorN rotation = null;
        if (rotationPath != null) rotation = rotationPath.getRelativePosition(renderMillis);
        if (rotation == null || rotation.values.length == 1)
        {
            if (scale2DPath != null)
            {
                VectorN scalar = scale2DPath.getRelativePosition(renderMillis);
                xScaleXFactor *= scalar.values[0];
                xScaleZFactor *= scalar.values[0];
                yScaleYFactor *= scalar.values[1];
                yScaleXFactor *= scalar.values[1];
                yScaleZFactor *= scalar.values[1];
            }

            if (xOrigin == 0.5 && yOrigin == 0.5) posOffsets = new VectorN[]
                    {
                            new VectorN(-xScaleXFactor - yScaleZFactor, -yScaleYFactor, -xScaleZFactor - yScaleXFactor).scale(0.5),
                            new VectorN(-xScaleXFactor + yScaleZFactor, yScaleYFactor, -xScaleZFactor + yScaleXFactor).scale(0.5),
                            new VectorN(xScaleXFactor + yScaleZFactor, yScaleYFactor, xScaleZFactor + yScaleXFactor).scale(0.5),
                            new VectorN(xScaleXFactor - yScaleZFactor, -yScaleYFactor, xScaleZFactor - yScaleXFactor).scale(0.5)
                    };
            else posOffsets = new VectorN[]
                    {
                            new VectorN(xScaleXFactor * (xOrigin - 1) + yScaleZFactor * (yOrigin - 1), yScaleYFactor * (yOrigin - 1), xScaleZFactor * (xOrigin - 1) + yScaleXFactor * (yOrigin - 1)),
                            new VectorN(xScaleXFactor * (xOrigin - 1) + yScaleZFactor * yOrigin, yScaleYFactor * yOrigin, xScaleZFactor * (xOrigin - 1) + yScaleXFactor * yOrigin),
                            new VectorN(xScaleXFactor * xOrigin + yScaleZFactor * yOrigin, yScaleYFactor * yOrigin, xScaleZFactor * xOrigin + yScaleXFactor * yOrigin),
                            new VectorN(xScaleXFactor * xOrigin + yScaleZFactor * (yOrigin - 1), yScaleYFactor * (yOrigin - 1), xScaleZFactor * xOrigin + yScaleXFactor * (yOrigin - 1))
                    };


            //Manual 1D rotations //TODO Possibly slow, need to check
            if (rotation != null)
            {
                float theta = (float) (rotation.values[0] * 0.5);
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
        }
        else
        {
            posOffsets = new VectorN[]
                    {
                            new VectorN(xOrigin - 1, yOrigin - 1, 0),
                            new VectorN(xOrigin - 1, yOrigin, 0),
                            new VectorN(xOrigin, yOrigin, 0),
                            new VectorN(xOrigin, yOrigin - 1, 0)
                    };

            if (scale2DPath != null)
            {
                VectorN scalar = scale2DPath.getRelativePosition(renderMillis);
                for (VectorN v : posOffsets) v.multiply(scalar.values[0], scalar.values[1], 0);
            }

            //Manual 3D rotations //TODO SLOW
            for (VectorN v : posOffsets)
            {
                v.rotate(VectorN.Z_AXIS, rotation.values[2]);
                v.rotate(VectorN.X_AXIS, rotation.values[1]);
                v.rotate(VectorN.Y_AXIS, rotation.values[0]);
            }
        }


        World world = Minecraft.getMinecraft().world;
        BlockPos blockPos = new BlockPos(pos.values[0], pos.values[1], pos.values[2]);
        int lightmapIndex = world.isBlockLoaded(blockPos) ? world.getCombinedLight(blockPos, 0) : 0;
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

        if (useFoliageColor || useGrassColor) //TODO SLOW
        {
            if (useFoliageColor)
            {
                int c = BiomeColorHelper.getFoliageColorAtPos(world, blockPos);
                r *= ((c >> 16) & 255) / 255d;
                g *= ((c >> 8) & 255) / 255d;
                b *= (c & 255) / 255d;
            }
            if (useGrassColor)
            {
                int c = BiomeColorHelper.getGrassColorAtPos(world, blockPos);
                r *= ((c >> 16) & 255) / 255d;
                g *= ((c >> 8) & 255) / 255d;
                b *= (c & 255) / 255d;
            }
        }

        float a = alphaPath == null ? 1 : (float) alphaPath.getRelativePosition(renderMillis).values[0];


        buffer.pos(x + posOffsets[0].values[0] * xScale3D, y + posOffsets[0].values[1] * yScale3D, z + posOffsets[0].values[2] * zScale3D).tex(u2, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[1].values[0] * xScale3D, y + posOffsets[1].values[1] * yScale3D, z + posOffsets[1].values[2] * zScale3D).tex(u2, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[2].values[0] * xScale3D, y + posOffsets[2].values[1] * yScale3D, z + posOffsets[2].values[2] * zScale3D).tex(u1, v1).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
        buffer.pos(x + posOffsets[3].values[0] * xScale3D, y + posOffsets[3].values[1] * yScale3D, z + posOffsets[3].values[2] * zScale3D).tex(u1, v2).color(r, g, b, a).lightmap(skyLight, blockLight).endVertex();
    }
}
