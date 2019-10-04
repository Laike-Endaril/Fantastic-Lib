package com.fantasticsource.mctools.gui;

import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

@SideOnly(Side.CLIENT)
public abstract class GUIScreen extends GuiScreen
{
    public static final Stack<ScreenEntry> SCREEN_STACK = new Stack<>();
    private static boolean ignoreClosure = false;

    public boolean drawStack = true;

    public static final FontRenderer FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;

    public int pxWidth, pxHeight;
    public float xPixel, yPixel;

    public static int[] currentScissor;

    public static double mouseX = 0.5, mouseY = 0.5;
    public final GUIView root;
    private ArrayList<Integer> mouseButtons = new ArrayList<>();
    private boolean initialized = false;

    public final ArrayList<Runnable> onClosedActions = new ArrayList<>();


    public GUIScreen()
    {
        pxWidth = Display.getWidth();
        pxHeight = Display.getHeight();

        xPixel = 1f / pxWidth;
        yPixel = 1f / pxHeight;

        root = new GUIView(this, 1, 1);
    }


    public static Color getIdleColor(Color activeColor)
    {
        return activeColor.copy().setVF(0.5f * activeColor.vf());
    }

    public static Color getHoverColor(Color activeColor)
    {
        return activeColor.copy().setVF(0.75f * activeColor.vf());
    }

    public boolean isInitialized()
    {
        return initialized;
    }

    public boolean isVisible()
    {
        return Minecraft.getMinecraft().currentScreen == this;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (pxWidth != Display.getWidth() || pxHeight != Display.getHeight()) recalc();

        if (drawStack)
        {
            double mX = GUIScreen.mouseX, mY = GUIScreen.mouseY;

            for (ScreenEntry entry : SCREEN_STACK)
            {
                GUIScreen.mouseX = entry.mouseX;
                GUIScreen.mouseY = entry.mouseY;
                entry.screen.draw();
            }

            GUIScreen.mouseX = mX;
            GUIScreen.mouseY = mY;
        }

        draw();
    }

    public boolean scissor()
    {
        int w = currentScissor[2] - currentScissor[0], h = currentScissor[3] - currentScissor[1];
        if (w < 0 || h < 0)
        {
            System.out.println("Scissor width and/or height is negative: " + w + ", " + h);
            return false;
        }

        GL11.glScissor(currentScissor[0], pxHeight - currentScissor[3], w, h);
        return true;
    }

    public void draw()
    {
        //Misc GL settings
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        //Matrix
        Render.startOrtho();
        GlStateManager.scale(pxWidth, pxHeight, 1);

        //Scissor
//        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        currentScissor = new int[]{0, 0, pxWidth, pxHeight};

        //Draw
        root.draw();

        //Undo scissor
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        //Undo matrix
        Render.endOrtho();

        //Undo misc GL settings
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void showStacked(GUIScreen screen)
    {
        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        if (current instanceof GUIScreen) SCREEN_STACK.push(new ScreenEntry((GUIScreen) current, mouseX, mouseY));

        ignoreClosure = true;
        Minecraft.getMinecraft().displayGuiScreen(screen);
        ignoreClosure = false;
    }

    @Override
    public void initGui()
    {
        if (!initialized)
        {
            initialized = true;
            init();
        }

        try
        {
            Mouse.setCursorPosition((int) (mouseX * Render.getViewportWidth()), (int) ((1 - mouseY) * Render.getViewportHeight()));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        mouseButtons.clear();
        root.recalc();
    }

    protected abstract void init();

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        if (w == width && h == height) return;
        super.onResize(mcIn, w, h);
        recalc();
    }

    private void recalc()
    {
        pxWidth = Display.getWidth();
        pxHeight = Display.getHeight();

        xPixel = 1f / pxWidth;
        yPixel = 1f / pxHeight;

        root.recalc();
    }

    @Override
    public void handleMouseInput()
    {
        //Cancel if outside game window
        if (!Mouse.isInsideWindow())
        {
            //Note: isInsideWindow returns true if you drag a mouse button from inside the window to outside it, until you release said button (inclusive)
            Mouse.getDWheel(); //Clear the wheel delta, or it will trigger when mouse re-enters window
            return;
        }


        //General setup
        mouseX = (double) Mouse.getX() / mc.displayWidth;
        int displayHeight = mc.displayHeight;
        mouseY = (double) (displayHeight - 1 - Mouse.getY()) / displayHeight;


        //Mouse wheel
        int delta = Mouse.getDWheel();
        if (delta != 0) root.mouseWheel(mouseX, mouseY, delta);


        //Mouse press, release, and drag
        int btn = Mouse.getEventButton();
        if (btn != -1)
        {
            if (Mouse.isButtonDown(btn))
            {
                if (!mouseButtons.contains(btn)) mouseButtons.add(btn);
                root.mousePressed(mouseX, mouseY, btn);
            }
            else
            {
                mouseButtons.remove((Integer) btn); //Need to cast so it uses the object-based removal and not the index-based removal
                root.mouseReleased(mouseX, mouseY, btn);
            }
        }
        else
        {
            for (int b : mouseButtons)
            {
                root.mouseDrag(mouseX, mouseY, b);
            }
        }
    }

    public void close()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.currentScreen == this) mc.player.closeScreenAndDropStack();
    }

    @Override
    public final void onGuiClosed()
    {
        //Need minimum delay to keep buggy stuff from happening (see where this method is called from)
        if (!ignoreClosure) ClientTickTimer.schedule(0, this::onClosed);
    }

    public void onClosed()
    {
        for (Runnable action : onClosedActions) action.run();

        if (SCREEN_STACK.size() > 0)
        {
            GUIScreen screen = SCREEN_STACK.pop().screen;
            mc.displayGuiScreen(screen);
            screen.onResize(Minecraft.getMinecraft(), width, height);
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);
        root.keyTyped(typedChar, keyCode);
    }

    public static class ScreenEntry
    {
        public final GUIScreen screen;
        public final double mouseX, mouseY;

        public ScreenEntry(GUIScreen screen, double mouseX, double mouseY)
        {
            this.screen = screen;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    public GUIScreen addOnClosedActions(Runnable... actions)
    {
        onClosedActions.addAll(Arrays.asList(actions));
        return this;
    }
}
