package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.text.GUITextButton;
import com.fantasticsource.mctools.gui.element.text.GUITextInput;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNotEmpty;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUIView;
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
                test = 7;

            case 7:
            {
                GUIAutocroppedView view = new GUIAutocroppedView(this, 0.25, 0.25, 0.1, new GUIGradientBorder(this, 1, 1, 0.3, Color.WHITE, Color.BLANK));
                root.add(view);
                view.add(new GUITextInput(this, "Test", FilterNotEmpty.INSTANCE));
            }

            case 6:
                root.add(new GUITextButton(this, "Test"));

            case 5:
            {
                GUIView view = new GUIView(this, 0.25, 0.25, 0.5, 0.5);
                root.add(view);
                view.add(new GUITextInput(this, "Test", FilterNotEmpty.INSTANCE));
                break;
            }

            case 4:
                root.add(new GUITextInput(this, "Test", FilterNotEmpty.INSTANCE));
                break;

            case 3:
            {
                GUIView view = new GUIView(this, 0.25, 0.25, 0.5, 0.5);
                root.add(view);
                view.add(new GUIGradient(this, -1, -1, 3, 3, Color.WHITE));
                break;
            }

            case 2:
                root.add(new GUIText(this, "Test"));
                break;

            case 1:
                root.add(new GUIGradientBorder(this, 1, 1, 0.1, Color.WHITE, Color.BLANK));
                break;

            case 0:
                root.add(new GUIGradient(this, 1, 1, Color.BLACK));
                root.add(new GUIGradient(this, 0.1, 0.1, 0.8, 0.8, Color.WHITE));
                break;
        }

        test--;
    }
}
