package com.fantasticsource.mctools.gui.guielements.rect.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUITextRect;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class GUIRectTabView extends GUIRectView
{
    private GUIRectElement foreground, tabs[];
    private GUIRectView[] tabViews;
    private double lastScreenWidth, lastScreenHeight;

    public GUIRectTabView(GUIScreen screen, GUIRectElement foreground, double screenWidth, double screenHeight, GUIRectElement[] tabs, GUIRectView... tabViews)
    {
        super(screen, foreground.x, foreground.y, foreground.width, foreground.height);

        if (tabs.length != tabViews.length) throw new IllegalStateException("There must be the same number of tab names and tab elements!");
        this.tabs = tabs;
        for (GUIRectElement element : tabs)
        {
            children.add(element);
            element.parent = this;
        }
        this.tabViews = tabViews;
        for (GUIRectElement element : tabViews) element.parent = this;
        if (tabViews.length > 0) children.add(tabViews[0]);

        this.foreground = foreground;

        recalc(screenWidth, screenHeight);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public void recalc(double screenWidth, double screenHeight)
    {
        if (screenWidth == lastScreenWidth && screenHeight == lastScreenHeight) return;
        lastScreenWidth = screenWidth;
        lastScreenHeight = screenHeight;


        double pxWidth = screenWidth * width;
        for (GUIElement element : children)
        {
            if (element instanceof GUIRectElement)
            {
                if (element instanceof GUITextRect) ((GUITextRect) element).recalcHeight(pxWidth, screenHeight);
            }
        }
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


        foreground.draw();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    @SubscribeEvent
    public void tabClick(GUILeftClickEvent event)
    {
        GUIElement element = event.getElement();
        for (int i = 0; i < tabs.length; i++)
        {
            if (element == tabs[i])
            {
                GUIRectView view = tabViews[i];
                if (!children.contains(view))
                {
                    children.remove(children.get(children.size() - 1));
                    children.add(view);
                }
                break;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
