package com.fantasticsource.fantasticlib.config;

import net.minecraftforge.common.config.Config;

import static com.fantasticsource.fantasticlib.FantasticLib.MODID;

public class GUIConfig
{
    @Config.Name("Focus Zoom on Mouse by Default")
    @Config.LangKey(MODID + ".config.zoomFocusMouse")
    @Config.Comment(
            {
                    "Whether zooming in/out focuses on the mouse position or not in a zoomable GUI view (if not, center of view is used instead)",
                    "Whichever mode is *NOT* the default can be accessed by holding the ctrl key while using zoom controls"
            })
    public boolean zoomFocusMouse = true;

    @Config.Name("Reset Zoom (Mouse Button)")
    @Config.LangKey(MODID + ".config.zoomResetButton")
    @Config.Comment(
            {
                    "Which mouse button resets the zoom level in a zoomable GUI view (click)",
            })
    @Config.RangeInt(min = 0)
    public int zoomResetButton = 2;

    @Config.Name("Pan (Mouse Button)")
    @Config.LangKey(MODID + ".config.panButton")
    @Config.Comment(
            {
                    "Which mouse button pans the view in a pannable GUI view (hold and drag)",
            })
    @Config.RangeInt(min = 0)
    public int panButton = 2;

    @Config.Name("Zoom Rate")
    @Config.LangKey(MODID + ".config.zoomRate")
    @Config.Comment(
            {
                    "How fast zoomable views zoom",
            })
    @Config.RangeDouble(min = 1.1, max = 4)
    public double zoomRate = 1.2;
}
