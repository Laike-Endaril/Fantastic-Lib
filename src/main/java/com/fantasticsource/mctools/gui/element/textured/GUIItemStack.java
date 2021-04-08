package com.fantasticsource.mctools.gui.element.textured;

import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.view.GUIPanZoomView;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;

import java.nio.FloatBuffer;

public class GUIItemStack extends GUIElement
{
    private double unscaledWidth, unscaledHeight;
    private ItemStack stack;

    public GUIItemStack(GUIScreen screen, double unscaledWidth, double unscaledHeight, ItemStack stack)
    {
        super(screen, 1, 1);
        this.unscaledWidth = unscaledWidth;
        this.unscaledHeight = unscaledHeight;
        setItemStack(stack);
    }


    public GUIItemStack(GUIScreen screen, double x, double y, double unscaledWidth, double unscaledHeight, ItemStack stack)
    {
        super(screen, x, y, 1, 1);
        this.unscaledWidth = unscaledWidth;
        this.unscaledHeight = unscaledHeight;
        setItemStack(stack);
    }

    public ItemStack getItemStack()
    {
        return stack;
    }

    public void setItemStack(ItemStack stack)
    {
        this.stack = stack;
        tooltip = stack;
    }

    @Override
    public GUIItemStack recalc(int subIndexChanged)
    {
        width = unscaledWidth / screen.width;
        height = unscaledHeight / screen.height;

        //TODO this line is cancelling a scissor offset issue of unknown origin
        //TODO I might've fixed the root issue and not need this line?  Test alignment sometime...
//        width += (1 - unscaledWidth) / screen.width;

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
        FloatBuffer projection = Render.getCurrentProjectionMatrix();
        FloatBuffer modelView = Render.getCurrentModelViewMatrix();

        Render.setProjectionMatrix(GUIScreen.mcProjection);
        Render.setModelViewMatrix(GUIScreen.mcModelView);

        GlStateManager.enableTexture2D();
        RenderHelper.enableGUIStandardItemLighting();
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());

        GlStateManager.pushMatrix();
        GlStateManager.translate((double) absolutePxX() / sr.getScaleFactor(), (double) absolutePxY() / sr.getScaleFactor(), 0);
        if (parent instanceof GUIPanZoomView) GlStateManager.scale(((GUIPanZoomView) parent).getZoom(), ((GUIPanZoomView) parent).getZoom(), 1);
        Minecraft.getMinecraft().getRenderItem().renderItemIntoGUI(stack, 0, 0);
        GlStateManager.popMatrix();

        Render.setProjectionMatrix(projection);
        Render.setModelViewMatrix(modelView);

        RenderHelper.disableStandardItemLighting();
        GlStateManager.shadeModel(7425);

        drawChildren();
    }
}
