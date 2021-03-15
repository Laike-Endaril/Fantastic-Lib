package com.fantasticsource.mctools.aw;

import net.minecraft.entity.Entity;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

public abstract class SetRenderModeEvent extends EntityEvent
{
    public String renderModeChannel, renderMode;

    public SetRenderModeEvent(Entity entity, String renderModeChannel, String renderMode)
    {
        super(entity);
        this.renderModeChannel = renderModeChannel;
        this.renderMode = renderMode;
    }


    @Cancelable
    public static class Pre extends SetRenderModeEvent
    {
        public Pre(Entity entity, String renderModeChannel, String renderMode)
        {
            super(entity, renderModeChannel, renderMode);
        }
    }

    public static class Post extends SetRenderModeEvent
    {
        public Post(Entity entity, String renderModeChannel, String renderMode)
        {
            super(entity, renderModeChannel, renderMode);
        }
    }
}
