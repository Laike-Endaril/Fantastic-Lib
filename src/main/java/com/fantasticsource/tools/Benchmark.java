package com.fantasticsource.tools;

import javax.annotation.Nullable;

public class Benchmark
{
    public static void benchmark(int iterations, @Nullable String[] labels, Runnable... runnables)
    {
        long time;
        int i, i2 = 0;
        for (Runnable runnable : runnables)
        {
            i = 0;
            time = System.nanoTime();
            for (; i < iterations; i++) runnable.run();
            time = System.nanoTime() - time;
            System.out.println(((labels != null && i2 < labels.length) ? labels[i2++] + ": " : "") + time);
        }
    }
}
