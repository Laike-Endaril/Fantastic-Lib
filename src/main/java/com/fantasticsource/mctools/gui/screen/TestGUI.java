package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.textured.GUIItemStack;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
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

                root.add(new GUIItemStack(this, 16, 16, new ItemStack(Items.BOW)));
                root.add(new GUIText(this, "Test"));
                root.add(new GUIItemStack(this, 16, 16, new ItemStack(Items.BOW)));
                break;
        }

        test++;
    }
}
