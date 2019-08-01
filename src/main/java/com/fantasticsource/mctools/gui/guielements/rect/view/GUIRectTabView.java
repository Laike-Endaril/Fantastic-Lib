package com.fantasticsource.mctools.gui.guielements.rect.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUITextButton;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static com.fantasticsource.mctools.gui.GUIScreen.FONT_RENDERER;

public class GUIRectTabView extends GUIRectView
{
    private GUIRectElement[] tabs;
    public GUIRectView[] tabViews;
    private int current = 0;
    private boolean autocalcTabs = false, autocalcTabviews = false;

    public GUIRectTabView(GUIScreen screen, double x, double y, double width, double height, String... tabNames)
    {
        this(screen, x, y, width, height, genTabs(screen, width, tabNames));
        autocalcTabs = true;
        autocalcTabviews = true;
    }

    public GUIRectTabView(GUIScreen screen, double x, double y, double width, double height, String[] tabNames, GUIRectView... tabViews)
    {
        this(screen, x, y, width, height, genTabs(screen, width, tabNames), tabViews);
        autocalcTabs = true;
    }

    public GUIRectTabView(GUIScreen screen, double x, double y, double width, double height, GUIRectElement[] tabs, GUIRectView... tabViews)
    {
        super(screen, x, y, width, height);

        if (tabs.length != tabViews.length)
        {
            if (tabViews.length == 0)
            {
                this.tabViews = genTabViews(screen, tabs);
            }
            else throw new IllegalStateException("There must be the same number of tab names and tab elements!");
        }
        else this.tabViews = tabViews;

        for (GUIRectElement element : this.tabViews) element.parent = this;
        if (this.tabViews.length > 0)
        {
            tabs[0].setActive(true);
            children.add(this.tabViews[0]);
        }

        this.tabs = tabs;
        for (GUIRectElement element : tabs)
        {
            children.add(element);
            element.parent = this;
            element.setExternalDeactivation(true, true);
        }

        recalc();

        MinecraftForge.EVENT_BUS.register(this);
    }

    private static GUIRectView[] genTabViews(GUIScreen screen, GUIRectElement[] tabs)
    {
        GUIRectView[] result = new GUIRectView[tabs.length];

        GUIRectElement last = tabs[tabs.length - 1];
        double yy = last.y + last.height;

        for (int i = 0; i < result.length; i++)
        {
            result[i] = new GUIRectView(screen, 0, yy, 1, 1d - yy);
        }

        return result;
    }

    @Override
    public void recalc()
    {
        super.recalc();

        if (autocalcTabs)
        {
            double xx = 0, yy = 0;
            for (GUIRectElement tab : tabs)
            {
                if (xx + tab.width > width)
                {
                    yy += tab.height;
                    xx = 0;
                }

                tab.x = xx;
                tab.y = yy;

                xx += tab.width;
            }
            yy += tabs[0].height;

            if (autocalcTabviews)
            {
                for (GUIRectView view : tabViews)
                {
                    view.y = yy;
                    view.height = 1 - yy;
                }
            }
        }
    }

    private static GUIRectElement[] genTabs(GUIScreen screen, double width, String[] tabNames)
    {
        GUIRectElement[] result = new GUIRectElement[tabNames.length];

        double xx = 0, yy = 0;
        for (int i = 0; i < result.length; i++)
        {
            String name = tabNames[i];
            if (xx + ((double) (FONT_RENDERER.getStringWidth(name) - 1) / screen.width) + GUITextButton.DEFAULT_PADDING * 2 > width)
            {
                yy += ((double) (FONT_RENDERER.FONT_HEIGHT - 1) / screen.height) + GUITextButton.DEFAULT_PADDING * 2;
                xx = 0;
            }

            result[i] = new GUITextButton(screen, xx, yy, name);
            xx += result[i].width;
        }

        return result;
    }

    @SubscribeEvent
    public void tabClick(GUILeftClickEvent event)
    {
        GUIElement element = event.getElement();
        for (int i = 0; i < tabs.length; i++)
        {
            if (tabs[i] == element)
            {
                setActiveTab(i);
                break;
            }
        }
    }

    private void setActiveTab(int index)
    {
        if (index == current) return;
        GUIElement currentElement = tabs[index];

        for (GUIElement element : tabs) element.setActive(element == currentElement, true);

        children.remove(tabViews[current]);
        current = index;
        children.add(0, tabViews[current]);
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
