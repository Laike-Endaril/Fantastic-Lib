package com.fantasticsource.mctools.gui.guielements.rect.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIGradientBorder;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUITextButton;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GUIRectTabView extends GUIRectView
{
    public GUIRectElement[] tabs;
    public GUIRectView[] tabViews;
    private GUIElement tabBackground = null;
    private int current = 0;
    private boolean autocalcTabs = false, autocalcTabviews = false;

    public GUIRectTabView(GUIScreen screen, double x, double y, double width, double height, String... tabNames)
    {
        this(screen, x, y, width, height, genTabs(screen, tabNames));
        autocalcTabs = true;
        autocalcTabviews = true;

        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 1, new Color(0xFFFFFFFF), new Color(0));
        add(0, tabBackground);

        recalc();
    }

    public GUIRectTabView(GUIScreen screen, double x, double y, double width, double height, String[] tabNames, GUIRectView... tabViews)
    {
        this(screen, x, y, width, height, genTabs(screen, tabNames), tabViews);
        autocalcTabs = true;

        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 1, new Color(0xFFFFFFFF), new Color(0));
        add(0, tabBackground);

        recalc();
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

    private static GUIRectElement[] genTabs(GUIScreen screen, String[] tabNames)
    {
        GUIRectElement[] result = new GUIRectElement[tabNames.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = new GUITextButton(screen, 0, 0, tabNames[i]);
        }

        return result;
    }

    private static GUIRectView[] genTabViews(GUIScreen screen, GUIRectElement[] tabs)
    {
        GUIRectView[] result = new GUIRectView[tabs.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = new GUIRectView(screen, 0, 0, 1, 1);
        }

        return result;
    }

    @Override
    public GUIElement recalc()
    {
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

        if (tabBackground != null)
        {
            GUIElement element = tabs[tabs.length - 1];
            tabBackground.height = element.y + element.height;
        }

        return super.recalc();
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

        int i = children.indexOf(tabViews[current]);
        children.remove(i);
        current = index;
        children.add(i, tabViews[current].recalc());
    }

    @Override
    protected void finalize() throws Throwable
    {
        super.finalize();
        MinecraftForge.EVENT_BUS.unregister(this);
    }
}
