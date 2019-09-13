package com.fantasticsource.mctools.component;

import io.netty.buffer.ByteBuf;

public interface IObfuscatedComponent
{
    Component writeObf(ByteBuf buf);

    Component readObf(ByteBuf buf);
}
