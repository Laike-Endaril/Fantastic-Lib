package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloatColorChannel;
import com.fantasticsource.mctools.gui.element.text.filter.FilterIntColorChannel;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ColorSelectionGUI extends GUIScreen
{
    private Color color;
    private GUIColor colorElement;

    public ColorSelectionGUI(GUIColor clickedElement)
    {
        this(clickedElement, 1);
    }

    public ColorSelectionGUI(GUIColor clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        colorElement = clickedElement;
        color = colorElement.getValue();


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));

        GUIView left = new GUIView(this, 0.05, 0, 0.3, 1);
        GUIView center = new GUIView(this, 0.35, 0, 0.3, 1);
        GUIView right = new GUIView(this, 0.65, 0, 0.3, 1);
        left.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);

        left.add(new GUITextSpacer(this));
        left.add(new GUITextSpacer(this));
        left.add(new GUITextSpacer(this));
        left.add(new GUILabeledTextInput(this, "Red (0-255): ", "" + colorElement.getValue().r(), FilterIntColorChannel.INSTANCE));
        left.add(new GUILabeledTextInput(this, "Green (0-255): ", "" + colorElement.getValue().g(), FilterIntColorChannel.INSTANCE));
        left.add(new GUILabeledTextInput(this, "Blue (0-255): ", "" + colorElement.getValue().b(), FilterIntColorChannel.INSTANCE));
        left.add(new GUILabeledTextInput(this, "Alpha (0-255): ", "" + colorElement.getValue().a(), FilterIntColorChannel.INSTANCE));

        center.add(new GUITextSpacer(this));
        center.add(new GUILabeledTextInput(this, "Hex: ", colorElement.getValue().hex8(), FilterColor.INSTANCE));
        center.add(new GUITextSpacer(this));
        left.add(new GUILabeledTextInput(this, "Red (0-1): ", "" + colorElement.getValue().rf(), FilterFloatColorChannel.INSTANCE));
        left.add(new GUILabeledTextInput(this, "Green (0-1): ", "" + colorElement.getValue().gf(), FilterFloatColorChannel.INSTANCE));
        left.add(new GUILabeledTextInput(this, "Blue (0-1): ", "" + colorElement.getValue().bf(), FilterFloatColorChannel.INSTANCE));
        left.add(new GUILabeledTextInput(this, "Alpha (0-1): ", "" + colorElement.getValue().af(), FilterFloatColorChannel.INSTANCE));
    }

    @Override
    public String title()
    {
        return "Color";
    }

    @Override
    protected void init()
    {
    }
}
