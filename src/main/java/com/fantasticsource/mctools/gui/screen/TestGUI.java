package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIEllipse;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUILine;
import com.fantasticsource.mctools.gui.element.text.GUIFadingText;
import com.fantasticsource.mctools.gui.element.textured.GUIImage;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

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
                root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK));

                GUIPanZoomView view = new GUIPanZoomView(this, 1, 1);
                root.add(view);

                view.add(new GUIEllipse(this, 0, 0, 1, 1, 64, Color.GRAY));
                view.add(new GUILine(this, 0, 0, 1, 1, Color.WHITE));
                view.add(new GUILine(this, 0, 1, 1, 0, Color.RED));
                view.add(new GUILine(this, 0, 0, 1, 0, Color.GREEN));
                view.add(new GUILine(this, 0, 0, 0, 1, Color.BLUE));
                view.add(new GUIFadingText(this, 0, 0, "Test", 180, 240, Color.WHITE.copy().setAF(0.3f)));
                view.add(new GUIImage(this, 0, 0, 64, 64, new ResourceLocation(MODID, "image/gui.png")).ignoreMCGUIScale(true).setTooltip("Test"));
                view.add(new GUIImage(this, 0.5, 0, 64, 64, new ResourceLocation(MODID, "image/gui.png")).setTooltip("Test"));
                break;
        }

        test++;
    }
}
