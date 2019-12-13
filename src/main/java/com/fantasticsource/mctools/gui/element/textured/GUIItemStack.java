package com.fantasticsource.mctools.gui.element.textured;

import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.GUIText;
import com.fantasticsource.mctools.gui.element.view.GUIAutocroppedView;
import com.fantasticsource.mctools.gui.element.view.GUITooltipView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

import java.nio.FloatBuffer;

public class GUIItemStack extends GUIElement
{
    private double scaledWidth, scaledHeight;
    private ItemStack stack;

    public GUIItemStack(GUIScreen screen, double unscaledWidth, double unscaledHeight, ItemStack stack)
    {
        super(screen, 1, 1);

        scaledWidth = unscaledWidth * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        scaledHeight = unscaledHeight * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

        this.stack = stack;

        tooltip = new GUITooltipView(screen);
        tooltip.setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
        Minecraft mc = Minecraft.getMinecraft();

        for (String line : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.ADVANCED))
        {
            GUIAutocroppedView view = new GUIAutocroppedView(screen, 0.3);
            view.add(new GUIText(screen, line));
            tooltip.add(view);
        }
    }


    public GUIItemStack(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ItemStack stack)
    {
        super(screen, x, y, 1, 1);

        this.scaledWidth = unscaledWidth * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
        this.scaledHeight = unscaledHeight * 2 / new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

        this.stack = stack;

        tooltip = new GUITooltipView(screen);
        tooltip.setSubElementAutoplaceMethod(AP_X_0_TOP_TO_BOTTOM);
        Minecraft mc = Minecraft.getMinecraft();

        for (String line : stack.getTooltip(mc.player, ITooltipFlag.TooltipFlags.ADVANCED))
        {
            GUIAutocroppedView view = new GUIAutocroppedView(screen, 0.3);
            view.add(new GUIText(screen, line));
            tooltip.add(view);
        }
    }


    @Override
    public GUIItemStack recalc(int subIndexChanged)
    {
        width = scaledWidth / screen.width;
        height = scaledHeight / screen.height;

        //TODO this line is cancelling a scissor offset issue of unknown origin; offset = 1 - ()
        //TODO I might've fixed the root issue and not need this line?
//        width += (1 - scaledWidth) / screen.width;

        if (parent != null)
        {
            width /= parent.absoluteWidth();
            height /= parent.absoluteHeight();
        }

        recalcAndRepositionSubElements(0);

        postRecalc();

        return this;
    }

    @Override
    public void draw()
    {
        //TODO Later-drawn GUIItemStacks render on top of tooltips

        FloatBuffer projection = Render.getProjectionMatrix();
        FloatBuffer modelView = Render.getModelViewMatrix();

        Render.setProjectionMatrix(GUIScreen.mcProjection);
        Render.setModelViewMatrix(GUIScreen.mcModelView);

        GlStateManager.enableTexture2D();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, absolutePxX() / sr.getScaleFactor(), absolutePxY() / sr.getScaleFactor());

        Render.setProjectionMatrix(projection);
        Render.setModelViewMatrix(modelView);


        drawChildren();
    }
}
