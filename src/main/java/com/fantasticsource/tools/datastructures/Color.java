package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

import static com.fantasticsource.tools.Tools.max;
import static com.fantasticsource.tools.Tools.min;

public class Color
{
    public static ColorImmutable
            BLANK,
            BLACK,
            WHITE,
            RED,
            GREEN,
            BLUE,
            YELLOW,
            AQUA,
            PURPLE,
            GRAY,
            ORANGE;

    protected int intValue, r, g, b, a;
    protected float rf, gf, bf, af;
    protected String hex;


    public Color(int color)
    {
        this(color, false);
    }

    public Color(int color, boolean noAlpha)
    {
        if (noAlpha) color = (color << 8) | 255;

        intValue = color;

        r = (intValue >>> 24) & 0xff;
        g = (intValue >>> 16) & 0xff;
        b = (intValue >>> 8) & 0xff;
        a = intValue & 0xff;

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = min(max((float) a / 255, 0), 1);

        hex = Integer.toHexString(intValue);
    }


    public Color(int r, int g, int b)
    {
        this(r, g, b, 255);
    }

    public Color(int r, int g, int b, int a)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = min(max(a, 0), 255);

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = min(max((float) a / 255, 0), 1);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);
    }


    public Color(float r, float g, float b)
    {
        this(r, g, b, 1);
    }

    public Color(float r, float g, float b, float a)
    {
        rf = min(max(r, 0), 1);
        gf = min(max(g, 0), 1);
        bf = min(max(b, 0), 1);
        af = min(max(a, 0), 1);

        this.r = min(max((int) (rf * 255), 0), 255);
        this.g = min(max((int) (gf * 255), 0), 255);
        this.b = min(max((int) (bf * 255), 0), 255);
        this.a = min(max((int) (af * 255), 0), 255);

        intValue = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;

        hex = Integer.toHexString(intValue);
    }


    public Color(String hex)
    {
        this(hex, false);
    }

    public Color(String hex, boolean noAlpha)
    {
        if (noAlpha) hex = hex + "ff";

        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >>> 24) & 0xff;
        g = (intValue >>> 16) & 0xff;
        b = (intValue >>> 8) & 0xff;
        a = intValue & 0xff;

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = min(max((float) a / 255, 0), 1);
    }


    public Color copy()
    {
        return new Color(intValue);
    }


    public Color setR(int r)
    {
        this.r = min(max(r, 0), 255);
        rf = min(max((float) r / 255, 0), 1);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setRF(float rf)
    {
        this.rf = min(max(rf, 0), 1);
        r = min(max((int) (rf * 255), 0), 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setG(int g)
    {
        this.g = min(max(g, 0), 255);
        gf = min(max((float) g / 255, 0), 1);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setGF(float gf)
    {
        this.gf = min(max(gf, 0), 1);
        g = min(max((int) (gf * 255), 0), 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setB(int b)
    {
        this.b = min(max(b, 0), 255);
        bf = min(max((float) b / 255, 0), 1);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setBF(float bf)
    {
        this.bf = min(max(bf, 0), 1);
        b = min(max((int) (bf * 255), 0), 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setA(int a)
    {
        this.a = min(max(a, 0), 255);
        af = min(max((float) a / 255, 0), 1);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setAF(float af)
    {
        this.af = min(max(af, 0), 1);
        a = min(max((int) (af * 255), 0), 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColorNoAlpha(int color)
    {
        r = (color >>> 16) & 0xff;
        g = (color >>> 8) & 0xff;
        b = color & 0xff;
        a = 255;

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = 1;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(int color)
    {
        intValue = color;

        r = (intValue >>> 24) & 0xff;
        g = (intValue >>> 16) & 0xff;
        b = (intValue >>> 8) & 0xff;
        a = intValue & 0xff;

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = min(max((float) a / 255, 0), 1);

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(int r, int g, int b)
    {
        return setColor(r, g, b, 255);
    }

    public Color setColor(int r, int g, int b, int a)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = min(max(a, 0), 255);

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = min(max((float) a / 255, 0), 1);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(float r, float g, float b)
    {
        return setColor(r, g, b, 1);
    }

    public Color setColor(float r, float g, float b, float a)
    {
        rf = min(max(r, 0), 1);
        gf = min(max(g, 0), 1);
        bf = min(max(b, 0), 1);
        af = min(max(a, 0), 1);

        this.r = min(max((int) (rf * 255), 0), 255);
        this.g = min(max((int) (gf * 255), 0), 255);
        this.b = min(max((int) (bf * 255), 0), 255);
        this.a = min(max((int) (af * 255), 0), 255);

        intValue = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(String hex)
    {
        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >>> 24) & 0xff;
        g = (intValue >>> 16) & 0xff;
        b = (intValue >>> 8) & 0xff;
        a = intValue & 0xff;

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = min(max((float) a / 255, 0), 1);

        return this;
    }

    public Color setColorNoAlpha(String hex)
    {
        hex = hex + "ff";
        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >>> 24) & 0xff;
        g = (intValue >>> 16) & 0xff;
        b = (intValue >>> 8) & 0xff;
        a = 255;

        rf = min(max((float) r / 255, 0), 1);
        gf = min(max((float) g / 255, 0), 1);
        bf = min(max((float) b / 255, 0), 1);
        af = 1;

        return this;
    }


    public int color()
    {
        return intValue;
    }

    public int toARGB()
    {
        return (intValue >>> 8) | (a << 24);
    }

    public int r()
    {
        return r;
    }

    public int g()
    {
        return g;
    }

    public int b()
    {
        return b;
    }

    public int a()
    {
        return a;
    }

    public float rf()
    {
        return rf;
    }

    public float gf()
    {
        return gf;
    }

    public float bf()
    {
        return bf;
    }

    public float af()
    {
        return af;
    }

    public String hex()
    {
        return hex;
    }

    public String hex8()
    {
        StringBuilder result = new StringBuilder();
        for (int i = hex.length(); i < 8; i++) result.append("0");
        return result + hex;
    }

    public String toString()
    {
        return hex();
    }

    public int h()
    {
        return min(max((int) (255 * hf()), 0), 255);
    }

    public float hf()
    {
        if (rf == gf && gf == bf) return 0;

        float minF = min(rf, gf, bf), maxF = vf();

        float result;
        if (maxF == rf) result = Tools.posMod((gf - bf) / (maxF - minF) / 6, 1);
        else if (maxF == gf) result = Tools.posMod((bf - rf) / (maxF - minF) / 6 + 1f / 3, 1);
        else result = Tools.posMod((rf - gf) / (maxF - minF) / 6 + 1f * 2 / 3, 1);

        return min(max(result, 0), 1);
    }

    public int s()
    {
        return min(max((int) (sf() * 255), 0), 255);
    }

    public float sf()
    {
        float result = vf();
        if (result <= 0) return 0;


        if (result >= 1) result = 1 - min(rf, gf, bf);
        else result = (result - min(rf, gf, bf)) / result;

        return min(max(result, 0), 1);
    }

    public int v()
    {
        return max(r, g, b);
    }

    public float vf()
    {
        return max(rf, gf, bf);
    }

    public Color setH(int h)
    {
        return setHF((float) h / 255);
    }

    public Color setHF(float hf)
    {
        return setColorHSV(min(max(hf, 0), 1), sf(), vf(), af);
    }

    public Color setS(int s)
    {
        return setSF((float) s / 255);
    }

    public Color setSF(float sf)
    {
        return setColorHSV(hf(), min(max(sf, 0), 1), vf(), af);
    }

    public Color setV(int v)
    {
        return setVF((float) v / 255);
    }

    public Color setVF(float vf)
    {
        return setColorHSV(hf(), sf(), min(max(vf, 0), 1), af);
    }

    public Color setColorHSV(int h, int s, int v)
    {
        return setColorHSV(h, s, v, 255);
    }

    public Color setColorHSV(int h, int s, int v, int a)
    {
        return setColorHSV((float) h / 255, (float) s / 255, (float) v / 255, (float) a / 255);
    }

    public Color setColorHSV(float hf, float sf, float vf)
    {
        return setColorHSV(hf, sf, vf, 1f);
    }

    public Color setColorHSV(float hf, float sf, float vf, float af)
    {
        hf = min(max(hf, 0), 1);
        sf = min(max(sf, 0), 1);
        vf = min(max(vf, 0), 1);
        af = min(max(af, 0), 1);

        if (vf == 0) return setColor(0, 0, 0, af);
        if (sf == 0) return setColor(vf, vf, vf, af);


        hf = Tools.posMod(hf * 6, 6);
        int hSixth = (int) hf;
        float hFrac = hf - hSixth;

        float p = vf * (1 - sf);
        float q = vf * (1 - (sf * hFrac));
        float t = vf * (1 - (sf * (1 - hFrac)));

        switch (hSixth)
        {
            case 0:
                return setColor(vf, t, p, af);

            case 1:
                return setColor(q, vf, p, af);

            case 2:
                return setColor(p, vf, t, af);

            case 3:
                return setColor(p, q, vf, af);

            case 4:
                return setColor(t, p, vf, af);

            case 5:
                return setColor(vf, p, q, af);

            default:
                throw new IllegalStateException("This should never happen");
        }
    }

    @Override
    public boolean equals(Object obj)
    {
        return obj instanceof Color && ((Color) obj).intValue == intValue;
    }

    @Override
    public int hashCode()
    {
        return intValue;
    }
}
