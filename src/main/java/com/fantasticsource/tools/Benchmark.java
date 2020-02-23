package com.fantasticsource.tools;

import com.fantasticsource.tools.datastructures.Pair;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class Benchmark
{
    public static void benchmark(int iterations, Pair<String, Runnable>... labeledEntries)
    {
        long iTime, totalTime = 0;
        int i, i2 = 0;
        long[] times = new long[labeledEntries.length];
        for (Pair<String, Runnable> entry : labeledEntries)
        {
            i = 0;
            iTime = System.nanoTime();
            for (; i < iterations; i++) entry.getValue().run();
            iTime = System.nanoTime() - iTime;
            times[i2++] = iTime;
            totalTime += iTime;
        }

        NumberFormat timeFormat = NumberFormat.getInstance(), percentFormat = DecimalFormat.getInstance();
        String label, timeString, percent;
        i2 = 0;
        for (long time : times)
        {
            label = labeledEntries[i2++].getKey() + ": ";
            timeString = timeFormat.format(time) + " nanos";
            percent = percentFormat.format(((double) time / totalTime) * 100);
            System.out.println(label + timeString + " (" + percent + "%)");
        }
    }
}
