package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIVerticalScrollbar;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIScrollView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TestGUI extends GUIScreen
{
    public static int test = -1;

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
                test = 1;

            case 1:
                root.add(new GUIGradient(this, 1, 0.3, Color.BLACK.copy().setAF(0.4f)));
                GUIScrollView view = new GUIScrollView(this, 0.98, 0.4);
                root.add(view);
                root.add(new GUIVerticalScrollbar(this, 0.02, 0.4, Color.WHITE, Color.GRAY, Color.WHITE, Color.BLANK, view));
                root.add(new GUIGradient(this, 1, 0.3, Color.BLACK.copy().setAF(0.4f)));
                for (double i = 0; i <= 2; i += 0.1) view.add(new GUIText(this, 0, i, "" + i));
                break;

            case 0:
                root.add(new GUIGradient(this, 1, 1, Color.BLACK));
                root.add(new GUIGradient(this, 0.1, 0.1, 0.8, 0.8, Color.WHITE));
                break;
        }

        test--;
    }
}
