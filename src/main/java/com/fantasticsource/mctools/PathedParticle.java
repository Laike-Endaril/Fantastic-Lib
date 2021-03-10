package com.fantasticsource.mctools;

import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PathedParticle extends Particle
{
    protected boolean readyToRender = false;
    public boolean useBlockLight = false, isBottomRight78ths = true;
    public double xScale3D = 1, yScale3D = 1, zScale3D = 1;

    protected CPath.PathData basePath;
    protected ArrayList<CPath.PathData> morePaths = new ArrayList<>();

    public PathedParticle(World worldIn, CPath basePath, CPath... morePaths)
    {
        super(worldIn, Double.MAX_VALUE, Double.MAX_VALUE, Double.MAX_VALUE);

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

        Minecraft.getMinecraft().effectRenderer.addEffect(this);
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

        readyToRender = true;
    }

    public VectorN currentPos()
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


        double u1, u2, v1, v2;
        if (particleTexture == null)
        {
            u1 = (double) particleTextureIndexX / 16;
            u2 = u1 + 0.0624375;
            v1 = (double) particleTextureIndexY / 16;
            v2 = v1 + 0.0624375;
        }
        else
        {
            u1 = particleTexture.getMinU();
            u2 = particleTexture.getMaxU();
            v1 = particleTexture.getMinV();
            v2 = particleTexture.getMaxV();
        }
        if (isBottomRight78ths)
        {
            u1 += 0.0078046875;
            v1 += 0.0078046875;
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
