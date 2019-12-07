package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUIMultilineTextInput;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextInput;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TestGUI extends GUIScreen
{
    public static int test;

    @SubscribeEvent
    public static void interactAir(PlayerInteractEvent.RightClickEmpty event)
    {
        if (event.getSide() == Side.CLIENT && event.getHand() == EnumHand.MAIN_HAND) Minecraft.getMinecraft().displayGuiScreen(new TestGUI());
    }

    @Override
    public String title()
    {
        return "Test";
    }

    @Override
    protected void init()
    {
        switch (test)
        {
            default:
                test = 0;

            case 0:
                root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));

                root.add(new GUIText(this, "aaaa"));
                root.add(new GUIText(this, "IIII"));
                root.add(new GUIText(this, "\n"));
                root.add(new GUIText(this, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
                root.add(new GUIText(this, "IIII"));
                root.add(new GUIText(this, "\n"));
                root.add(new GUIText(this, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"));
                root.add(new GUIText(this, "IIII"));

                root.add(new GUIText(this, "\n"));
                root.add(new GUITextInput(this, "aaaa", FilterNone.INSTANCE));
                root.add(new GUITextInput(this, "IIII", FilterNone.INSTANCE));
                root.add(new GUIText(this, "\n"));
                root.add(new GUITextInput(this, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", FilterNone.INSTANCE));
                root.add(new GUITextInput(this, "IIII", FilterNone.INSTANCE));
                root.add(new GUIText(this, "\n"));
                root.add(new GUITextInput(this, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", FilterNone.INSTANCE));
                root.add(new GUITextInput(this, "IIII", FilterNone.INSTANCE));

                root.add(new GUIText(this, "\n"));
                root.add(new GUIMultilineTextInput(this, "a", FilterNone.INSTANCE));
                break;
        }

        test++;
    }
}
