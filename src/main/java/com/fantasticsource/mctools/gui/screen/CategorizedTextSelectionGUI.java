package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIList;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

import java.util.*;

public class CategorizedTextSelectionGUI extends GUIScreen
{
    protected final Color LIGHT_ORANGE = Color.ORANGE.copy().setVF(0.8f), LIGHT_ORANGES[] = new Color[]{LIGHT_ORANGE, LIGHT_ORANGE.copy().setSF(0.5f), Color.WHITE};
    protected final HashMap<GUIList.Line, GUIList.Line> categoryToFirstSubElement = new HashMap<>();
    public final String title;

    public CategorizedTextSelectionGUI(GUIText clickedElement, String title, LinkedHashMap<String, String[]> categorizedOptions)
    {
        this(clickedElement, title, 1, categorizedOptions);
    }

    public CategorizedTextSelectionGUI(GUIText clickedElement, String title, double textScale, LinkedHashMap<String, String[]> categorizedOptions)
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
            public GUIElement[] newLineDefaultElements()
            {
                return new GUIElement[]{new GUIText(screen, clickedElement.getText())};
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
        GUIList.Line focusLine = null;
        for (Map.Entry<String, String[]> entry : categorizedOptions.entrySet())
        {
            list.addLine(); //Category spacer

            GUIList.Line line = list.addLine();
            GUIText text = (GUIText) line.getLineElement(0);
            text.setText(entry.getKey()).setColor(LIGHT_ORANGES[0], LIGHT_ORANGES[1], LIGHT_ORANGES[2]);
            text.addClickActions(() ->
            {
                if (entry.getValue().length > 0)
                {
                    int index = list.indexOf(line);

                    if (list.indexOf(categoryToFirstSubElement.get(line)) == -1)
                    {
                        //Show
                        List<String> options = Arrays.asList(entry.getValue());
                        Collections.reverse(options);
                        for (String option : options)
                        {
                            GUIList.Line line2 = list.addLine(index + 1);
                            GUIText text2 = (GUIText) line2.getLineElement(0);
                            text2.setText(option);
                            text2.addClickActions(() ->
                            {
                                clickedElement.setText(text2.getText());
                                close();
                            });
                            if (option.equals(clickedElement.getText())) text2.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
                            else text2.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);

                            line2.add(line2.indexOf(text2), new GUIText(this, "   "));
                        }

                        categoryToFirstSubElement.put(line, list.get(index + 1));
                    }
                    else
                    {
                        //Hide
                        for (int i = entry.getValue().length; i > 0; i--) list.remove(index + 1);
                    }
                }

                recalc();
            });
            line.add(line.indexOf(text), new GUIText(this, " "));

            if (entry.getValue().length > 0)
            {
                if (Tools.contains(entry.getValue(), clickedElement.getText()))
                {
                    //Start expanded because we contain the "currently selected" entry
                    focusLine = line;
                    for (String option : entry.getValue())
                    {
                        GUIList.Line line2 = list.addLine();
                        GUIText text2 = (GUIText) line2.getLineElement(0);
                        text2.setText(option);
                        text2.addClickActions(() ->
                        {
                            clickedElement.setText(text2.getText());
                            close();
                        });
                        if (option.equals(clickedElement.getText())) text2.setColor(getIdleColor(Color.PURPLE), getHoverColor(Color.PURPLE), Color.PURPLE);
                        else text2.setColor(getIdleColor(Color.WHITE), getHoverColor(Color.WHITE), Color.WHITE);

                        line2.add(line2.indexOf(text2), new GUIText(this, "   "));
                    }

                    categoryToFirstSubElement.put(line, list.get(list.indexOf(line) + 1));
                }
            }
        }
        list.addLine();

        if (focusLine != null) list.focus(focusLine);
    }

    @Override
    public String title()
    {
        return title;
    }
}
