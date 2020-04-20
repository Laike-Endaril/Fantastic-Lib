package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class TextSelectionGUI extends GUIScreen
{
    public final String title;

    public TextSelectionGUI(GUIText clickedElement, String title, String... options)
    {
        this(clickedElement, title, 1, options);
    }

    public TextSelectionGUI(GUIText clickedElement, String title, double textScale, String... options)
    {
        super(textScale);

        this.title = title;


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        //Navigation bar
        GUINavbar navbar = new GUINavbar(this);


        GUIList list = new GUIList(this, false, 0.98, 1 - navbar.height)
        {
            @Override
            public GUIElement[] newLineDefaultElements(int lineIndex)
            {
                GUIText text = new GUIText(screen, clickedElement.getText());
                return new GUIElement[]{text.addClickActions(() ->
                {
                    clickedElement.setText(text.getText());
                    screen.close();
                })};
            }
        };
        GUIVerticalScrollbar scrollbar = new GUIVerticalScrollbar(this, 0.02, 1, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK, list);

        //Add elements
        root.addAll
                (
                        new GUIDarkenedBackground(this),
                        navbar.addRecalcActions(() ->
                        {
                            list.height = 1 - navbar.height;
                            scrollbar.height = 1 - navbar.height;
                        }),
                        list,
                        scrollbar
                );

        //Add options
        for (String option : options)
        {
            list.addLine();
            GUIText text = (GUIText) list.get(list.lineCount() - 1).getLineElement(0);
            text.setText(option);
            if (option.equals(clickedElement.getText())) text.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
            else text.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);
        }
    }

    @Override
    public String title()
    {
        return title;
    }
}
