package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class GUILabeledTextInput extends GUIView
{
    static
    {
        MinecraftForge.EVENT_BUS.register(GUILabeledTextInput.class);
    }

    public final GUIText label;
    public final GUITextInput input;

    public GUILabeledTextInput(GUIScreen screen, String label, String defaultInput, TextFilter filter)
    {
        super(screen, 1, new GUIText(screen, "").height);

        this.label = new GUIText(screen, label);
        add(this.label);

        input = new GUITextInput(screen, this.label.width, 0, defaultInput, filter);
        add(input);
    }

    public GUILabeledTextInput(GUIScreen screen, double x, double y, String label, String defaultInput, TextFilter filter)
    {
        super(screen, x, y, 1 - x, new GUIText(screen, "").height);

        this.label = new GUIText(screen, label);
        add(this.label);

        input = new GUITextInput(screen, this.label.width, 0, defaultInput, filter);
        add(input);
    }

    @SubscribeEvent
    public static void labelClick(GUILeftClickEvent event)
    {
        GUIElement element = event.getElement();
        if (element.parent instanceof GUILabeledTextInput)
        {
            GUILabeledTextInput labeledInput = (GUILabeledTextInput) element.parent;
            if (element == labeledInput.label)
            {
                GUITextInput input = labeledInput.input;
                input.cursorPosition = 0;
                input.selectorPosition = -1;
                input.setActive(true);
            }
        }
    }

    @Override
    public String toString()
    {
        return input.toString();
    }
}
