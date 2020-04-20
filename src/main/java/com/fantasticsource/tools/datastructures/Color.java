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
        intValue = color;

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        hex = Integer.toHexString(intValue);
    }

    public Color(int color, boolean noAlpha)
    {
        if (noAlpha) color = (color << 8) & 255;

        intValue = color;

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        hex = Integer.toHexString(intValue);
    }


    public Color(int r, int g, int b, int a)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = min(max(a, 0), 255);

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);
    }

    public Color(int r, int g, int b)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = 255;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = 1;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);
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

    public Color(float r, float g, float b)
    {
        rf = min(max(r, 0), 1);
        gf = min(max(g, 0), 1);
        bf = min(max(b, 0), 1);
        af = 1;

        this.r = min(max((int) (rf * 255), 0), 255);
        this.g = min(max((int) (gf * 255), 0), 255);
        this.b = min(max((int) (bf * 255), 0), 255);
        this.a = 255;

        intValue = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;

        hex = Integer.toHexString(intValue);
    }


    public Color(String hex)
    {
        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;
    }

    public Color(String hex, boolean noAlpha)
    {
        if (noAlpha) hex = hex + "ff";

        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;
    }


    public Color copy()
    {
        return new Color(intValue);
    }


    public Color setR(int r)
    {
        this.r = r;
        rf = (float) r / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setRF(float rf)
    {
        this.rf = rf;
        this.r = (int) (rf * 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setG(int g)
    {
        this.g = g;
        gf = (float) g / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setGF(float gf)
    {
        this.gf = gf;
        this.g = (int) (gf * 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setB(int b)
    {
        this.b = b;
        bf = (float) b / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setBF(float bf)
    {
        this.bf = bf;
        this.b = (int) (bf * 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setA(int a)
    {
        this.a = a;
        af = (float) a / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setAF(float af)
    {
        this.af = af;
        this.a = (int) (af * 255);

        intValue = (r << 24) | (g << 16) | (b << 8) | a;
        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColorNoAlpha(int color)
    {
        r = (color >> 16) & 0xff;
        g = (color >> 8) & 0xff;
        b = color & 0xff;
        a = 255;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(int color)
    {
        intValue = color;

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(int r, int g, int b)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = 255;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = 1;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(int r, int g, int b, int a)
    {
        this.r = min(max(r, 0), 255);
        this.g = min(max(g, 0), 255);
        this.b = min(max(b, 0), 255);
        this.a = min(max(a, 0), 255);

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        intValue = (r << 24) | (g << 16) | (b << 8) | a;

        hex = Integer.toHexString(intValue);

        return this;
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

    public Color setColor(float r, float g, float b)
    {
        rf = min(max(r, 0), 1);
        gf = min(max(g, 0), 1);
        bf = min(max(b, 0), 1);
        af = 1;

        this.r = min(max((int) (rf * 255), 0), 255);
        this.g = min(max((int) (gf * 255), 0), 255);
        this.b = min(max((int) (bf * 255), 0), 255);
        this.a = 255;

        intValue = (this.r << 24) | (this.g << 16) | (this.b << 8) | this.a;

        hex = Integer.toHexString(intValue);

        return this;
    }

    public Color setColor(String hex)
    {
        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        return this;
    }

    public Color setColorNoAlpha(String hex)
    {
        hex = hex + "ff";
        this.hex = hex;

        intValue = Tools.parseHexInt(hex);

        r = (intValue >> 24) & 0xff;
        g = (intValue >> 16) & 0xff;
        b = (intValue >> 8) & 0xff;
        a = intValue & 0xff;

        rf = (float) r / 255;
        gf = (float) g / 255;
        bf = (float) b / 255;
        af = (float) a / 255;

        return this;
    }


    public int color()
    {
        return intValue;
    }

    public int toARGB()
    {
        return (intValue >> 8) | (a << 24);
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
        return (int) (255 * hf());
    }

    public float hf()
    {
        if (rf == gf && gf == bf) return 0;

        float cmin = Tools.min(rf, gf, bf), vf = vf();

        if (vf == rf) return Tools.posMod((gf - bf) / (vf - cmin) / 6, 1);
        if (vf == gf) return Tools.posMod((bf - rf) / (vf - cmin) / 6 + 1f / 3, 1);
        return Tools.posMod((rf - gf) / (vf - cmin) / 6 + 1f * 2 / 3, 1);
    }

    public int s()
    {
        return (int) (sf() * 255);
    }

    public float sf()
    {
        float vf = vf();
        return vf == 0 ? 0 : (vf - Tools.min(rf, gf, bf)) / vf;
    }

    public int v()
    {
        return Tools.max(r, g, b);
    }

    public float vf()
    {
        return Tools.max(rf, gf, bf);
    }

    public Color setH(int h)
    {
        return setHF((float) h / 255);
    }

    public Color setHF(float hf)
    {
        return setColorHSV(hf, sf(), vf(), af);
    }

    public Color setS(int s)
    {
        return setSF((float) s / 255);
    }

    public Color setSF(float sf)
    {
        return setColorHSV(hf(), sf, vf(), af);
    }

    public Color setV(int v)
    {
        return setVF((float) v / 255);
    }

    public Color setVF(float vf)
    {
        return setColorHSV(hf(), sf(), vf, af);
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
        if (hf < 0 || hf > 1 || sf < 0 || sf > 1 || vf < 0 || vf > 1 || af < 0 || af > 1) throw new IllegalArgumentException("Out of bounds (0, 1): h=" + hf + ", s=" + sf + ", v=" + vf + ", a=" + af);

        if (vf == 0) return setColor(0, 0, 0, af);
        if (sf == 0) return setColor(vf, vf, vf, af);

        if (hf == 1) hf = 0;

        hf *= 6;
        int hInt = (int) hf;
        float hFrac = hf - hInt;

        float p = vf * (1 - sf);
        float q = vf * (1 - (sf * hFrac));
        float t = vf * (1 - (sf * (1 - hFrac)));

        switch (hInt)
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
