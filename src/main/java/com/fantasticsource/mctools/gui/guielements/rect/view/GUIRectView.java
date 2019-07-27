package com.fantasticsource.mctools.gui.guielements.rect.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;

public class GUIRectView extends GUIRectElement
{
    public GUIRectView(GUIScreen screen, double x, double y, double width, double height)
    {
        super(screen, x, y, width, height);
    }

    @Override
    public void draw()
    {
        double screenWidth = screen.width, screenHeight = screen.height;

        int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
        double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor((int) (x * wScale), (int) ((1 - (y + height)) * hScale), (int) (width * wScale), (int) (height * hScale));

        for (GUIElement element : children)
        {
            if (element.x + element.width < 0 || element.x > width || element.y + element.height < 0 || element.y >= height) continue;
            element.draw();
        }

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }
}
