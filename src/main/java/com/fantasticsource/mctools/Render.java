package com.fantasticsource.mctools;

import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import com.fantasticsource.tools.datastructures.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static com.fantasticsource.mctools.MCTools.crash;
import static com.fantasticsource.tools.Tools.radtodeg;

@SideOnly(Side.CLIENT)
public class Render
{
    public static final byte
            SCALING_FULL = 0,
            SCALING_MC_GUI = 1;


    private static Field minecraftRenderPartialTicksPausedField;

    private static float fov, fovMultiplier;


    public static void init()
    {
        try
        {
            minecraftRenderPartialTicksPausedField = ReflectionTool.getField(Minecraft.class, "field_193996_ah", "renderPartialTicksPaused");

            MinecraftForge.EVENT_BUS.register(Render.class);
        }
        catch (Exception e)
        {
            crash(e, 701, false);
        }
    }


    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public static void drawHUD(RenderGameOverlayEvent.Pre event)
    {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR)
        {
            GlStateManager.pushMatrix();
            MinecraftForge.EVENT_BUS.post(new RenderHUDEvent(event));
            GlStateManager.popMatrix();
        }
        GlStateManager.color(1, 1, 1, 1);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateFOV(EntityViewRenderEvent.FOVModifier event)
    {
        fov = event.getFOV();
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void updateFOVMultiplier(FOVUpdateEvent event)
    {
        fovMultiplier = event.getNewfov();
    }


    public static int getPartialStringWidth(String beforePart, String part)
    {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        return fr.getStringWidth(beforePart + part) - fr.getStringWidth(beforePart);
    }


    public static void startOrtho()
    {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0, Display.getWidth(), Display.getHeight(), 0, -1, 1);
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
    }

    public static void endOrtho()
    {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
    }


    public static float getVFOV()
    {
        //This should already be accounting for partialticks behind the scenes
        return fov * fovMultiplier;
    }

    public static double getHFOV(TrigLookupTable trigLookupTable) throws IllegalAccessException
    {
        return radtodeg(trigLookupTable.arctan(getZNearWidth() * 0.5 / getZNearDist())) * 2;
    }


    public static double getPartialTick() throws IllegalAccessException
    {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.isGamePaused() ? (double) (float) minecraftRenderPartialTicksPausedField.get(mc) : mc.getRenderPartialTicks();
    }


    public static Pair<Float, Float> getEntityXYInWindow(Entity entity) throws IllegalAccessException
    {
        return getEntityXYInWindow(entity, 0, 0, 0);
    }

    public static Pair<Float, Float> getEntityXYInWindow(Entity entity, double xOffset, double yOffset, double zOffset) throws IllegalAccessException
    {
        double partialTick = getPartialTick();

        double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTick + xOffset;
        double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTick + yOffset;
        double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTick + zOffset;

        return get2DWindowCoordsFrom3DWorldCoords(x, y, z, partialTick);
    }

