package com.fantasticsource.mctools.animation;

import com.fantasticsource.tools.component.Component;
import com.fantasticsource.tools.component.path.CPath;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;

public class CPlayerAnimation extends Component
{
    public static final HashMap<Entity, CPlayerAnimation> ANIMATION_DATA = new HashMap<>();


    public CModelRendererAnimation head, chest, leftArm, rightArm, leftLeg, rightLeg;


    public CPlayerAnimation()
    {
        this(new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation(), new CModelRendererAnimation());
    }

    public CPlayerAnimation(CModelRendererAnimation head, CModelRendererAnimation chest, CModelRendererAnimation leftArm, CModelRendererAnimation rightArm, CModelRendererAnimation leftLeg, CModelRendererAnimation rightLeg)
    {
        this.head = head;
        this.chest = chest;
        this.leftArm = leftArm;
        this.rightArm = rightArm;
        this.leftLeg = leftLeg;
        this.rightLeg = rightLeg;
    }


    public static void setHeadXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.xRotPath = path;
    }

    public static void setHeadYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.yRotPath = path;
    }

    public static void setHeadZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.zRotPath = path;
    }


    public static void setChestXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.xRotPath = path;
    }

    public static void setChestYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.yRotPath = path;
    }

    public static void setChestZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.zRotPath = path;
    }


    public static void setLeftArmXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.xRotPath = path;
    }

    public static void setLeftArmYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.yRotPath = path;
    }

    public static void setLeftArmZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.zRotPath = path;
    }


    public static void setRightArmXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.xRotPath = path;
    }

    public static void setRightArmYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.yRotPath = path;
    }

    public static void setRightArmZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.zRotPath = path;
    }


    public static void setLeftLegXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.xRotPath = path;
    }

    public static void setLeftLegYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.yRotPath = path;
    }

    public static void setLeftLegZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.zRotPath = path;
    }


    public static void setRightLegXRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.xRotPath = path;
    }

    public static void setRightLegYRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.yRotPath = path;
    }

    public static void setRightLegZRotPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.zRotPath = path;
    }


    @Override
    public CPlayerAnimation write(ByteBuf buf)
    {
        head.write(buf);
        chest.write(buf);
        leftArm.write(buf);
        rightArm.write(buf);
        leftLeg.write(buf);
        rightLeg.write(buf);

        return this;
    }

    @Override
    public CPlayerAnimation read(ByteBuf buf)
    {
        head.read(buf);
        chest.read(buf);
        leftArm.read(buf);
        rightArm.read(buf);
        leftLeg.read(buf);
        rightLeg.read(buf);

        return this;
    }

    @Override
    public CPlayerAnimation save(OutputStream stream)
    {
        head.save(stream);
        chest.save(stream);
        leftArm.save(stream);
        rightArm.save(stream);
        leftLeg.save(stream);
        rightLeg.save(stream);

        return this;
    }

    @Override
    public CPlayerAnimation load(InputStream stream)
    {
        head.load(stream);
        chest.load(stream);
        leftArm.load(stream);
        rightArm.load(stream);
        leftLeg.load(stream);
        rightLeg.load(stream);

        return this;
    }
}
