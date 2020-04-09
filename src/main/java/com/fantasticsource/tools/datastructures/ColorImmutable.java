package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

public class ColorImmutable extends Color
{
    static
    {
        Color.BLANK = new ColorImmutable(0);
        Color.BLACK = new ColorImmutable(0xFF);
        Color.WHITE = new ColorImmutable(0xFFFFFFFF);
        Color.RED = new ColorImmutable(0xFF0000FF);
        Color.GREEN = new ColorImmutable(0x00FF00FF);
        Color.BLUE = new ColorImmutable(0x0000FFFF);
        Color.YELLOW = new ColorImmutable(0xFFFF00FF);
        Color.AQUA = new ColorImmutable(0x00FFFFFF);
        Color.PURPLE = new ColorImmutable(0xFF00FFFF);
        Color.GRAY = new ColorImmutable(0x777777FF);
        Color.ORANGE = new ColorImmutable(0xFF7700FF);
    }

    public ColorImmutable(int color)
    {
        super(color);
    }

    public ColorImmutable(int color, boolean noAlpha)
    {
        super(color, noAlpha);
    }


    public ColorImmutable(int r, int g, int b, int a)
    {
        super(0);
        super.setColor(r, g, b, a);
    }

    public ColorImmutable(int r, int g, int b)
    {
        super(0);
        super.setColor(r, g, b);
    }


    public ColorImmutable(float r, float g, float b, float a)
    {
        super(0);
        super.setColor(r, g, b, a);
    }

    public ColorImmutable(float r, float g, float b)
    {
        super(0);
        super.setColor(r, g, b);
    }


    public ColorImmutable(String hex)
    {
        super(0);
        super.setColor(hex);
    }

    public ColorImmutable(String hex, boolean noAlpha)
    {
        super(0);
        if (noAlpha) super.setColorNoAlpha(hex);
        else super.setColor(hex);
    }


    public static void init()
    {
        //Indirectly initializes this class, as well as the constants in the Color class
    }


    public ColorImmutable setR(int r)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setRF(float rf)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setG(int g)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setGF(float gf)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setB(int b)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setBF(float bf)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setA(int a)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setAF(float af)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColor(int color)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColorNoAlpha(int color)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColor(int r, int g, int b, int a)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColor(int r, int g, int b)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColor(float r, float g, float b, float a)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColor(float r, float g, float b)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColor(String hex)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColorNoAlpha(String hex)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }


    public ColorImmutable setH(int h)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setHF(float hf)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setV(int v)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setVF(float vf)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColorHSV(int h, int s, int v)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColorHSV(int h, int s, int v, int a)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColorHSV(float hf, float sf, float vf)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }

    public ColorImmutable setColorHSV(float hf, float sf, float vf, float af)
    {
        Tools.printStackTrace();
        throw new IllegalStateException("Cannot do this with an immutable color!");
    }
}