    public static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z) throws IllegalAccessException
    {
        return get2DWindowCoordsFrom3DWorldCoords(x, y, z, getPartialTick());
    }

    /**
     * When the entity is visible in the current projection, the returned values are its position in the window
     * When the entity is not visible in the current projection, the returned values are an off-screen position with the correct ratio to be used for an edge-of-screen indicator
     */
    private static Pair<Float, Float> get2DWindowCoordsFrom3DWorldCoords(double x, double y, double z, double partialTick) throws IllegalAccessException
    {
        //Based on GLU.gluProject()
        EntityPlayer player = Minecraft.getMinecraft().player;
        double px = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTick;
        double py = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTick;
        double pz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTick;

        FloatBuffer modelView = getModelViewMatrix();
        FloatBuffer projection = getProjectionMatrix();
        IntBuffer viewport = getViewportMatrix();

        float[] in = new float[4];
        float[] out = new float[4];

        in[0] = (float) (x - px);
        in[1] = (float) (y - py);
        in[2] = (float) (z - pz);
        in[3] = 1.0f;

        multMatrix(modelView, in, out);
        multMatrix(projection, out, in);

        if (in[3] == 0.0) return null;

        in[3] = (1.0f / in[3]) * 0.5f;

        boolean behind = in[3] < 0;
        if (behind)
        {
            //Offscreen, hehind znear plane
            in[0] = -in[0];
            in[1] = -in[1];
        }

        //x and y are between -0.5 and 0.5 if on window
        in[0] = in[0] * in[3];
        in[1] = in[1] * in[3];

        float scaleFactor = behind ? Math.abs(in[0] * 2) : Tools.max(Math.abs(in[0] * 2), Math.abs(in[1] * 2));
        //The line above this comment is for games that have pitch rotation limited to +/-(90*) or close to it (it's usually technically less than 90 to prevent some mathematical issues)
        //The line below this comment is what you would use in games that do not have said limitation; eg. many flying games, especially space-themed ones
//        float scaleFactor = Tools.max(Math.abs(in[0] * 2), Math.abs(in[1] * 2));

        if (behind || scaleFactor > 1)
        {
            //If offscreen, scale both x and y so that the maximum of the 2 is on the edge of the window
            in[0] /= scaleFactor;
            in[1] /= scaleFactor;
        }

        //Map x,y to viewport
        float xx = (0.5f + in[0]) * viewport.get(2) + viewport.get(0);
        float yy = (0.5f - in[1]) * viewport.get(3) + viewport.get(1);

        return new Pair<>(xx, yy);
    }

    private static void multMatrix(FloatBuffer m, float[] in, float[] out)
    {
        for (int i = 0; i < 4; i++)
        {
            out[i] = in[0] * m.get(i) + in[1] * m.get(i + 4) + in[2] * m.get(i + 8) + in[3] * m.get(i + 12);
        }
    }


    public static double getZNearDist()
    {
        FloatBuffer projection = getProjectionMatrix();
        return (2f * projection.get(11)) / (2f * projection.get(10) - 2f);
    }

    public static double getZNearWidth()
    {
        return getZNearDist() * 2 / getProjectionMatrix().get(0);
    }

    public static double getZNearHeight()
    {
        return getZNearDist() * 2 / getProjectionMatrix().get(5);
    }


    /**
     * This is not the width of the near plane!  This is the PORT width, not the VIEW width, ie. usually the window width
     */
    public static int getViewportWidth()
    {
        return getViewportMatrix().get(2);
    }

    /**
     * This is not the height of the near plane!  This is the PORT height, not the VIEW height, ie. usually the window height
     */
    public static int getViewportHeight()
    {
        return getViewportMatrix().get(3);
    }


    public static IntBuffer getViewportMatrix()
    {
        IntBuffer result = ByteBuffer.allocateDirect(16 << 2).asIntBuffer();
        GlStateManager.glGetInteger(GL11.GL_PROJECTION_MATRIX, result);
        return result;
    }

    public static FloatBuffer getProjectionMatrix()
    {
        FloatBuffer result = ByteBuffer.allocateDirect(16 << 2).asFloatBuffer();
        GlStateManager.getFloat(GL11.GL_PROJECTION_MATRIX, result);
        return result;
    }

    public static void setProjectionMatrix(FloatBuffer matrix)
    {
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GL11.glLoadMatrix(matrix);
    }

    public static FloatBuffer getModelViewMatrix()
    {
        FloatBuffer result = ByteBuffer.allocateDirect(16 << 2).asFloatBuffer();
        GlStateManager.getFloat(GL11.GL_MODELVIEW_MATRIX, result);
        return result;
    }

    public static void setModelViewMatrix(FloatBuffer matrix)
    {
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadMatrix(matrix);
    }


    public static Vec3d getCameraPosition()
    {
        return Minecraft.getMinecraft().player.getPositionVector().add(ActiveRenderInfo.getCameraPosition());
    }


    public static class RenderHUDEvent extends Event
    {
        RenderGameOverlayEvent.Pre parentEvent;
        byte scalingMode = SCALING_MC_GUI;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        int width = sr.getScaledWidth(), height = sr.getScaledHeight();

        public RenderHUDEvent(RenderGameOverlayEvent.Pre parentEvent)
        {
            this.parentEvent = parentEvent;
        }

        public RenderGameOverlayEvent.Pre getParentEvent()
        {
            return parentEvent;
        }

        public void setScalingMode(byte scalingMode)
        {
            if (this.scalingMode == scalingMode) return;

            double xRatio = width, yRatio = height;

            switch (scalingMode)
            {
                case SCALING_FULL:
                    width = Render.getViewportWidth();
                    height = Render.getViewportHeight();
                    break;

                case SCALING_MC_GUI:
                    width = sr.getScaledWidth();
                    height = sr.getScaledHeight();
                    break;

                default:
                    return;
            }

            this.scalingMode = scalingMode;

            xRatio /= width;
            yRatio /= height;

            GlStateManager.scale(xRatio, yRatio, 1);
        }

        public int getWidth()
        {
            return width;
        }

        public int getHeight()
        {
            return height;
        }
    }
}
