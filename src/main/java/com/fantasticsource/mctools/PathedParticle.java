package com.fantasticsource.mctools;

import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PathedParticle extends Particle
{
    protected VectorN origin;
    protected ArrayList<CPath.PathData> appliedPaths = new ArrayList<>();

    public PathedParticle(World worldIn, double posXIn, double posYIn, double posZIn, CPath... pathsToApply)
    {
        super(worldIn, posXIn, posYIn, posZIn);

        setParticleTextureIndex(160);
        particleMaxAge = 60;
        origin = new VectorN(posXIn, posYIn, posZIn);

        Minecraft.getMinecraft().effectRenderer.addEffect(this);

        for (CPath path : pathsToApply) applyPath(path);
    }


    public void applyPath(CPath path)
    {
        appliedPaths.add(new CPath.PathData(path));
    }


    @Override
    public void onUpdate()
    {
        if (particleAge++ >= particleMaxAge) setExpired();

        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        VectorN pos = origin.copy(), pathPos;
        for (CPath.PathData data : appliedPaths)
        {
            pathPos = data.getRelativePosition();
            if (pathPos == null)
            {
                setExpired();
                return;
            }

            pos.add(pathPos);
        }

        setPosition(pos.values[0], pos.values[1], pos.values[2]);
    }
}
