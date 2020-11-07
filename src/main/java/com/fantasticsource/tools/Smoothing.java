package com.fantasticsource.tools;

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

    public static void printTest(int type)
    {
        String result = "000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
        for (double d = 0; d <= 1; d += 0.01)
        {
            if (1 - d < 0.002) d = 1;

            System.out.println(result.substring((int) (result.length() - result.length() * interpolate(0, 1, d, type))));
        }
    }
}
