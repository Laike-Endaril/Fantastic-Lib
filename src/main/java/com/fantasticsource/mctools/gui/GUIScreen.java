package com.fantasticsource.mctools.gui;

import com.fantasticsource.mctools.gui.guielements.GUIElement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

import java.io.IOException;
import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public abstract class GUIScreen extends GuiScreen
{
    public static double mouseX = 0.5, mouseY = 0.5;
    protected ArrayList<GUIElement> guiElements = new ArrayList<>();
    private ArrayList<Integer> mouseButtons = new ArrayList<>();

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.shadeModel(7425);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        GlStateManager.pushMatrix();
        GlStateManager.scale(width, height, 1);

        for (GUIElement element : guiElements) element.draw();

        GlStateManager.popMatrix();

        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    @Override
    public void initGui()
    {
        guiElements.clear();
        mouseButtons.clear();
    }

    @Override
    public void onResize(Minecraft mcIn, int w, int h)
    {
        if (w == width && h == height) return;

        super.onResize(mcIn, w, h);
        for (GUIElement element : guiElements) element.recalc();
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
        if (delta != 0)
        {
            for (GUIElement element : guiElements)
            {
                element.mouseWheel(mouseX, mouseY, delta);
            }
        }


        //Mouse press, release, and drag
        int btn = Mouse.getEventButton();
        if (btn != -1)
        {
            if (Mouse.isButtonDown(btn))
            {
                mouseButtons.add(btn);
                for (GUIElement element : guiElements)
                {
                    element.mousePressed(mouseX, mouseY, btn);
                }
            }
            else
            {
                mouseButtons.remove((Integer) btn); //Need to cast so it uses the object-based removal and not the index-based removal
                for (GUIElement element : guiElements)
                {
                    element.mouseReleased(mouseX, mouseY, btn);
                }
            }
        }
        else
        {
            for (int b : mouseButtons)
            {
                for (GUIElement element : guiElements)
                {
                    element.mouseDrag(mouseX, mouseY, b);
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        super.keyTyped(typedChar, keyCode);

        for (GUIElement element : guiElements)
        {
            element.keyTyped(typedChar, keyCode);
        }
    }
}
