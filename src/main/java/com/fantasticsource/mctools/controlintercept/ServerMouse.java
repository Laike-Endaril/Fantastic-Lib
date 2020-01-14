package com.fantasticsource.mctools.controlintercept;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class ServerMouse
{
    private static final int BUFFER_SIZE = 50;
    public static final int EVENT_SIZE = 1 + 1 + 4 + 4 + 4 + 8;
    private static String[] buttonNames = new String[16];
    private static final Map<String, Integer> buttonMap = new HashMap<>(16);

    private static ByteBuffer buttonStates;
    private static int x, y;
    private static int absoluteX, absoluteY;
    private static IntBuffer coordBuffer;
    private static int dx;
    private static int dy;
    private static int dwheel;
    private static ByteBuffer readBuffer;
    private static int eventButton;
    private static boolean eventState;
    private static int eventDX;
    private static int eventDY;
    private static int eventDWheel;
    private static int eventX;
    private static int eventY;
    private static long claimedClientEventNanos, serverNanos;
    private static int grabX;
    private static int grabY;
    private static int lastEventRawX;
    private static int lastEventRawY;
    private static boolean isGrabbed;

    private ServerMouse()
    {
    }

    static
    {
        // Assign names to all the buttons
        for (int i = 0; i < 16; i++)
        {
            buttonNames[i] = "BUTTON" + i;
            buttonMap.put(buttonNames[i], i);
        }

        // set mouse buttons
        buttonStates = BufferUtils.createByteBuffer(16);
        coordBuffer = BufferUtils.createIntBuffer(3);
        readBuffer = ByteBuffer.allocate(EVENT_SIZE * BUFFER_SIZE);
        readBuffer.limit(0);
    }

    private static void resetMouse()
    {
        dx = dy = dwheel = 0;
        readBuffer.position(readBuffer.limit());
    }

    /**
     * See if a particular mouse button is down.
     *
     * @param button The index of the button you wish to test (0..getButtonCount-1)
     * @return true if the specified button is down
     */
    public static boolean isButtonDown(int button)
    {
        if (button >= 16 || button < 0) return false;
        return buttonStates.get(button) == 1;
    }

    /**
     * Gets a button's name
     *
     * @param button The button
     * @return a String with the button's human readable name in it or null if the button is unnamed
     */
    public static String getButtonName(int button)
    {
        if (button >= 16 || button < 0) return null;
        return buttonNames[button];
    }

    /**
     * Get's a button's index. If the button is unrecognised then -1 is returned.
     *
     * @param buttonName The button name
     */
    public static int getButtonIndex(String buttonName)
    {
        Integer ret = buttonMap.get(buttonName);
        if (ret == null) return -1;
        return ret;
    }

    /**
     * @return Current events button. Returns -1 if no button state was changed
     */
    public static int getEventButton()
    {
        return eventButton;
    }

    /**
     * Get the current events button state.
     *
     * @return Current events button state.
     */
    public static boolean getEventButtonState()
    {
        return eventState;
    }

    /**
     * @return Current events delta x.
     */
    public static int getEventDX()
    {
        return eventDX;
    }

    /**
     * @return Current events delta y.
     */
    public static int getEventDY()
    {
        return eventDY;
    }

    /**
     * @return Current events absolute x.
     */
    public static int getEventX()
    {
        return eventX;
    }

    /**
     * @return Current events absolute y.
     */
    public static int getEventY()
    {
        return eventY;
    }

    /**
     * @return Current events delta z
     */
    public static int getEventDWheel()
    {
        return eventDWheel;
    }

    /**
     * Gets the send time in nanoseconds of the current event, as claimed by the client.
     * Only useful for relative comparisons with other
     * Mouse events, as the absolute time has no defined
     * origin.
     *
     * @return The time in nanoseconds of the current event
     */
    public static long getClaimedClientEventNanoseconds()
    {
        return claimedClientEventNanos;
    }

    /**
     * Gets the reception time in nanoseconds of the current event, as calculated by the server
     * Only useful for relative comparisons with other
     * Mouse events, as the absolute time has no defined
     * origin.
     *
     * @return The time in nanoseconds of the current event
     */
    public static long getServerEventNanoseconds()
    {
        return serverNanos;
    }

    /**
     * Retrieves the absolute position. It will be clamped to
     * 0...width-1.
     *
     * @return Absolute x axis position of mouse
     */
    public static int getX()
    {
        return x;
    }

    /**
     * Retrieves the absolute position. It will be clamped to
     * 0...height-1.
     *
     * @return Absolute y axis position of mouse
     */
    public static int getY()
    {
        return y;
    }

    /**
     * @return Movement on the x axis since last time getDX() was called.
     */
    public static int getDX()
    {
        int result = dx;
        dx = 0;
        return result;
    }

    /**
     * @return Movement on the y axis since last time getDY() was called.
     */
    public static int getDY()
    {
        int result = dy;
        dy = 0;
        return result;
    }

    /**
     * @return Movement of the wheel since last time getDWheel() was called
     */
    public static int getDWheel()
    {
        int result = dwheel;
        dwheel = 0;
        return result;
    }

    /**
     * @return whether or not the mouse has grabbed the cursor
     */
    public static boolean isGrabbed()
    {
        return isGrabbed;
    }
}
