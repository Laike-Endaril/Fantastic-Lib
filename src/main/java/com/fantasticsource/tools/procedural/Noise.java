package com.fantasticsource.tools.procedural;

import com.fantasticsource.tools.Random;

public class Noise
{
    public static int[] permutations = genPermutations(new Random().nextLong());

    public static double brownian(double x, double y, double amplitude, double amplitudeGain, double frequency, double frequencyGain, int octaves, int... permutations)
    {
        if (permutations.length == 0) permutations = Noise.permutations;
        else if (permutations.length != 512) throw new IllegalArgumentException("Permutations array must be 512 ints");


        double result = 0;

        for (int i = 0; i < octaves; i++)
        {
            result += simplex(x * frequency, y * frequency, permutations) * amplitude;
            amplitude *= amplitudeGain;
            frequency *= frequencyGain;
        }

        return result;
    }

    public static double simplex(double x, double y, int... permutations)
    {
        if (permutations.length == 0) permutations = Noise.permutations;
        else if (permutations.length != 512) throw new IllegalArgumentException("Permutations array must be 512 ints");


        double s = (x + y) * 0.366025403;
        int i = (int) (x + s);
        int j = (int) (y + s);

        double t = (i + j) * 0.211324865;
        double x0 = x - (i - t);
        double y0 = y - (j - t);

        int i1, j1;
        if (x0 > y0)
        {
            i1 = 1;
            j1 = 0;
        }
        else
        {
            i1 = 0;
            j1 = 1;
        }

        double x1 = x0 - i1 + 0.211324865;
        double y1 = y0 - j1 + 0.211324865;
        double x2 = x0 - 1.42264973;
        double y2 = y0 - 1.42264973;

        int ii = i & 0xFF;
        int jj = j & 0xFF;

        double t0 = 0.5 - x0 * x0 - y0 * y0;
        double n0, n1, n2;
        if (t0 < 0) n0 = 0;
        else
        {
            t0 *= t0;
            n0 = t0 * t0 * gradient(permutations[ii + permutations[jj]], x0, y0);
        }

        double t1 = 0.5 - x1 * x1 - y1 * y1;
        if (t1 < 0) n1 = 0;
        else
        {
            t1 *= t1;
            n1 = t1 * t1 * gradient(permutations[ii + i1 + permutations[jj + j1]], x1, y1);
        }

        double t2 = 0.5 - x2 * x2 - y2 * y2;
        if (t2 < 0) n2 = 0;
        else
        {
            t2 *= t2;
            n2 = t2 * t2 * gradient(permutations[ii + 1 + permutations[jj + 1]], x2, y2);
        }

        return 40 * (n0 + n1 + n2);
    }

    public static double gradient(int i, double x, double y)
    {
        int lowBits = i & 7;

        double a, b;
        if (lowBits < 4)
        {
            if ((lowBits & 1) != 0) a = -x;
            else a = x;
            if ((lowBits & 2) != 0) b = -2 * y;
            else b = 2 * y;
        }
        else
        {
            if ((lowBits & 1) != 0) a = -y;
            else a = y;
            if ((lowBits & 2) != 0) b = -2 * x;
            else b = 2 * x;
        }

        return a + b;
    }


    public static int[] genPermutations(long seed)
    {
        int[] result = new int[512];
        Random random = new Random(seed);
        for (int i = 0; i < 512; i++) result[i] = random.nextInt(256);
        return result;
    }

    public static void setPermutations(int... permutations)
    {
        Noise.permutations = permutations;
    }

    public static void genAndSetPermutations(long seed)
    {
        setPermutations(genPermutations(seed));
    }
}
