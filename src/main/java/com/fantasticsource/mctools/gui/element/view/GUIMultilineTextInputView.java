package com.fantasticsource.mctools.gui.element.view;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.text.GUIMultilineTextInput;

public class GUIMultilineTextInputView extends GUIScrollView
{
    public final GUIMultilineTextInput multilineTextInput;

    public GUIMultilineTextInputView(GUIScreen screen, double width, double height, GUIMultilineTextInput multilineTextInput)
    {
        super(screen, width, height, multilineTextInput);

        this.multilineTextInput = multilineTextInput;
    }

    public GUIMultilineTextInputView(GUIScreen screen, double x, double y, double width, double height, GUIMultilineTextInput multilineTextInput)
    {
        super(screen, x, y, width, height, multilineTextInput);

        this.multilineTextInput = multilineTextInput;
    }

    @Override
    public GUIMultilineTextInputView recalc(int subIndexChanged)
    {
        if (multilineTextInput == null) return this;

        recalcThisOnly();

        //Focus on current line
        int line = multilineTextInput.cursorLine();
        double ratio = 1d / multilineTextInput.fullLineCount();
        double lineTop = line * ratio * internalHeight;
        double lineBottom = (line + 1) * ratio * internalHeight;
        if (lineTop < top) progress = lineTop / (internalHeight - 1);
        else if (lineBottom > bottom) progress = (lineBottom - 1) / (internalHeight - 1);

        super.recalc(subIndexChanged);

        return this;
    }


    public boolean valid()
    {
        return multilineTextInput.valid();
    }

    public String getText()
    {
        return multilineTextInput.getText();
    }

    public void setText(String text)
    {
        multilineTextInput.setText(text);
    }
}
