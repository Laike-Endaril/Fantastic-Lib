package com.fantasticsource.mctools.cliententity;

import com.fantasticsource.mctools.ImprovedRayTracing;
import com.fantasticsource.tools.Smoothing;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.VectorN;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Camera extends ClientEntity
{
    protected static final double CAMERA_PADDING = 0.25;
    protected static final double OFFSET_COLLISION_BUFFER_DIRECT = 0.2, OFFSET_COLLISION_BUFFER_FORWARD = 0.2;

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
            PLAYER_RENDER_NEVER = 2,
            CONTROL_PLAYER = 0,
            CONTROL_CAMERA_CREATIVE = 1;

    public static int playerRenderMode = PLAYER_RENDER_IF_THIRD_PERSON, controlMode = CONTROL_PLAYER;
    public static boolean showHotbar = true, renderFirstPersonHands = true;
    public static double followOffsetLR = 0;
    public Entity toFollow = null, originalViewEntity = Minecraft.getMinecraft().getRenderViewEntity();


    protected boolean active = false;
    protected int mode, originalMode; //0 is first person, 1 is third person, 2 is third person flipped (in front), -1 allows client control via the view mode keybind


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
        activate(toFollow, toFollow.world, toFollow.posX, toFollow.posY, toFollow.posZ, toFollow.getRotationYawHead(), toFollow.rotationPitch, mode);
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


        //View entity
        originalViewEntity = Minecraft.getMinecraft().getRenderViewEntity();
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


            //View entity
            mc.setRenderViewEntity(originalViewEntity);
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
        camera.posY += camera.getEyeHeight();

        GameSettings gs = Minecraft.getMinecraft().gameSettings;
        switch (controlMode)
        {
            case CONTROL_CAMERA_CREATIVE:
            {
                camera.prevRotationYaw = camera.rotationYaw;
                camera.prevRotationPitch = camera.rotationPitch;
                camera.setRotationYawHead(camera.originalViewEntity.getRotationYawHead());
                camera.rotationYaw = camera.originalViewEntity.rotationYaw;
                camera.rotationPitch = camera.originalViewEntity.rotationPitch;

                VectorN motionVec = new VectorN(0, 0, 0);
                if (gs.keyBindForward.isKeyDown()) motionVec.values[2] += 1;
                if (gs.keyBindBack.isKeyDown()) motionVec.values[2] -= 1;
                if (gs.keyBindRight.isKeyDown()) motionVec.values[0] += 1;
                if (gs.keyBindLeft.isKeyDown()) motionVec.values[0] -= 1;
                if (gs.keyBindJump.isKeyDown()) motionVec.values[1] += 1;
                if (gs.keyBindSneak.isKeyDown()) motionVec.values[1] -= 1;

                motionVec.rotate(new VectorN(0, 1, 0), Tools.degtorad(camera.rotationYaw)).setMagnitude(gs.keyBindSprint.isKeyDown() ? 1.5 : 0.5);
                motionVec.values[0] = -motionVec.values[0];

                camera.prevPosX = camera.posX;
                camera.prevPosY = camera.posY;
                camera.prevPosZ = camera.posZ;

                RayTraceResult result = ImprovedRayTracing.rayTraceBlocks(camera.world, camera.getPositionVector(), camera.getPositionVector().addVector(motionVec.values[0], motionVec.values[1], motionVec.values[2]), true);
                double change;
                boolean x = false, y = false, z = false;
                while (result.typeOfHit == RayTraceResult.Type.BLOCK)
                {
                    if (result.hitVec.equals(camera.getPositionVector())) break;

                    switch (result.sideHit)
                    {
                        case WEST:
                            x = true;
                            if (Math.abs(motionVec.values[0]) < CAMERA_PADDING) motionVec.values[0] = 0;
                            else
                            {
                                change = result.hitVec.x - camera.posX - CAMERA_PADDING;
                                motionVec.scale(1 - change / motionVec.values[0]);
                                motionVec.values[0] = 0;
                                camera.posX += change;
                            }
                            break;

                        case EAST:
                            x = true;
                            if (Math.abs(motionVec.values[0]) < CAMERA_PADDING) motionVec.values[0] = 0;
                            else
                            {
                                change = result.hitVec.x - camera.posX + CAMERA_PADDING;
                                motionVec.scale(1 - change / motionVec.values[0]);
                                motionVec.values[0] = 0;
                                camera.posX += change;
                            }
                            break;

                        case DOWN:
                            y = true;
                            if (Math.abs(motionVec.values[1]) < CAMERA_PADDING) motionVec.values[1] = 0;
                            else
                            {
                                change = result.hitVec.y - camera.posY - CAMERA_PADDING;
                                motionVec.scale(1 - change / motionVec.values[1]);
                                motionVec.values[1] = 0;
                                camera.posY += change;
                            }
                            break;

                        case UP:
                            y = true;
                            if (Math.abs(motionVec.values[1]) < CAMERA_PADDING) motionVec.values[1] = 0;
                            else
                            {
                                change = result.hitVec.y - camera.posY + CAMERA_PADDING;
                                motionVec.scale(1 - change / motionVec.values[1]);
                                motionVec.values[1] = 0;
                                camera.posY += change;
                            }
                            break;

                        case NORTH:
                            z = true;
                            if (Math.abs(motionVec.values[2]) < CAMERA_PADDING) motionVec.values[2] = 0;
                            else
                            {
                                change = result.hitVec.z - camera.posZ - CAMERA_PADDING;
                                motionVec.scale(1 - change / motionVec.values[2]);
                                motionVec.values[2] = 0;
                                camera.posZ += change;
                            }
                            break;

                        case SOUTH:
                            z = true;
                            if (Math.abs(motionVec.values[2]) < CAMERA_PADDING) motionVec.values[2] = 0;
                            else
                            {
                                change = result.hitVec.z - camera.posZ + CAMERA_PADDING;
                                motionVec.scale(1 - change / motionVec.values[2]);
                                motionVec.values[2] = 0;
                                camera.posZ += change;
                            }
                            break;
                    }

                    result = ImprovedRayTracing.rayTraceBlocks(camera.world, camera.getPositionVector(), camera.getPositionVector().addVector(motionVec.values[0], motionVec.values[1], motionVec.values[2]), true);
                }

                camera.posX = result.hitVec.x;
                camera.posY = result.hitVec.y;
                camera.posZ = result.hitVec.z;

                if (!x)
                {
                    result = ImprovedRayTracing.rayTraceBlocks(camera.world, camera.getPositionVector(), camera.getPositionVector().addVector(motionVec.values[0], 0, 0), CAMERA_PADDING, true);
                    if (result.typeOfHit == RayTraceResult.Type.BLOCK)
                    {
                        if (motionVec.values[0] > 0) camera.posX = result.hitVec.x - CAMERA_PADDING;
                        else camera.posX = result.hitVec.x + CAMERA_PADDING;
                    }
                }

                if (!y)
                {
                    result = ImprovedRayTracing.rayTraceBlocks(camera.world, camera.getPositionVector(), camera.getPositionVector().addVector(0, motionVec.values[1], 0), CAMERA_PADDING, true);
                    if (result.typeOfHit == RayTraceResult.Type.BLOCK)
                    {
                        if (motionVec.values[1] > 0) camera.posY = result.hitVec.y - CAMERA_PADDING;
                        else camera.posY = result.hitVec.y + CAMERA_PADDING;
                    }
                }

                if (!z)
                {
                    result = ImprovedRayTracing.rayTraceBlocks(camera.world, camera.getPositionVector(), camera.getPositionVector().addVector(0, 0, motionVec.values[2]), CAMERA_PADDING, true);
                    if (result.typeOfHit == RayTraceResult.Type.BLOCK)
                    {
                        if (motionVec.values[2] > 0) camera.posZ = result.hitVec.z - CAMERA_PADDING;
                        else camera.posZ = result.hitVec.z + CAMERA_PADDING;
                    }
                }
            }
            break;
        }

        camera.posY -= camera.getEyeHeight();
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
                    camera.setPosition(dif.normalize().scale(Tools.min(testDist, dist)).add(start).subtract(0, entity.getEyeHeight(), 0));
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
        return originalViewEntity.getEyeHeight();
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

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void preOverlayRender(RenderGameOverlayEvent.Pre event)
    {
        if (showHotbar == false || event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) return;

        if (getCamera().active)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.setRenderViewEntity(camera.originalViewEntity);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void postOverlayRender(RenderGameOverlayEvent.Post event)
    {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        if (getCamera().active)
        {
            Minecraft mc = Minecraft.getMinecraft();
            mc.setRenderViewEntity(camera);
        }
    }


    @SubscribeEvent
    public static void controlFix(TickEvent.RenderTickEvent event)
    {
        if (controlMode != CONTROL_PLAYER || !camera.active) return;

        if (event.phase == TickEvent.Phase.START)
        {
            Minecraft.getMinecraft().setRenderViewEntity(camera);
        }
        else
        {
            Minecraft.getMinecraft().setRenderViewEntity(camera.originalViewEntity);
        }
    }


    @SubscribeEvent
    public static void renderHand(RenderHandEvent event)
    {
        if (Camera.getCamera().active && !renderFirstPersonHands) event.setCanceled(true);
    }
}
