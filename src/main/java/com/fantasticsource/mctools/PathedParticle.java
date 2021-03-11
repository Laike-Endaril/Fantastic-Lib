package com.fantasticsource.mctools;

import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.Color;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PathedParticle extends Particle
{
    protected boolean readyToRender = false;
    public double u1 = 0, u2 = 1, v1 = 0, v2 = 1;
    public boolean useBlockLight = false;
    public double xScale3D = 1, yScale3D = 1, zScale3D = 1;

    protected CPath.PathData basePath, rgbPath = null, hsvPath = null, alphaPath = null;
    protected ArrayList<CPath.PathData> morePaths = new ArrayList<>();

    public PathedParticle(World world, CPath basePath, CPath... morePaths)
    {
        this(world, GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, basePath, morePaths);
    }

    public PathedParticle(World world, GlStateManager.SourceFactor sourceBlend, GlStateManager.DestFactor destBlend, CPath basePath, CPath... morePaths)
    {
        super(world, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

        this.basePath = new CPath.PathData(basePath);
        for (CPath path : morePaths) applyPath(path);
        VectorN pos = currentPos();
        setPosition(pos.values[0], pos.values[1], pos.values[2]);
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        setParticleTextureIndex(160);
        particleMaxAge = 60;
        particleScale = 1;
        canCollide = false;

        PathedParticleManager.add(this, sourceBlend, destBlend);
    }


    public PathedParticle applyPath(CPath path)
    {
        morePaths.add(new CPath.PathData(path));
        return this;
    }

    public PathedParticle useBlockLight(boolean useBlockLight)
    {
        this.useBlockLight = useBlockLight;
        return this;
    }

    public PathedParticle rgbPath(CPath path)
    {
        rgbPath = new CPath.PathData(path);
        return this;
    }

    public PathedParticle hsvPath(CPath path)
    {
        hsvPath = new CPath.PathData(path);
        return this;
    }

    public PathedParticle alphaPath(CPath path)
    {
        alphaPath = new CPath.PathData(path);
        return this;
    }


    @Override
    public void onUpdate()
    {
        if (particleAge++ > particleMaxAge) setExpired();

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        VectorN pos = currentPos();
        if (pos == null)
        {
            setExpired();
            return;
        }

        setPosition(pos.values[0], pos.values[1], pos.values[2]);

        if (rgbPath != null)
        {
            VectorN rgb = rgbPath.getRelativePosition();
            setRBGColorF((float) rgb.values[0], (float) rgb.values[1], (float) rgb.values[2]);
        }
        else if (hsvPath != null)
        {
            VectorN hsv = hsvPath.getRelativePosition();
            Color c = new Color(0).setColorHSV((float) hsv.values[0], (float) hsv.values[1], (float) hsv.values[2]);
            setRBGColorF(c.rf(), c.gf(), c.bf());
        }

        if (alphaPath != null) setAlphaF((float) alphaPath.getRelativePosition().values[0]);

        readyToRender = true;
    }

    protected VectorN currentPos()
    {
        VectorN pos = basePath.getRelativePosition(), pathPos;
        for (CPath.PathData data : morePaths)
        {
            pathPos = data.getRelativePosition();
            if (pathPos == null) return null;

            pos.add(pathPos);
        }
        return pos;
    }

    @Override
    public int getBrightnessForRender(float p_189214_1_)
    {
        if (useBlockLight) return super.getBrightnessForRender(p_189214_1_);
        return 15728880;
    }

    @Override
    public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
    {
        if (!readyToRender) return;

        double x = prevPosX + (posX - prevPosX) * partialTicks - interpPosX;
        double y = prevPosY + (posY - prevPosY) * partialTicks - interpPosY;
        double z = prevPosZ + (posZ - prevPosZ) * partialTicks - interpPosZ;
        double scale = particleScale / 2;
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


        buffer.pos(x + vecs[0].x, y + vecs[0].y, z + vecs[0].z).tex(u2, v2).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + vecs[1].x, y + vecs[1].y, z + vecs[1].z).tex(u2, v1).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + vecs[2].x, y + vecs[2].y, z + vecs[2].z).tex(u1, v1).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
        buffer.pos(x + vecs[3].x, y + vecs[3].y, z + vecs[3].z).tex(u1, v2).color(particleRed, particleGreen, particleBlue, particleAlpha).lightmap(lightmapX, lightmapY).endVertex();
    }
}
