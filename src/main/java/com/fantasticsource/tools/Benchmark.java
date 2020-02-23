package com.fantasticsource.tools;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Benchmark
{
    public static void benchmark(int iterations, @Nullable String[] labels, Runnable... runnables)
    {
        long iTime, totalTime = 0;
        int i, i2 = 0;
        long[] times = new long[runnables.length];
        for (Runnable runnable : runnables)
        {
            i = 0;
            iTime = System.nanoTime();
            for (; i < iterations; i++) runnable.run();
            iTime = System.nanoTime() - iTime;
            times[i2++] = iTime;
            totalTime += iTime;
        }

        NumberFormat timeFormat = NumberFormat.getInstance(), percentFormat = DecimalFormat.getInstance();
        String label, timeString, percent;
        i2 = 0;
        for (long time : times)
        {
            label = labels != null && i2 < labels.length ? labels[i2++] + ": " : "";
            timeString = timeFormat.format(time) + " nanos";
            percent = percentFormat.format((double) (time / totalTime) * 100);
            System.out.println(label + timeString + " (" + percent + "%)");
        }
    }
}
