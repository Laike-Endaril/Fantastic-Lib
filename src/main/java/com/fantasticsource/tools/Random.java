package com.fantasticsource.tools;

public class Random
{
    public static final double ONE_THIRD = 1d / 3, EPSILON = 0.0000001;

    public long seed, rng_a, rng_b, rng_c, nextIteration;


    public Random()
    {
        randomize();
    }

    public Random(long seed)
    {
        setSeed(seed);
    }

    public Random(double seed)
    {
        setSeed(seed);
    }

    public Random(long seed, long rng_a, long rng_b, long rng_c, long nextIteration)
    {
        this.seed = seed;
        this.rng_a = rng_a;
        this.rng_b = rng_b;
        this.rng_c = rng_c;
        this.nextIteration = nextIteration;
    }


    public long getSeed()
    {
        return seed;
    }

    public void setSeed(long seed)
    {
        this.seed = seed;
        rng_a = seed;
        rng_b = seed;
        rng_c = seed;
        nextIteration = 1;
        for (int i = 0; i < 12; i++) nextLong();
    }

    public void setSeed(double seed)
    {
        setSeed(Double.doubleToRawLongBits(seed));
    }

    public void randomize()
    {
        setSeed(System.nanoTime());
    }


    public long nextLong()
    {
        long tmp = rng_a + rng_b + nextIteration++;
        rng_a = rng_b ^ (rng_b >> 12);
        rng_b = rng_c + (rng_c << 3);
        rng_c = ((rng_c << 25) | (rng_c >> (64 - 25))) + tmp;
        return tmp & Long.MAX_VALUE;
    }

    public long nextLong(long limit)
    {
        return (long) (nextDouble(limit));
    }


    public int nextInt()
    {
        return (int) nextLong();
    }

    public int nextInt(int limit)
    {
        return (int) (nextDouble(limit));
    }


    public double nextDouble()
    {
        return nextLong() / ((double) Long.MAX_VALUE + 1);
    }

    public double nextDouble(double limit)
    {
        return nextDouble() * limit;
    }


    public float nextFloat()
    {
        return (float) nextDouble();
    }

    public float nextFloat(float limit)
    {
        return (float) (nextDouble() * limit);
    }


    public double nextGaussian()
    {
        double v1, v2, s;
        do
        {
            v1 = 2 * nextDouble() - 1; // between -1 and 1
            v2 = 2 * nextDouble() - 1; // between -1 and 1
            s = v1 * v1 + v2 * v2;
        }
        while (s >= 1 || s == 0);
        return v1 * StrictMath.sqrt(-2 * StrictMath.log(s) / s);
    }


    public double getNextUniformRandZInFrustum(double nearW, double nearH, double farW, double farH, double depth)
    {
        double a = (farW - nearW) * (farH - nearH) / (depth * depth);
        double b = (nearH * (farW - nearW) + nearW * (farH - nearH)) / depth;

        if (Math.abs(a) < EPSILON)
        {
            if (Math.abs(b) < EPSILON)
            {
                //Rectangular prism
                return (nextDouble(depth));
            }

            //Trapezoidal prism
            double c = nearW * nearH;
            double area = depth * (c + depth * 0.5 * b);
            double r = nextDouble(area);
            return (-c + Math.sqrt(c * c + 2 * b * r)) / b;
        }

        //General case
        double c = nearW * nearH;
        double area = depth * (c + depth * (0.5 * b + depth * ONE_THIRD * a));
        double r = nextDouble(area);
        double det = b * b - 4 * a * c;
        double part1 = b * (b * b - 6 * a * c) - 12 * a * a * r;
        double part2 = Math.sqrt(part1 * part1 - det * det * det);
        if (part1 < 0.0) part2 = -part2;
        double part3 = part1 + part2;
        if (part3 < 0.0) part3 = -Math.pow(-part3, ONE_THIRD);
        else part3 = Math.pow(part3, ONE_THIRD);
        return -(b + det / part3 + part3) / (2 * a);
    }
}
