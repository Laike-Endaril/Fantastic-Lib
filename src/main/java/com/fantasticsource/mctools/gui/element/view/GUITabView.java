package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;

import static com.fantasticsource.tools.datastructures.Color.WHITE;

public class GUITabView extends GUIView
{
    public GUIElement[] tabs;
    public GUIView[] tabViews;
    private GUIElement tabBackground = null;
    private int current = 0;
    private boolean autocalcTabs = false, autocalcTabviews = false;

    public GUITabView(GUIScreen screen, double width, double height, String... tabNames)
    {
        this(screen, width, height, genTabs(screen, tabNames));
        autocalcTabs = true;
        autocalcTabviews = true;

        GUITextButton tab = (GUITextButton) tabs[0];
        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 0.1, tab.border, tab.center);
        add(0, tabBackground);

        recalc();
    }

    public GUITabView(GUIScreen screen, double width, double height, String[] tabNames, GUIView... tabViews)
    {
        this(screen, width, height, genTabs(screen, tabNames), tabViews);
        autocalcTabs = true;

        GUITextButton tab = (GUITextButton) tabs[0];
        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 0.1, tab.border, tab.center);
        add(0, tabBackground);

        recalc();
    }

    public GUITabView(GUIScreen screen, double width, double height, GUIElement[] tabs, GUIView... tabViews)
    {
        super(screen, width, height);

        if (tabs.length != tabViews.length)
        {
            if (tabViews.length == 0)
            {
                this.tabViews = genTabViews(screen, tabs);
            }
            else throw new IllegalStateException("There must be the same number of tab names and tab elements!");
        }
        else this.tabViews = tabViews;

        for (GUIElement element : this.tabViews) element.parent = this;
        if (this.tabViews.length > 0)
        {
            tabs[0].setActive(true);
            children.add(this.tabViews[0]);
        }

        this.tabs = tabs;
        for (GUIElement element : tabs)
        {
            children.add(element);
            element.parent = this;
            element.setExternalDeactivation(true, true);
        }

        recalc();
    }

    public GUITabView(GUIScreen screen, double x, double y, double width, double height, String... tabNames)
    {
        this(screen, x, y, width, height, genTabs(screen, tabNames));
        autocalcTabs = true;
        autocalcTabviews = true;

        GUITextButton tab = (GUITextButton) tabs[0];
        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 0.1, tab.border, tab.center);
        add(0, tabBackground);

        recalc();
    }

    public GUITabView(GUIScreen screen, double x, double y, double width, double height, String[] tabNames, GUIView... tabViews)
    {
        this(screen, x, y, width, height, genTabs(screen, tabNames), tabViews);
        autocalcTabs = true;

        GUITextButton tab = (GUITextButton) tabs[0];
        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 0.1, tab.border, tab.center);
        add(0, tabBackground);

        recalc();
    }

    public GUITabView(GUIScreen screen, double x, double y, double width, double height, GUIElement[] tabs, GUIView... tabViews)
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

        for (GUIElement element : this.tabViews) element.parent = this;
        if (this.tabViews.length > 0)
        {
            tabs[0].setActive(true);
            children.add(this.tabViews[0]);
        }

        this.tabs = tabs;
        for (GUIElement element : tabs)
        {
            children.add(element);
            element.parent = this;
            element.setExternalDeactivation(true, true);
        }

        recalc();
    }

    private static GUIElement[] genTabs(GUIScreen screen, String[] tabNames)
    {
        GUIElement[] result = new GUIElement[tabNames.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = new GUITextButton(screen, 0, 0, tabNames[i], WHITE, T_GRAY);
        }

        return result;
    }

    private static GUIView[] genTabViews(GUIScreen screen, GUIElement[] tabs)
    {
        GUIView[] result = new GUIView[tabs.length];

        for (int i = 0; i < result.length; i++)
        {
            result[i] = new GUIView(screen, 0, 0, 1, 1);
        }

        return result;
    }

    public int currentTab()
    {
        return current;
    }

    @Override
    public GUIElement recalc()
    {
        if (autocalcTabs)
        {
            double xx = 0, yy = 0;
            for (GUIElement tab : tabs)
            {
                if (xx + tab.width / width > 1)
                {
                    yy += tab.height / height;
                    xx = 0;
                }

                tab.x = xx;
                tab.y = yy;

                xx += tab.width / width;
            }
            yy += tabs[0].height / height;

            if (autocalcTabviews)
            {
                for (GUIView view : tabViews)
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

    public void setActiveTab(int index)
    {
        GUIElement currentElement = tabs[index];
        for (GUIElement element : tabs) element.setActive(element == currentElement, true);

        if (index == current) return;


        int i = children.indexOf(tabViews[current]);
        children.remove(i);
        current = index;
        children.add(i, tabViews[current].recalc());
    }

    @Override
    public boolean mouseReleased(double x, double y, int button)
    {
        boolean result = false;
        if (button == 0)
        {
            if (active && isMouseWithin())
            {
                if (!MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this))) click();
                result = true;
            }
            setActive(false);
        }

        int index = -1;
        for (GUIElement child : (ArrayList<GUIElement>) children.clone())
        {
            if (child.mouseReleased(x - this.x, y - this.y, button))
            {
                for (int i = 0; i < tabs.length; i++)
                {
                    GUIElement tab = tabs[i];
                    if (child == tab)
                    {
                        index = i;
                        break;
                    }
                }
            }
        }

        if (index != -1 && index != current) setActiveTab(index);
        else setActiveTab(current);

        return result;
    }
}
