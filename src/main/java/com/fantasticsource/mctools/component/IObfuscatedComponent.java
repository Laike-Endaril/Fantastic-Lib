package com.fantasticsource.mctools.component;

import io.netty.buffer.ByteBuf;

public interface IObfuscatedComponent
{
    void writeObf(ByteBuf buf);

    void readObf(ByteBuf buf);
}
