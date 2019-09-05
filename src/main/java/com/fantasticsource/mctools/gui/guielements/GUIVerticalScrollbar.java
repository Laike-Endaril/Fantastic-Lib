package com.fantasticsource.mctools.gui.guielements;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.rect.GUIGradientBorder;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.view.GUIRectScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUIVerticalScrollbar extends GUIRectElement
{
    private GUIGradientBorder background, slider;
    private GUIRectScrollView scrollView;

    public GUIVerticalScrollbar(GUIScreen screen, double x, double y, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIRectScrollView scrollView)
    {
        super(screen, x, y, width, height);
        this.scrollView = scrollView;

        background = new GUIGradientBorder(screen, x, y, width, height, 1d / 3, backgroundBorder, backgroundCenter);

        slider = new GUIGradientBorder(screen, x, y, width, height / 10, 1d / 3, sliderBorder, sliderCenter);
    }

    @Override
    public void draw()
    {
        background.draw();

        if (scrollView.progress >= 0 && scrollView.progress <= 1)
        {
            slider.y = y + (height - slider.height) * scrollView.progress;
            slider.draw();
        }

        super.draw();
    }

    @Override
    public void mouseWheel(double x, double y, int delta)
    {
        if (scrollView.progress != -1 && (isMouseWithin() || scrollView.isMouseWithin()))
        {
            if (delta < 0)
            {
                scrollView.progress += 0.1;
                if (scrollView.progress > 1) scrollView.progress = 1;
            }
            else
            {
                scrollView.progress -= 0.1;
                if (scrollView.progress < 0) scrollView.progress = 0;
            }
        }
    }

    @Override
    public boolean isWithin(double x, double y)
    {
        return background.isWithin(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        setActive(super.mousePressed(x, y, button));

        if (active && scrollView.progress != -1)
        {
            scrollView.progress = Tools.min(Tools.max((y - this.y - slider.height * 0.5) / (height - slider.height), 0), 1);
        }

        return active;
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        if (active && button == 0)
        {
            if (scrollView.progress == -1) setActive(false);
            else scrollView.progress = Tools.min(Tools.max((y - this.y - slider.height * 0.5) / (height - slider.height), 0), 1);
        }
    }
}
