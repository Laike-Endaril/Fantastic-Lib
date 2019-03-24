package com.fantasticsource.mctools;

import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.fml.client.config.GuiUtils;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;
import java.util.List;

public class TooltipFixer
{
    @SubscribeEvent
    public static void configTooltip(RenderTooltipEvent.Pre event)
    {
        List<String> lines = event.getLines();
        boolean fixNewlines = false, fillScreen = false;

        for (String string : lines)
        {
            if (string.contains("FIXNEWLINES")) fixNewlines = true;
            if (string.contains("FILLSCREEN")) fillScreen = true;
        }

        if (fixNewlines || fillScreen)
        {
            String[] oldLines = lines.toArray(new String[lines.size()]);
            lines = new ArrayList<>();

            if (fixNewlines)
            {
                for (String string : oldLines)
                {
                    for (String s : string.split("\\\\n"))
                    {
                        lines.add(s.replaceAll("FIXNEWLINES[ ]*", "").replaceAll("FILLSCREEN[ ]*", ""));
                    }
                }
            }
            else
            {
                for (String string : oldLines)
                {
                    lines.add(string.replaceAll("FILLSCREEN[ ]*", ""));
                }
            }

            event.setCanceled(true);

            if (fillScreen) GuiUtils.drawHoveringText(lines, 0, 0, event.getScreenWidth(), event.getScreenHeight(), Integer.MAX_VALUE, event.getFontRenderer());
            else GuiUtils.drawHoveringText(lines, event.getX(), event.getY(), event.getScreenWidth(), event.getScreenHeight(), event.getMaxWidth(), event.getFontRenderer());
        }
    }
}
