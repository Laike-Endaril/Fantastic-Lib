package com.fantasticsource.mctools.gui.element.other;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUIHorizontalSlider extends GUIGradientBorder
{
    private double amount = 0.5;
    private GUIGradientBorder slider;

    public GUIHorizontalSlider(GUIScreen screen, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter)
    {
        super(screen, width, height, 1d / 3, backgroundBorder, backgroundCenter);

        slider = new GUIGradientBorder(screen, -9999, 0, 0.05, 1, 1d / 3, sliderBorder, sliderCenter);
        add(slider);
    }

    public GUIHorizontalSlider(GUIScreen screen, double x, double y, double width, double height, Color backgroundBorder, Color backgroundCenter, Color sliderBorder, Color sliderCenter)
    {
        super(screen, x, y, width, height, 1d / 3, backgroundBorder, backgroundCenter);

        slider = new GUIGradientBorder(screen, -9999, 0, 0.05, 1, 1d / 3, sliderBorder, sliderCenter);
        add(slider);
    }

    @Override
    public void draw()
    {
        if (amount >= 0 && amount <= 1)
        {
            slider.x = (1 - slider.width) * amount;
        }
        else slider.x = -99999;

        super.draw();
    }

    @Override
    public void mouseWheel(int delta)
    {
        if (amount != -1 && isMouseWithin())
        {
            if (delta < 0)
            {
                amount -= 0.05;
                if (amount < 0) amount = 0;
            }
            else
            {
                amount += 0.05;
                if (amount > 1) amount = 1;
            }
        }

        runEditActions();
    }

    @Override
    public boolean mousePressed(int button)
    {
        boolean result = super.mousePressed(button);
        setActive(result);

        if (active && amount != -1)
        {
            amount = Tools.min(Tools.max((mouseX() - absoluteX() - slider.absoluteWidth() * 0.5) / (absoluteWidth() - slider.absoluteWidth()), 0), 1);
        }

        runEditActions();

        return result;
    }

    @Override
    public void mouseDrag(int button)
    {
        if (active && button == 0)
        {
            if (amount == -1) setActive(false);
            else amount = Tools.min(Tools.max((mouseX() - absoluteX() - slider.absoluteWidth() * 0.5) / (absoluteWidth() - slider.absoluteWidth()), 0), 1);
        }

        runEditActions();
    }

    public double getAmount()
    {
        return amount;
    }

    public void setAmount(double amount)
    {
        setAmount(amount, true);
    }

    public void setAmount(double amount, boolean runEditActions)
    {
        this.amount = amount;
        if (runEditActions) runEditActions();
    }
}
