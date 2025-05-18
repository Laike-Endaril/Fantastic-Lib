package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUIVerticalScrollbar extends GUIGradientBorder
{
    private GUIGradientBorder slider;
    private GUIScrollView scrollView;

    public GUIVerticalScrollbar(GUIScreen screen, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIScrollView scrollView)
    {
        super(screen, width, height, 1d / 3, backgroundBorder, backgroundCenter);
        this.scrollView = scrollView;
        scrollView.scrollbar = this;

        slider = new GUIGradientBorder(screen, 0, -9999, 1, 1d / 10, 1d / 3, sliderBorder, sliderCenter);
        add(slider);
    }

    public GUIVerticalScrollbar(GUIScreen screen, double x, double y, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter, GUIScrollView scrollView)
    {
        super(screen, x, y, width, height, 1d / 3, backgroundBorder, backgroundCenter);
        this.scrollView = scrollView;

        slider = new GUIGradientBorder(screen, 0, -9999, 1, 1d / 10, 1d / 3, sliderBorder, sliderCenter);
        add(slider);
    }

    @Override
    public void draw()
    {
        double progress = scrollView.getProgress();
        if (progress >= 0 && progress <= 1)
        {
            slider.y = (1 - slider.height) * progress;
        }
        else slider.y = -99999;

        super.draw();
    }

    @Override
    public void mouseWheel(int delta)
    {
        double progress = scrollView.getProgress();
        if (progress != -1 && (isMouseWithin() || scrollView.isMouseWithin()))
        {
            if (delta < 0)
            {
                progress += 0.25 / scrollView.internalHeight;
                if (progress > 1) progress = 1;
            }
            else
            {
                progress -= 0.25 / scrollView.internalHeight;
                if (progress < 0) progress = 0;
            }
        }
        scrollView.setProgress(progress);
    }

    @Override
    public boolean mousePressed(int button)
    {
        boolean result = super.mousePressed(button);
        setActive(result);

        if (active && scrollView.getProgress() != -1)
        {
            scrollView.setProgress(Tools.min(Tools.max((mouseY() - absoluteY() - slider.absoluteHeight() * 0.5) / (absoluteHeight() - slider.absoluteHeight()), 0), 1));
        }

        return result;
    }

    @Override
    public void mouseDrag(int button)
    {
        if (active && button == 0)
        {
            if (scrollView.getProgress() == -1) setActive(false);
            else scrollView.setProgress(Tools.min(Tools.max((mouseY() - absoluteY() - slider.absoluteHeight() * 0.5) / (absoluteHeight() - slider.absoluteHeight()), 0), 1));
        }
    }
}
