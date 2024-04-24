package com.fantasticsource.mctools;

import net.minecraft.client.settings.KeyBinding;

public class KeybindHandler
{
    private final KeyBinding keyBinding;
    private boolean wasActive = false;

    public KeybindHandler(KeyBinding keyBinding)
    {
        this.keyBinding = keyBinding;
    }

    public boolean wasJustPressed()
    {
        boolean isActive = keyBinding.isKeyDown();

        if (wasActive)
        {
            wasActive = isActive;
            return false;
        }

        wasActive = isActive;
        return isActive;
    }

    public boolean isActive()
    {
        return keyBinding.isKeyDown();
    }
}
