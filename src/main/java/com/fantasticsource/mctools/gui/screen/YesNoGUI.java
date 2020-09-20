package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.text.GUINavbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class YesNoGUI extends GUIScreen
{
    public final String title;
    public boolean pressedYes = false;

    public YesNoGUI(String title, String message)
    {
        this(title, message, 1);
    }

    public YesNoGUI(String title, String message, double textScale)
    {
        super(textScale);

        this.title = title;


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        //Navigation bar
        GUINavbar navbar = new GUINavbar(this);


        //Add elements
        root.setSubElementAutoplaceMethod(GUIElement.AP_CENTERED_H_TOP_TO_BOTTOM);
        root.addAll
                (
                        new GUIDarkenedBackground(this),
                        navbar,
                        new GUITextSpacer(this),
                        new GUIText(this, message, Color.PURPLE),
                        new GUITextSpacer(this),
                        new GUITextButton(this, "YES", Color.GREEN).addClickActions(() ->
                        {
                            pressedYes = true;
                            close();
                        }),
                        new GUITextSpacer(this),
                        new GUITextButton(this, "NO", Color.RED).addClickActions(this::close)
                );
    }

    @Override
    public String title()
    {
        return title;
    }
}
