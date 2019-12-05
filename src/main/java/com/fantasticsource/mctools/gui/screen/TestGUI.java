package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIImage;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
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
                GUIAutocroppedView view = new GUIAutocroppedView(this);
                view.addAll(new GUIElement(this, 1, 0), new GUIText(this, "Test"));
                root.addAll(view, new GUIText(this, "Test2"));

                root.add(new GUIImage(this, new ResourceLocation(MODID, "image/monospace.png"), 8, 8, 0, 0, 1d / 16, 1d / 16));

                break;
        }

        test++;
    }
}
