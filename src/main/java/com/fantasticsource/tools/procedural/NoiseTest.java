package com.fantasticsource.tools.procedural;

import com.fantasticsource.tools.Random;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;

import static org.lwjgl.opengl.GL11.*;

public class NoiseTest
{
    public static void main(String... args)
    {
        if (args.length == 0) args = new String[]{"ruffled alien"};

        int w = 0, h = 0;
        try
        {
            Display.create();
            glMatrixMode(GL_MODELVIEW);
            glPushMatrix();
            glLoadIdentity();
            glMatrixMode(GL_PROJECTION);
            glPushMatrix();
            glLoadIdentity();
            w = Display.getWidth();
            h = Display.getHeight();
            glViewport(0, 0, w, h);
            glOrtho(0, w, h, 0, -1, 1);
        }
        catch (LWJGLException e)
        {
            e.printStackTrace();
        }

        long seed;
        try
        {
            seed = Long.parseLong(args[1]);
        }
        catch (Exception e)
        {
            seed = new Random().nextLong();
        }
        int[] permutations = Noise.genPermutations(seed);


        String computeMode = "raw";
        double amplitude = 1, amplitudeGain = 1, frequency = 1, frequencyGain = 1;
        int octaves = 1;
        switch (args[0].toLowerCase())
        {
            case "raw 1":
                break;

            case "raw 2":
                frequency = 2;
                break;

            case "raw 3":
                frequency = 0.25;
                break;

            case "raw 4":
                frequency = 0.023;
                break;

            case "raw 5":
                octaves = 2;
                amplitudeGain = 0.6;
                frequency = 0.23;
                frequencyGain = 0.1;
                break;

            case "raw clouds 1":
                amplitude = 0.75;
                frequency = 0.002;
                amplitudeGain = 0.04;
                frequencyGain = 5;
                octaves = 5;
                break;

            case "raw clouds 2":
                amplitude = 0.75;
                frequency = 0.002;
                amplitudeGain = 0.04;
                frequencyGain = 5;
                octaves = 5;
                break;

            case "islands 1":
                octaves = 2;
                amplitudeGain = 1.1;
                frequency = 0.002;
                frequencyGain = 0.2;
                break;

            case "fabric 1":
                octaves = 2;
                amplitudeGain = 0.6;
                frequency = 1.69;
                frequencyGain = 1.3;
                break;


            case "heightmap 1":
                computeMode = "heightmap";
                octaves = 3;
                amplitudeGain = 15;
                frequency = 4;
                frequencyGain = 2;
                break;

            case "mountains 1":
                computeMode = "heightmap";
                amplitudeGain = 15;
                frequency = 0.005;
                break;

            case "curves and points":
                computeMode = "heightmap";
                amplitude = 15;
                frequency = 0.001;
                break;


            case "mountains 2":
                computeMode = "heightmap 2";
                amplitude = 2.0735999999;
                frequency = 0.0080375515;
                break;

            case "thick spikes":
                computeMode = "heightmap 2";
                frequency = 0.1238347294;
                break;

            case "thin spikes":
                computeMode = "heightmap 2";
                frequency = 0.8874444496;
                break;

            case "tiny spikes":
                computeMode = "heightmap 2";
                frequency = 0.6162808678;
                break;


            case "volcano tunnels 1":
                computeMode = "volcano tunnels";
                frequency = 0.01;
                amplitude = 100;
                break;

            case "volcano tunnels 2":
                computeMode = "volcano tunnels";
                amplitude = 110;
                frequency = 0.001;
                break;

            case "volcano tunnels 3":
                computeMode = "volcano tunnels";
                octaves = 2;
                amplitude = 110;
                amplitudeGain = 110;
                frequency = 0.02;
                frequencyGain = 2;
                break;

            case "volcano tunnels 4":
                computeMode = "volcano tunnels";
                octaves = 3;
                amplitude = 110;
                amplitudeGain = 110;
                frequency = 0.005;
                frequencyGain = 0.5;
                break;

            case "volcano tunnels 5":
                computeMode = "volcano tunnels";
                octaves = 3;
                amplitude = 110;
                amplitudeGain = 110;
                frequency = 0.01;
                frequencyGain = 0.1;
                break;

            case "volcano tunnels 6":
                computeMode = "volcano tunnels";
                octaves = 3;
                amplitude = 110;
                amplitudeGain = 110;
                frequency = 0.002;
                frequencyGain = 0.1;
                break;

            case "volcano tunnels 7":
                computeMode = "volcano tunnels";
                amplitude = 100;
                frequency = 0.002;
                break;


            case "tunnels 1":
                computeMode = "tunnels";
                amplitude = 10;
                frequency = 0.022;
                break;

            case "tunnels 2":
                computeMode = "tunnels";
                octaves = 2;
                amplitude = 2.5;
                amplitudeGain = 2.5;
                frequency = 0.011;
                frequencyGain = 1.1;
                break;

            case "tunnels 3":
                computeMode = "tunnels";
                octaves = 3;
                amplitude = 1.5;
                amplitudeGain = 1.5;
                frequency = 0.008;
                frequencyGain = 2;
                break;


            case "warped 1":
                computeMode = "warped";
                amplitude = 10;
                frequency = 0.022;
                break;


            case "fan":
                computeMode = "fan";
                amplitude = 2;
                frequency = 0.022;
                break;


            case "circuit board 1":
                computeMode = "circuit board";
                break;


            case "random mesh 1":
                computeMode = "random mesh";
                break;


            case "edge stretch 1":
                computeMode = "edge stretch";
                frequency = 0.0001;
                break;


            case "cross hash 1a":
                computeMode = "cross hash 1";
                frequency = 0.00001;
                break;

            case "cross hash 1b":
                computeMode = "cross hash 1";
                frequency = 10000;
                break;


            case "cross hash 2":
                computeMode = "cross hash 2";
                frequency = 0.00001;
                break;


            case "clouds 1":
                computeMode = "clouds";
                frequency = 0.001;
                octaves = 6;
                frequencyGain = 2;
                amplitudeGain = 0.5;
                break;


            case "valleys 1":
                computeMode = "valleys";
                frequency = 0.001;
                octaves = 6;
                frequencyGain = 2;
                amplitudeGain = 0.5;
                break;


            case "ridges 1":
                computeMode = "ridges";
                frequency = 0.001;
                octaves = 6;
                frequencyGain = 2;
                amplitudeGain = 0.5;
                break;


            case "land and sea 1":
                computeMode = "land and sea";
                amplitude = 0.5;
                frequency = 0.001;
                octaves = 6;
                frequencyGain = 2;
                amplitudeGain = 0.5;
                break;


            case "water damage":
                computeMode = "water damage";
                amplitude = 0.5;
                frequency = 0.001;
                octaves = 2;
                amplitudeGain = 0.5;
                frequencyGain = 2;
                break;


            case "alien 1":
                computeMode = "organic 1";
                amplitude = 0.5;
                frequency = 0.01;
                break;

            case "alien 2":
                computeMode = "organic 1";
                amplitude = 0.5;
                frequency = 0.01;
                octaves = 2;
                amplitudeGain = 0.5;
                frequencyGain = 2;
                break;

            case "lichen":
                computeMode = "organic 1";
                amplitude = 0.5;
                frequency = 0.01;
                octaves = 4;
                amplitudeGain = 0.5;
                frequencyGain = 2;
                break;


            case "ruffled alien":
                computeMode = "ruffled organic";
                amplitude = 0.5;
                frequency = 0.01;
                break;
        }


        double terrainHeight;
        while (!Display.isCloseRequested())
        {
            switch (computeMode.toLowerCase())
            {
                case "raw":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "heightmap":
                    glBegin(GL_LINES);
                    for (int x = 0; x < w; x++)
                    {
                        terrainHeight = 0;
                        for (int y = 0; y < h; y++)
                        {
                            double a = Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            if (a > 0) terrainHeight++;
                        }
                        glVertex2d(x, h - terrainHeight);
                        glVertex2d(x, h);
                    }
                    glEnd();
                    break;

                case "heightmap 2":
                    glBegin(GL_LINES);
                    for (int x = 0; x < w; x++)
                    {
                        terrainHeight = 0;
                        for (int y = 0; y < h; y++)
                        {
                            double a = (double) y / h + Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            if (a > 0) terrainHeight++;
                        }
                        glVertex2d(x, h - terrainHeight);
                        glVertex2d(x, h);
                    }
                    glEnd();
                    break;

                case "volcano tunnels":
                    glBegin(GL_POINTS);
                    int xCenter = w >> 1, yCenter = h >> 1;
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            double a = 1 + Noise.brownian(x, y, amplitude * (yCenter - y) / h * Math.abs(x - xCenter) / w, amplitudeGain * (yCenter - y) / h * Math.abs(x - xCenter) / w, frequency, frequencyGain, octaves, permutations);
                            a = a * a / 2 - 1;
                            if (a > 0) glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "tunnels":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (1 - Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            f = f * f / 2 - 1;
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "warped":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (1 - Noise.brownian(x, y, amplitude, amplitudeGain, frequency * x / y, frequencyGain, octaves, permutations));
                            f = f * f / 2 - 1;
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "fan":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (1 - Noise.brownian(x, y, amplitude * x / y, amplitudeGain * x / y, frequency, frequencyGain, octaves, permutations));
                            f = f * f / 2 - 1;
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "circuit board":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (Noise.brownian(x ^ y, y ^ x, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "random mesh":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (Noise.brownian(TrigLookupTable.TRIG_TABLE_1024.sin(x), TrigLookupTable.TRIG_TABLE_1024.sin(y), amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "edge stretch":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (Noise.brownian(x * x, y * y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "cross hash 1":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (Noise.brownian(x * permutations[x % permutations.length], y * permutations[y % permutations.length], amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "cross hash 2":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) (Noise.brownian(x * permutations[y % permutations.length], y * permutations[x % permutations.length], amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "clouds":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = 0.2f + 0.2f * (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            glColor4f(0.3f + f, 0.3f + f, 1, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "valleys":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = 0.2f + 0.2f * (float) Math.abs(Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "ridges":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = 1 - (0.4f + 0.4f * (float) Math.abs(Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations)));
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "land and sea":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = 0.2f + 0.2f * (float) Tools.posMod(Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations), 1);
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "water damage":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            f = (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency + f, frequencyGain, octaves, permutations);
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "organic 1":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            f = (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency * f, frequencyGain, octaves, permutations);
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;

                case "ruffled organic":
                    glBegin(GL_POINTS);
                    for (int x = 0; x < w; x++)
                    {
                        for (int y = 0; y < h; y++)
                        {
                            float f = (float) Noise.brownian(x, y, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            f = (float) Noise.brownian(x + f * 900, y + f * 200, amplitude, amplitudeGain, frequency, frequencyGain, octaves, permutations);
                            glColor4f(f, f, f, 1);
                            glVertex2d(x, y);
                        }
                    }
                    glEnd();
                    break;
            }

            Display.update();
        }
    }
}
