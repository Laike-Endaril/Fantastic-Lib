package com.fantasticsource.mctools;

import com.fantasticsource.tools.component.path.CPath;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

import java.util.ArrayList;

public class PathedParticle extends Particle
{
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

        Minecraft.getMinecraft().effectRenderer.addEffect(this);
    }


    public void applyPath(CPath path)
    {
        morePaths.add(new CPath.PathData(path));
    }


    @Override
    public void onUpdate()
    {
        if (particleAge++ >= particleMaxAge) setExpired();

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
}
