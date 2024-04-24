package com.fantasticsource.mctools;

public class KeybindHandler
{
    private boolean wasActive = false;

    public boolean wasJustPressed(boolean isActive)
    {
        if (wasActive)
        {
            wasActive = isActive;
            return false;
        }

        wasActive = isActive;
        return isActive;
    }
}
