package com.fantasticsource.mctools.gui.guielements.rect.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.guielements.GUIElement;
import com.fantasticsource.mctools.gui.guielements.rect.GUIRectElement;
import com.fantasticsource.mctools.gui.guielements.rect.text.filter.TextFilter;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class GUILabeledTextInput extends GUIRectElement
{
    private GUITextRect label;
    private GUITextInputRect input;

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String labelText, String defaultText, TextFilter filter)
    {
        super(screen, x, y, 0, 0);

        label = new GUITextRect(screen, 0, y, labelText, Color.WHITE);
        input = new GUITextInputRect(screen, label.width, 0, defaultText, filter);

        recalc();
    }

    @Override
    public GUIElement recalc()
    {
        super.recalc();

        width = label.width + input.width;
        height = Tools.max(label.height, input.height);

        return this;
    }
}
