package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUIText;
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
    protected void init()
    {
        switch (test)
        {
            default:
                test = 0;

            case 0:
                root.add(new GUIGradient(this, -100, -100, 200, 200, Color.WHITE));

                GUIElement element = new GUIText(this, 0.1, 0.1, "Test", Color.RED, 0.25);
                root.add(element);
                element.add(new GUIGradient(this, -100, -100, 200, 200, Color.BLACK.copy().setAF(0.3f)));

                element = new GUIText(this, 0.1, 0.2, "Test", Color.RED, 0.5);
                root.add(element);
                element.add(new GUIGradient(this, -100, -100, 200, 200, Color.BLACK.copy().setAF(0.3f)));

                element = new GUIText(this, 0.1, 0.3, "Test", Color.RED, 1);
                root.add(element);
                element.add(new GUIGradient(this, -100, -100, 200, 200, Color.BLACK.copy().setAF(0.3f)));

                element = new GUIText(this, 0.1, 0.4, "Test", Color.RED, 2);
                root.add(element);
                element.add(new GUIGradient(this, -100, -100, 200, 200, Color.BLACK.copy().setAF(0.3f)));

                element = new GUIText(this, 0.1, 0.5, "Test", Color.RED, 4);
                root.add(element);
                element.add(new GUIGradient(this, -100, -100, 200, 200, Color.BLACK.copy().setAF(0.3f)));

                break;
        }

        test++;
    }
}
