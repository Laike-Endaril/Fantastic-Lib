package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class TextSelectionGUI extends GUIScreen
{
    public final String title;

    public TextSelectionGUI(GUIText clickedElement, String title, String... options)
    {
        this(clickedElement, title, 1);
    }

    public TextSelectionGUI(GUIText clickedElement, String title, double textScale, String... options)
    {
        super(textScale);

        this.title = title;


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        //Background
        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));


        GUITextSpacer spacer = new GUITextSpacer(this, true);
        TextSelectionGUI gui = this;
        GUIList list = new GUIList(this, 0.98 - spacer.width * 2, 1, false)
        {
            @Override
            public GUIElement[] newLineDefaultElements()
            {
                GUIText text = new GUIText(gui, clickedElement.getText());
                return new GUIElement[]{text.addClickActions(() ->
                {
                    clickedElement.setText(text.getText());
                    gui.close();
                })};
            }
        };
        root.add(spacer.addRecalcActions(() -> list.width = 0.98 - spacer.width * 2));
        root.add(list);

        root.add(new GUITextSpacer(this, true));
        root.add(new GUIVerticalScrollbar(this, 0.98, 0, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list));

        //Add options
        for (String option : options)
        {
            list.addLine();
            GUIText text = (GUIText) list.get(list.lineCount() - 1).getLineElement(0);
            text.setText(option);
            if (option.equals(clickedElement.getText())) text.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
        }
    }

    @Override
    public String title()
    {
        return title;
    }
}
