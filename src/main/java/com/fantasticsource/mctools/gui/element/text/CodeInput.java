package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.datastructures.Color;

public class CodeInput extends MultilineTextInput
{
    public CodeInput(GUIScreen screen, double width, double height, String... lines)
    {
        super(screen, width, height, lines);
    }

    public CodeInput(GUIScreen screen, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String... lines)
    {
        super(screen, width, height, color, hoverColor, activeColor, hightlightColor, lines);
    }

    public CodeInput(GUIScreen screen, double x, double y, double width, double height, String... lines)
    {
        super(screen, x, y, width, height, lines);
    }

    public CodeInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String... lines)
    {
        super(screen, x, y, width, height, color, hoverColor, activeColor, hightlightColor, lines);
    }
}
