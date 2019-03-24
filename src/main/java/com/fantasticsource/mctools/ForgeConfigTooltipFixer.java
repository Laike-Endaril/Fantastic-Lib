package com.fantasticsource.mctools;

import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class ForgeConfigTooltipFixer
{
    @SubscribeEvent
    public static void configTooltip(RenderTooltipEvent.Pre event)
    {
        List<String> lines = event.getLines();
        boolean applyFix = false;

        for (String string : lines)
        {
            if (string.contains("FIXNEWLINES"))
            {
                applyFix = true;
                break;
            }
        }

        if (applyFix)
        {
            String[] oldLines = lines.toArray(new String[lines.size()]);
            lines = new ArrayList<>();
            for (String string : oldLines)
            {
                for (String s : string.split("\\\\n"))
                {
                    lines.add(s.replaceAll("FIXNEWLINES[ ]*", ""));
                }
            }

            event.setCanceled(true);
            GuiUtils.drawHoveringText(lines, event.getX(), event.getY(), event.getScreenWidth(), event.getScreenHeight(), event.getMaxWidth(), event.getFontRenderer());
        }
    }
}
