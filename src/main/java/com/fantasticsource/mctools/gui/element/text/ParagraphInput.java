package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;

public class ParagraphInput extends MultilineTextInput
{
    public ParagraphInput(GUIScreen screen, double width, double height, String text)
    {
        super(screen, width, height, toLines(text));
    }

    public ParagraphInput(GUIScreen screen, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String text)
    {
        super(screen, width, height, color, hoverColor, activeColor, hightlightColor, toLines(text));
    }

    public ParagraphInput(GUIScreen screen, double x, double y, double width, double height, String text)
    {
        super(screen, x, y, width, height, toLines(text));
    }

    public ParagraphInput(GUIScreen screen, double x, double y, double width, double height, Color color, Color hoverColor, Color activeColor, Color hightlightColor, String text)
    {
        super(screen, x, y, width, height, color, hoverColor, activeColor, hightlightColor, toLines(text));
    }

    private static String[] toLines(String text)
    {
        return Tools.fixedSplit(text.replaceAll("\r", ""), "\n");
    }

    public String getText()
    {
        if (children.size() == 0) return "";


        StringBuilder result = new StringBuilder(((GUITextInput) children.get(0)).text);

        for (int i = 0; i < children.size(); i++)
        {
            result.append("\n").append(((GUITextInput) children.get(i)).text);
        }

        return result.toString();
    }
}
