package com.fantasticsource.tools;

import net.minecraft.util.text.TextFormatting;

public class Smoothing
{
    public static final int
            LINEAR = 0,
            SINUOUS = 1;

    public static double interpolate(double start, double end, double normalizedProgress, int type)
    {
        switch (type)
        {
            case SINUOUS:
                return start + (end - start) * (1 - TrigLookupTable.TRIG_TABLE_1024.cos(Math.PI * normalizedProgress)) / 2;

            case LINEAR:
            default:
                return start + (end - start) * normalizedProgress;
        }
    }

    public static double balanceAlpha(int layers, double alpha)
    {
        return 1 - Math.pow(1 - alpha, 1d / layers);
    }


    public static void balanceAlphaTest()
    {
        for (double d : new double[]{.1, .2, .3, .4, .5, .6, .7, .8, .9, 1})
        {
            for (int i = 1; i < 11; i++)
            {
                double la = Smoothing.balanceAlpha(i, d);
                double da = 1 - Math.pow(1 - la, i);
                System.out.println(TextFormatting.AQUA + "" + i + ", " + d + ", " + la + ", " + da);
            }
        }
    }

    public static void interpolationTest(int type)
    {
        String result = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        for (double d = 0; d <= 1; d += 0.01)
        {
            if (1 - d < 0.002) d = 1;

            System.out.println(result.substring((int) (result.length() - result.length() * interpolate(0, 1, d, type))));
        }
    }
}
