package com.fantasticsource.mctools.cliententity;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Smoothing;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.GetCollisionBoxesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.Field;

@SideOnly(Side.CLIENT)
public class Camera extends ClientEntity
{
    protected static final double OFFSET_COLLISION_BUFFER_DIRECT = 0.2, OFFSET_COLLISION_BUFFER_FORWARD = 0.2;
    protected static final Field MINECRAFT_RENDER_VIEW_ENTITY_FIELD = ReflectionTool.getField(Minecraft.class, "field_175622_Z", "renderViewEntity");

    static
    {
        MinecraftForge.EVENT_BUS.register(Camera.class);
    }

    protected static Camera camera = null;

    public static Camera getCamera()
    {
        if (camera == null) camera = new Camera(null);
        return camera;
    }


    public static final int
            PLAYER_RENDER_IF_THIRD_PERSON = 0,
            PLAYER_RENDER_ALWAYS = 1,
            PLAYER_RENDER_NEVER = 2;

    public static int playerRenderMode = PLAYER_RENDER_IF_THIRD_PERSON;
    public static boolean allowControl = true;
    public static double followOffsetLR = 0;


    protected boolean active = false;
    protected int mode, originalMode; //0 is first person, 1 is third person, 2 is third person flipped (in front), -1 allows client control via the view mode keybind
    protected Entity toFollow = null;


    protected Camera(World worldIn)
    {
        super(worldIn);
        setSize(0, 0);
        forceSpawn = true;
    }

    public boolean isActive()
    {
        return active;
    }

    public void activate(Entity toFollow, int mode)
    {
        activate(toFollow, toFollow.world, toFollow.posX, toFollow.posY + toFollow.getEyeHeight(), toFollow.posZ, toFollow.getRotationYawHead(), toFollow.rotationPitch, mode);
    }

    public void activate(World world, double x, double y, double z, float yaw, float pitch, int mode)
    {
        activate(null, world, x, y, z, yaw, pitch, mode);
    }

    protected void activate(Entity toFollow, World world, double x, double y, double z, float yaw, float pitch, int mode)
    {
        if (active) deactivate();


        //Set state
        active = true;
        this.toFollow = toFollow;


        //Entity
        this.world = world;
        dimension = world.provider.getDimension();
        posX = x;
        prevPosX = x;
        posY = y;
        prevPosY = y;
        posZ = z;
        prevPosZ = z;
        rotationYaw = yaw;
        prevRotationYaw = yaw;
        rotationPitch = pitch;
        prevRotationPitch = pitch;
        isDead = false;
        world.spawnEntity(this);


        //Mode
        this.mode = mode;
        if (mode == -1) originalMode = -1;
        else
        {
            GameSettings gs = Minecraft.getMinecraft().gameSettings;
            originalMode = gs.thirdPersonView;
            gs.thirdPersonView = mode;
        }


        //Set camera
        Minecraft.getMinecraft().setRenderViewEntity(camera);
    }

    public void deactivate()
    {
        if (active)
        {
            //Set state
            active = false;


            //Entity
            world.removeEntity(this);
            world = null;


            //Mode
            Minecraft mc = Minecraft.getMinecraft();
            if (mode != -1)
            {
                mc.gameSettings.thirdPersonView = originalMode;
            }


            //Set camera
            mc.setRenderViewEntity(mc.player);
        }
    }

    @Override
    public void onUpdate()
    {
        //Mode
        if (active && mode != -1) Minecraft.getMinecraft().gameSettings.thirdPersonView = mode;

        super.onUpdate();
    }

    @Override
    public void onEntityUpdate()
    {
    }

    @SubscribeEvent
    public static void trackFollowed(TickEvent.RenderTickEvent event)
    {
        if (!getCamera().active || event.phase != TickEvent.Phase.START) return;

        if (camera.toFollow != null) followEntity(event.renderTickTime);
    }

