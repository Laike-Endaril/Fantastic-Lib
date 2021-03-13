package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.config.FantasticConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipAlterer
{
    @SubscribeEvent
    public static void renderTooltipPre(RenderTooltipEvent.Pre event)
    {
        double scale = FantasticConfig.tooltipScaling;
        GlStateManager.pushMatrix();
        GlStateManager.translate(event.getX() * (1 - scale), event.getY() * (1 - scale), 0);
        GlStateManager.scale(scale, scale, scale);
    }

    @SubscribeEvent
    public static void renderTooltipPost(RenderTooltipEvent.PostText event)
    {
        GlStateManager.popMatrix();
    }
}
