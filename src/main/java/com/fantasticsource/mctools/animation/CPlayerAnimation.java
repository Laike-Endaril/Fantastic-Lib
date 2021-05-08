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


    public static void setHeadXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.xPath = path;
    }

    public static void setHeadYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.yPath = path;
    }

    public static void setHeadZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.zPath = path;
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

    public static void setHeadXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.xScalePath = path;
    }

    public static void setHeadYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.yScalePath = path;
    }

    public static void setHeadZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).head.zScalePath = path;
    }


    public static void setChestXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.xPath = path;
    }

    public static void setChestYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.yPath = path;
    }

    public static void setChestZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.zPath = path;
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

    public static void setChestXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.xScalePath = path;
    }

    public static void setChestYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.yScalePath = path;
    }

    public static void setChestZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).chest.zScalePath = path;
    }


    public static void setLeftArmXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.xPath = path;
    }

    public static void setLeftArmYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.yPath = path;
    }

    public static void setLeftArmZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.zPath = path;
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

    public static void setLeftArmXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.xScalePath = path;
    }

    public static void setLeftArmYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.yScalePath = path;
    }

    public static void setLeftArmZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftArm.zScalePath = path;
    }


    public static void setRightArmXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.xPath = path;
    }

    public static void setRightArmYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.yPath = path;
    }

    public static void setRightArmZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.zPath = path;
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

    public static void setRightArmXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.xScalePath = path;
    }

    public static void setRightArmYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.yScalePath = path;
    }

    public static void setRightArmZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightArm.zScalePath = path;
    }


    public static void setLeftLegXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.xPath = path;
    }

    public static void setLeftLegYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.yPath = path;
    }

    public static void setLeftLegZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.zPath = path;
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

    public static void setLeftLegXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.xScalePath = path;
    }

    public static void setLeftLegYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.yScalePath = path;
    }

    public static void setLeftLegZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).leftLeg.zScalePath = path;
    }


    public static void setRightLegXPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.xPath = path;
    }

    public static void setRightLegYPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.yPath = path;
    }

    public static void setRightLegZPath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.zPath = path;
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

    public static void setRightLegXScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.xScalePath = path;
    }

    public static void setRightLegYScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.yScalePath = path;
    }

    public static void setRightLegZScalePath(Entity entity, CPath path)
    {
        ANIMATION_DATA.computeIfAbsent(entity, o -> new CPlayerAnimation()).rightLeg.zScalePath = path;
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