    protected static void followEntity(float partialTick)
    {
        Entity entity = camera.toFollow;


        if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 0)
        {
            camera.rotationYaw = entity.getRotationYawHead();
            camera.rotationPitch = entity.rotationPitch;
            camera.prevRotationYaw = entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).prevRotationYawHead : entity.prevRotationYaw;
            camera.prevRotationPitch = entity.prevRotationPitch;
        }
        else
        {
            camera.rotationYaw = (float) Smoothing.interpolate(entity instanceof EntityLivingBase ? ((EntityLivingBase) entity).prevRotationYawHead : entity.prevRotationYaw, entity.getRotationYawHead(), partialTick, Smoothing.LINEAR);
            camera.rotationPitch = (float) Smoothing.interpolate(entity.prevRotationPitch, entity.rotationPitch, partialTick, Smoothing.LINEAR);
            camera.prevRotationYaw = camera.rotationYaw;
            camera.prevRotationPitch = camera.rotationPitch;
        }


        if (followOffsetLR != 0)
        {
            World world = entity.world;
            double testFollowOffsetLR = followOffsetLR > 0 ? followOffsetLR + OFFSET_COLLISION_BUFFER_DIRECT : followOffsetLR - OFFSET_COLLISION_BUFFER_DIRECT;
            Vec3d start = entity.getPositionEyes(1);
            Vec3d testStart = start.addVector(-OFFSET_COLLISION_BUFFER_FORWARD * TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad(camera.rotationYaw)), 0, OFFSET_COLLISION_BUFFER_FORWARD * TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad(camera.rotationYaw)));
            Vec3d testEnd = testStart.subtract(testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad(camera.rotationYaw)), 0, testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad(camera.rotationYaw)));
            RayTraceResult testResult = ImprovedRayTracing.rayTraceBlocks(world, testStart, testEnd, Math.abs(testFollowOffsetLR), true);
            Vec3d testHitVec = testResult.hitVec != null ? testResult.hitVec : testEnd;
            Vec3d testDif = testHitVec.subtract(testStart);
            double testDist = testDif.lengthVector() - OFFSET_COLLISION_BUFFER_DIRECT;

            if (testDist > 0)
            {
                Vec3d end = start.subtract(testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.cos(Tools.degtorad(camera.rotationYaw)), 0, testFollowOffsetLR * TrigLookupTable.TRIG_TABLE_1024.sin(Tools.degtorad(camera.rotationYaw)));
                RayTraceResult result = ImprovedRayTracing.rayTraceBlocks(world, start, end, testDist + OFFSET_COLLISION_BUFFER_DIRECT, true);
                Vec3d hitVec = result.hitVec != null ? result.hitVec : end;
                Vec3d dif = hitVec.subtract(start);
                double dist = dif.lengthVector() - OFFSET_COLLISION_BUFFER_DIRECT;

                if (dist > 0)
                {
                    camera.setPosition(dif.normalize().scale(Tools.min(testDist, dist)).add(start));
                }
            }
        }

        camera.prevPosX = camera.posX;
        camera.prevPosY = camera.posY;
        camera.prevPosZ = camera.posZ;
    }


    public void setPositionAndRotation(Vec3d position, float yaw, float pitch)
    {
        setPosition(position);
        setRotation(yaw, pitch);
    }

    @Override
    public void setPositionAndRotation(double x, double y, double z, float yaw, float pitch)
    {
        setPosition(x, y, z);
        setRotation(yaw, pitch);
    }

    public void setPosition(Vec3d vec)
    {
        setPosition(vec.x, vec.y, vec.z);
    }

    @Override
    public float getEyeHeight()
    {
        return 0;
    }


    @SubscribeEvent
    public static void renderPlayerPre(RenderPlayerEvent.Pre event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        switch (playerRenderMode)
        {
            case PLAYER_RENDER_NEVER:
                return;

            case PLAYER_RENDER_ALWAYS:
                break;

            case PLAYER_RENDER_IF_THIRD_PERSON:
            default:
                if (mc.gameSettings.thirdPersonView == 0) return;
        }

        if (getCamera().active && event.getEntityPlayer() == mc.player)
        {
            mc.getRenderManager().renderViewEntity = mc.player;
        }
    }

    @SubscribeEvent
    public static void renderPlayerPost(RenderPlayerEvent.Post event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (getCamera().active && event.getEntityPlayer() == mc.player)
        {
            mc.getRenderManager().renderViewEntity = camera;
        }
    }

    @SubscribeEvent
    public static void preOverlayRender(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (getCamera().active)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.setRenderViewEntity(mc.player);
        }
    }

    @SubscribeEvent
    public static void postOverlayRender(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (getCamera().active)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.setRenderViewEntity(camera);
        }
    }


    protected static boolean control1 = false, control2 = false;

    @SubscribeEvent
    public static void controlFixPre1(PlayerSPPushOutOfBlocksEvent event)
    {
        if (!allowControl) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (getCamera().active && event.getEntityPlayer() == mc.player)
        {
            ReflectionTool.set(MINECRAFT_RENDER_VIEW_ENTITY_FIELD, mc, mc.player);
            control1 = true;
        }
    }

    @SubscribeEvent
    public static void controlFixPre2(TickEvent.PlayerTickEvent event)
    {
        if (!allowControl) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (event.phase == TickEvent.Phase.END && getCamera().active && event.player == mc.player)
        {
            ReflectionTool.set(MINECRAFT_RENDER_VIEW_ENTITY_FIELD, mc, mc.player);
            control2 = true;
        }
    }

    @SubscribeEvent
    public static void controlFixPost(GetCollisionBoxesEvent event)
    {
        Minecraft mc = Minecraft.getMinecraft();
        if ((control1 || control2) && getCamera().active && event.getWorld().isRemote)
        {
            control1 = false;
            control2 = false;
            ReflectionTool.set(MINECRAFT_RENDER_VIEW_ENTITY_FIELD, mc, camera);
        }
    }
}
