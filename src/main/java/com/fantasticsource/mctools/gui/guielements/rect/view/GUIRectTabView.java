package com.fantasticsource.mctools.gui.guielements.rect.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GUIRectTabView extends GUIRectView
{
    private GUIRectElement[] tabs;
    private GUIRectView[] tabViews;
    private double lastScreenWidth, lastScreenHeight;

    public GUIRectTabView(GUIScreen screen, double x, double y, double width, double height, GUIRectElement[] tabs, GUIRectView... tabViews)
    {
        super(screen, x, y, width, height);

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

        recalc();

        MinecraftForge.EVENT_BUS.register(this);
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
