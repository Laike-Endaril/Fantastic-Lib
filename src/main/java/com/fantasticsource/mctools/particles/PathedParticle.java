package com.fantasticsource.mctools.particles;

import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

public class PathedParticle extends Particle
{
    public static long renderMillis;

    public final PathedParticleSharedRenderData sharedRenderData;

    public double u1 = 32d / 128, v1 = 16d / 128, u2 = 64d / 128, v2 = 48d / 128;

    protected CPath.CPathData basePath, rgbPath = null, hsvPath = null, alphaPath = null, scale3DPath = null;
    protected ArrayList<CPath.CPathData> morePaths = new ArrayList<>();


    public PathedParticle(PathedParticleSharedRenderData sharedRenderData, CPath basePath, CPath... morePaths)
    {
        super(Minecraft.getMinecraft().world, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

        this.sharedRenderData = sharedRenderData;

        this.basePath = new CPath.CPathData(basePath, 0);
        for (CPath path : morePaths) applyPath(path);

        particleMaxAge = 20;
        particleScale = 1;
        canCollide = false;

        PathedParticleManager.add(this);
    }


    @Override
    public int getFXLayer()
    {
        return -1;
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


    @Override
    public void onUpdate()
    {
        particleAge++;
    }

    protected VectorN currentPos()
    {
        VectorN pos = basePath.getRelativePosition(renderMillis), pathPos;
        if (pos == null) return null;

        for (CPath.CPathData data : morePaths)
        {
            pathPos = data.getRelativePosition(renderMillis);
            if (pathPos == null) return null;

            pos.add(pathPos);
        }
        return pos;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        //Normalize all path progress over the course of the particle lifetime
        renderMillis = (long) ((particleAge * 50 + partialTicks * 50) * 20 / particleMaxAge);
        if (renderMillis > 1000)
        {
            setExpired();
            return;
        }


        VectorN pos = currentPos();
        if (pos == null)
        {
            setExpired();
            return;
        }
        setPosition(pos.values[0], pos.values[1], pos.values[2]);


        double x = posX - interpPosX;
        double y = posY - interpPosY;
        double z = posZ - interpPosZ;
        double scale = particleScale / 2;

        double xScale3D = 1, yScale3D = 1, zScale3D = 1;
        if (scale3DPath != null)
        {
            VectorN scalar = scale3DPath.getRelativePosition(renderMillis);
            xScale3D = scalar.values[0];
            yScale3D = scalar.values[1];
            zScale3D = scalar.values[2];
        }

        Vec3d[] vecs = new Vec3d[]
                {
                        new Vec3d((-rotationX - rotationXY) * xScale3D, -rotationZ * yScale3D, (-rotationYZ - rotationXZ) * zScale3D).scale(scale),
                        new Vec3d((-rotationX + rotationXY) * xScale3D, rotationZ * yScale3D, (-rotationYZ + rotationXZ) * zScale3D).scale(scale),
                        new Vec3d((rotationX + rotationXY) * xScale3D, rotationZ * yScale3D, (rotationYZ + rotationXZ) * zScale3D).scale(scale),
                        new Vec3d((rotationX - rotationXY) * xScale3D, -rotationZ * yScale3D, (rotationYZ - rotationXZ) * zScale3D).scale(scale)
                };

        if (particleAngle != 0)
        {
            float theta = (particleAngle + (particleAngle - prevParticleAngle) * partialTicks) * 0.5f;
            float cosTheta = MathHelper.cos(theta);
            double xx = MathHelper.sin(theta) * cameraViewDir.x;
            double yy = MathHelper.sin(theta) * cameraViewDir.y;
            double zz = MathHelper.sin(theta) * cameraViewDir.z;
            Vec3d vec3d = new Vec3d(xx, yy, zz);

            for (int l = 0; l < 4; ++l)
            {
                vecs[l] = vec3d.scale(2 * vecs[l].dotProduct(vec3d)).add(vecs[l].scale(cosTheta * cosTheta - vec3d.dotProduct(vec3d))).add(vec3d.crossProduct(vecs[l]).scale(2 * cosTheta));
            }
        }


        int lightmapIndex = getBrightnessForRender(partialTicks);
        int lightmapX = lightmapIndex >> 16 & 65535;
        int lightmapY = lightmapIndex & 65535;


        if (rgbPath != null)
        {
            VectorN rgb = rgbPath.getRelativePosition(renderMillis);
            setRBGColorF((float) rgb.values[0], (float) rgb.values[1], (float) rgb.values[2]);
        }
        else if (hsvPath != null)
        {
            VectorN hsv = hsvPath.getRelativePosition(renderMillis);
            Color c = new Color(0).setColorHSV((float) hsv.values[0], (float) hsv.values[1], (float) hsv.values[2]);
            setRBGColorF(c.rf(), c.gf(), c.bf());
        }

        if (alphaPath != null) setAlphaF((float) alphaPath.getRelativePosition(renderMillis).values[0]);

        buffer.pos(x + vecs[0].x, y + vecs[0].y, z + vecs[0].z).tex(u2, v2).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + vecs[1].x, y + vecs[1].y, z + vecs[1].z).tex(u2, v1).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + vecs[2].x, y + vecs[2].y, z + vecs[2].z).tex(u1, v1).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + vecs[3].x, y + vecs[3].y, z + vecs[3].z).tex(u1, v2).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
    }
}
