package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Arrays;

import static com.fantasticsource.tools.datastructures.Color.WHITE;

public class GUITabView extends GUIView
{
    public ArrayList<GUIElement> tabs = new ArrayList<>();
    public ArrayList<GUIView> tabViews = new ArrayList<>();
    private GUIElement tabBackground = null;
    private int current = 0;
    private boolean autocalcTabs = false, autocalcTabviews = false;

    public GUITabView(GUIScreen screen, double width, double height, String... tabNames)
    {
        this(screen, width, height, genTabs(screen, tabNames));
        autocalcTabs = true;
        autocalcTabviews = true;

        GUITextButton tab = (GUITextButton) tabs.get(0);
        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 0.1, tab.border, tab.center);
        add(0, tabBackground);

        recalc();
    }

    public GUITabView(GUIScreen screen, double width, double height, String[] tabNames, GUIView... tabViews)
    {
        this(screen, width, height, genTabs(screen, tabNames), tabViews);
        autocalcTabs = true;

        GUITextButton tab = (GUITextButton) tabs.get(0);
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
                this.tabViews = genTabViews(screen, tabs.length);
            }
            else throw new IllegalStateException("There must be the same number of tab names and tab elements!");
        }
        else this.tabViews.addAll(Arrays.asList(tabViews));

        for (GUIElement element : this.tabViews) element.parent = this;
        if (this.tabViews.size() > 0)
        {
            tabs[0].setActive(true);
            children.add(this.tabViews.get(0));
        }

        this.tabs.addAll(Arrays.asList(tabs));
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

        GUITextButton tab = (GUITextButton) tabs.get(0);
        tabBackground = new GUIGradientBorder(screen, 0, 0, 1, 1, 0.1, tab.border, tab.center);
        add(0, tabBackground);

        recalc();
    }

    public GUITabView(GUIScreen screen, double x, double y, double width, double height, String[] tabNames, GUIView... tabViews)
    {
        this(screen, x, y, width, height, genTabs(screen, tabNames), tabViews);
        autocalcTabs = true;

        GUITextButton tab = (GUITextButton) tabs.get(0);
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
                this.tabViews = genTabViews(screen, tabs.length);
            }
            else throw new IllegalStateException("There must be the same number of tab names and tab elements!");
        }
        else this.tabViews.addAll(Arrays.asList(tabViews));

        for (GUIElement element : this.tabViews) element.parent = this;
        if (this.tabViews.size() > 0)
        {
            tabs[0].setActive(true);
            children.add(this.tabViews.get(0));
        }

        this.tabs.addAll(Arrays.asList(tabs));
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

    private static ArrayList<GUIView> genTabViews(GUIScreen screen, int count)
    {
        ArrayList<GUIView> result = new ArrayList<>(count);

        for (int i = 0; i < count; i++)
        {
            result.add(new GUIView(screen, 0, 0, 1, 1));
        }

        return result;
    }

    public int currentTab()
    {
        return current;
    }

    public int addTab(String name)
    {
        GUIView view = new GUIView(screen, 0, 0, 1, 1);
        tabViews.add(view);
        view.parent = this;

        GUITextButton tab = new GUITextButton(screen, 0, 0, name, WHITE, T_GRAY);
        tabs.add(tab);
        add(tab);
        recalc();

        return tabs.size() - 1;
    }

    public void removeTab(int index)
    {
        tabViews.remove(index);

        GUIElement tab = tabs.get(index);
        if (current == index) setActiveTab(0);
        tabs.remove(index);
        remove(tab);
    }

    @Override
    public GUIElement recalc()
    {
        return recalc(0);
    }

    @Override
    public GUIElement recalc(int subIndexChanged)
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
            yy += tabs.get(0).height / height;

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
            GUIElement element = tabs.get(tabs.size() - 1);
            tabBackground.height = element.y + element.height;
        }

        return super.recalc(0);
    }

    public void setActiveTab(int index)
    {
        GUIElement currentElement = tabs.get(index);
        for (GUIElement element : tabs) element.setActive(element == currentElement, true);

        if (index == current) return;


        int i = children.indexOf(tabViews.get(current));
        children.remove(i);
        current = index;
        children.add(i, tabViews.get(current).recalc());
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
                for (int i = 0; i < tabs.size(); i++)
                {
                    GUIElement tab = tabs.get(i);
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
