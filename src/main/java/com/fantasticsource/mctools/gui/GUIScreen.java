package com.fantasticsource.mctools.gui;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import com.fantasticsource.mctools.ClientTickTimer;
import com.fantasticsource.mctools.MCTools;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.ReflectionTool;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Stack;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

@SideOnly(Side.CLIENT)
public abstract class GUIScreen extends GuiScreen
{
    protected static final Field CONFIG_MANAGER_CONFIGS_FIELD = ReflectionTool.getField(ConfigManager.class, "CONFIGS");

    public static final Stack<ScreenEntry> SCREEN_STACK = new Stack<>();
    public static final FontRenderer FONT_RENDERER = Minecraft.getMinecraft().fontRenderer;
    public static int[] currentScissor;
    public static double mouseX = 0.5, mouseY = 0.5;
    public static FloatBuffer mcProjection, mcModelView;
    private static boolean ignoreClosure = false;


    public final GUIView root, tooltips;
    public ItemStack tooltipStack = null;
    public final ArrayList<Runnable> onClosedActions = new ArrayList<>();
    public final double textScale;
    public boolean drawStack = true, closeIfStackedOn = false;
    public int pxWidth, pxHeight;
    public float xPixel, yPixel;
    private ArrayList<Integer> mouseButtons = new ArrayList<>();
    private boolean initialized = false;
    public final LinkedHashMap<String, Namespace> namespaces = new LinkedHashMap<>();


    public GUIScreen()
    {
        this(1);
    }

    public GUIScreen(double textScale)
    {
        boolean found = false;
        double mul = 1;
        for (String s : FantasticConfig.guiSettings.perGUIScaling)
        {
            String[] tokens = Tools.fixedSplit(s, ",");
            if (getClass().getName().equals(tokens[0]))
            {
                mul = Double.parseDouble(tokens[1]);
                found = true;
            }
        }
        this.textScale = textScale * mul;
        if (!found)
        {
            String[] entries = new String[FantasticConfig.guiSettings.perGUIScaling.length + 1];
            System.arraycopy(FantasticConfig.guiSettings.perGUIScaling, 0, entries, 0, FantasticConfig.guiSettings.perGUIScaling.length);
            entries[entries.length - 1] = getClass().getName() + ", 1";
            FantasticConfig.guiSettings.perGUIScaling = entries;
            try
            {
                MCTools.saveConfig(MODID);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
        }


        pxWidth = Display.getWidth();
        pxHeight = Display.getHeight();

        xPixel = 1f / pxWidth;
        yPixel = 1f / pxHeight;

        root = new GUIView(this, 1, 1);
        tooltips = new GUIView(this, 1, 1);
    }

    public static Color getIdleColor(Color activeColor)
    {
        return activeColor.copy().setVF(0.5f * activeColor.vf());
    }

    public static Color getHoverColor(Color activeColor)
    {
        return activeColor.copy().setVF(0.75f * activeColor.vf());
    }


    public void show()
    {
        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) showStacked();
        else showUnstacked();
    }

    public void showUnstacked()
    {
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    public void showStacked()
    {
        GuiScreen current = Minecraft.getMinecraft().currentScreen;
        if (!(current instanceof GUIScreen) || !((GUIScreen) current).closeIfStackedOn) SCREEN_STACK.push(new ScreenEntry(current, mouseX, mouseY));

        ignoreClosure = true;
        Minecraft.getMinecraft().displayGuiScreen(this);
        ignoreClosure = false;
    }


    public static void show(GUIScreen screen)
    {
        screen.show();
    }

    public static void showUnstacked(GUIScreen screen)
    {
        screen.showUnstacked();
    }

    public static void showStacked(GUIScreen screen)
    {
        screen.showStacked();
    }


    public abstract String title();

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
        mcProjection = Render.getCurrentProjectionMatrix();
        mcModelView = Render.getCurrentModelViewMatrix();

        if (pxWidth != Display.getWidth() || pxHeight != Display.getHeight()) recalc();

        if (drawStack)
        {
            double mX = GUIScreen.mouseX, mY = GUIScreen.mouseY;

            for (ScreenEntry entry : SCREEN_STACK)
            {
                if (entry.screen instanceof GUIScreen)
                {
                    GUIScreen.mouseX = entry.mouseX;
                    GUIScreen.mouseY = entry.mouseY;
                    ((GUIScreen) entry.screen).draw();
                }
                else
                {
                    entry.screen.drawScreen((int) (Display.getWidth() * entry.mouseX), (int) (Display.getHeight() * entry.mouseY), partialTicks);
                }
            }

            GUIScreen.mouseX = mX;
            GUIScreen.mouseY = mY;
        }

        draw();
    }

    public boolean scissor()
    {
        int w = currentScissor[2] - currentScissor[0], h = currentScissor[3] - currentScissor[1];
        if (w < 1 || h < 1) return false;

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
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        currentScissor = new int[]{0, 0, pxWidth, pxHeight};


        //Draw normal elements
        root.draw();

        //Draw and clear text tooltips
        currentScissor = new int[]{0, 0, pxWidth, pxHeight};
        GlStateManager.disableDepth();
        if (isVisible()) tooltips.draw();
        tooltips.clear();

        //Undo scissor
        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        //Undo matrix
        Render.endOrtho();

        //Undo misc GL settings
        GlStateManager.enableDepth();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();

        //Draw and clear itemstack tooltip
        if (isVisible() && tooltipStack != null) renderToolTip(tooltipStack);
        tooltipStack = null;
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
            Mouse.setCursorPosition((int) (mouseX * Render.getStoredViewportWidth()), (int) ((1 - mouseY) * Render.getStoredViewportHeight()));
        }
        catch (IllegalAccessException e)
        {
            e.printStackTrace();
        }

        mouseButtons.clear();
        root.recalc(0);

        Keyboard.enableRepeatEvents(true);
    }

    protected void init()
    {
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        if (w == width && h == height) return;
        super.onResize(mcIn, w, h);
        recalc();
    }

    protected void recalc()
    {
        pxWidth = Display.getWidth();
        pxHeight = Display.getHeight();

        xPixel = 1f / pxWidth;
        yPixel = 1f / pxHeight;

        root.recalc(0);
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
        if (delta != 0) root.mouseWheel(delta);


        //Mouse press, release, and drag
        int btn = Mouse.getEventButton();
        if (btn != -1)
        {
            if (Mouse.isButtonDown(btn))
            {
                if (!mouseButtons.contains(btn)) mouseButtons.add(btn);
                root.mousePressed(btn);
            }
            else
            {
                mouseButtons.remove((Integer) btn); //Need to cast so it uses the object-based removal and not the index-based removal
                root.mouseReleased(btn);
            }
        }
        else
        {
            for (int b : mouseButtons)
            {
                root.mouseDrag(b);
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
            GuiScreen screen = SCREEN_STACK.pop().screen;
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

    public GUIScreen addOnClosedActions(Runnable... actions)
    {
        onClosedActions.addAll(Arrays.asList(actions));
        return this;
    }

    public static class ScreenEntry
    {
        public final GuiScreen screen;
        public final double mouseX, mouseY;

        public ScreenEntry(GuiScreen screen, double mouseX, double mouseY)
        {
            this.screen = screen;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    public void renderToolTip(ItemStack stack)
    {
        ScaledResolution sr = new ScaledResolution(mc);
        super.renderToolTip(stack, Mouse.getX() * sr.getScaledWidth() / mc.displayWidth, sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / mc.displayHeight - 1);
    }
}
