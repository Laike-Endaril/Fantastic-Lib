package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

import static com.fantasticsource.tools.Tools.max;
import static com.fantasticsource.tools.Tools.min;

public class Color
{
    public static final Color
            BLANK = new Color(0),
            BLACK = new Color(0xFF),
            WHITE = new Color(0xFFFFFFFF),
            RED = new Color(0xFF0000FF),
            GREEN = new Color(0x00FF00FF),
            BLUE = new Color(0x0000FFFF),
            YELLOW = new Color(0xFFFF00FF),
            AQUA = new Color(0x00FFFFFF),
            PURPLE = new Color(0xFF00FFFF),
            GRAY = new Color(0x777777FF);

    private int intValue, r, g, b, a;
    private float rf, gf, bf, af;
    private String hex;


    public Color(int color)
    {
        setColor(color);
    }

    public Color(int color, boolean noAlpha)
    {
        if (noAlpha) setColorNoAlpha(color);
        else setColor(color);
    }


    public Color(int r, int g, int b, int a)
    {
        setColor(r, g, b, a);
    }

    public Color(int r, int g, int b)
    {
        setColor(r, g, b);
    }


    public Color(float r, float g, float b, float a)
    {
        setColor(r, g, b, a);
    }

    public Color(float r, float g, float b)
    {
        setColor(r, g, b);
    }


    public Color(String hex)
    {
        setColor(hex);
    }

    public Color(String hex, boolean noAlpha)
    {
        if (noAlpha) setColorNoAlpha(hex);
        else setColor(hex);
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

    public Color setColorNoAlpha(int color)
    {
        return setColor((color << 8) | 0xff);
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

    public Color setColor(int r, int g, int b)
    {
        return setColor(r, g, b, 255);
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
        return setColor(r, g, b, 1);
    }

    public Color setColor(String hex)
    {
        this.hex = hex;

        intValue = Integer.parseInt(hex, 16);

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
        return setColor(hex + "ff");
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

    public String toString()
    {
        return hex();
    }

    public int v()
    {
        return (int) ((double) (r + g + b) / 3);
    }

    public float vf()
    {
        return (rf + gf + bf) / 3;
    }

    public Color setV(int v)
    {
        v = v & 0xff;

        if (v == 0) setColor(0, 0, 0, a);
        else if (v == 255) setColor(255, 255, 255, a);
        else
        {
            int current = v();

            int rr = (int) (((double) v / current) * r);
            int gg = (int) (((double) v / current) * g);
            int bb = (int) (((double) v / current) * b);

            if (rr >= 255)
            {
                if (gg >= 255)
                {
                    //1 or 0 non-max
                    bb = Tools.min(255, rr + gg + bb - 510);
                    rr = 255;
                    gg = 255;
                }
                else if (bb >= 255)
                {
                    //1 or 0 non-max
                    gg = Tools.min(255, rr + gg + bb - 510);
                    rr = 255;
                    bb = 255;
                }
                else
                {
                    //2 non-max
                    int overflow = rr - 255;
                    rr = 255;

                    if (gg > bb)
                    {
                        double ratio = (double) gg / (bb + gg);
                        int inc = (int) (ratio * overflow);
                        gg += inc;
                        if (gg > 255)
                        {
                            overflow += gg - 255;
                            gg = 255;
                        }
                        bb = Tools.min(255, bb + overflow - inc);
                    }
                    else
                    {
                        double ratio = (gg + bb == 0) ? 0.5 : (double) bb / (bb + gg);
                        int inc = (int) (ratio * overflow);
                        bb += inc;
                        if (bb > 255)
                        {
                            overflow += bb - 255;
                            bb = 255;
                        }
                        gg = Tools.min(255, gg + overflow - inc);
                    }
                }
            }
            else
            {
                if (gg >= 255)
                {
                    if (bb >= 255)
                    {
                        //1 or 0 non-max
                        rr = Tools.min(255, rr + gg + bb - 510);
                        gg = 255;
                        bb = 255;
                    }
                    else
                    {
                        //2 non-max
                        int overflow = gg - 255;
                        gg = 255;

                        if (rr > bb)
                        {
                            double ratio = (double) rr / (bb + rr);
                            int inc = (int) (ratio * overflow);
                            rr += inc;
                            if (rr > 255)
                            {
                                overflow += rr - 255;
                                rr = 255;
                            }
                            bb = Tools.min(255, bb + overflow - inc);
                        }
                        else
                        {
                            double ratio = (rr + bb == 0) ? 0.5 : (double) bb / (bb + rr);
                            int inc = (int) (ratio * overflow);
                            bb += inc;
                            if (bb > 255)
                            {
                                overflow += bb - 255;
                                bb = 255;
                            }
                            rr = Tools.min(255, rr + overflow - inc);
                        }
                    }
                }
                else if (bb >= 255)
                {
                    //2 non-max
                    int overflow = bb - 255;
                    bb = 255;

                    if (rr > gg)
                    {
                        double ratio = (double) rr / (gg + rr);
                        int inc = (int) (ratio * overflow);
                        rr += inc;
                        if (rr > 255)
                        {
                            overflow += rr - 255;
                            rr = 255;
                        }
                        gg = Tools.min(255, gg + overflow - inc);
                    }
                    else
                    {
                        double ratio = (gg + rr == 0) ? 0.5 : (double) gg / (gg + rr);
                        int inc = (int) (ratio * overflow);
                        gg += inc;
                        if (gg > 255)
                        {
                            overflow += gg - 255;
                            gg = 255;
                        }
                        rr = Tools.min(255, rr + overflow - inc);
                    }
                }
            }

            setR(rr);
            setG(gg);
            setB(bb);
        }

        return this;
    }

    public Color setVF(float vf)
    {
        return setV((int) (vf * 255));
    }
}
