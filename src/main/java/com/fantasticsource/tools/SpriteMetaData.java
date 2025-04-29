package com.fantasticsource.tools;

import java.util.ArrayList;

public class SpriteMetaData
{
    public ArrayList<FrameMetaData> frames = new ArrayList<>();

    public SpriteMetaData()
    {
    }

    public SpriteMetaData(int textureTotalWidth, int textureTotalHeight, int firstFrameX1, int firstFrameY1, int firstFrameX2, int firstFrameY2, boolean spriteFramesProgressVertically, int frameCount)
    {
        frames.add(new FrameMetaData((double) firstFrameX1 / textureTotalWidth, (double) firstFrameY1 / textureTotalHeight, (double) firstFrameX2 / textureTotalWidth, (double) firstFrameY2 / textureTotalHeight));

        int w = firstFrameX2 - firstFrameX1;
        int h = firstFrameY2 - firstFrameY1;
        for (int i = 1; i < frameCount; i++)
        {
            if (spriteFramesProgressVertically)
            {
                if (firstFrameY2 == textureTotalHeight)
                {
                    firstFrameX1 += w;
                    firstFrameX2 += w;
                    firstFrameY1 = 0;
                    firstFrameY2 = firstFrameY1 + h;
                }
                else
                {
                    firstFrameY1 += h;
                    firstFrameY2 += h;
                }
            }
            else
            {
                if (firstFrameX2 == textureTotalWidth)
                {
                    firstFrameY1 += h;
                    firstFrameY2 += h;
                    firstFrameX1 = 0;
                    firstFrameX2 = firstFrameX1 + w;
                }
                else
                {
                    firstFrameX1 += w;
                    firstFrameX2 += w;
                }
            }

            frames.add(new FrameMetaData((double) firstFrameX1 / textureTotalWidth, (double) firstFrameY1 / textureTotalHeight, (double) firstFrameX2 / textureTotalWidth, (double) firstFrameY2 / textureTotalHeight));
        }
    }

    public SpriteMetaData(int textureTotalWidth, int textureTotalHeight, int... framePixelValues)
    {
        if (framePixelValues.length == 0) throw new IllegalArgumentException("You must specify values for at least one frame");
        if (framePixelValues.length % 4 != 0) throw new IllegalArgumentException("The number of values passed in for pixel coordinates must be a multiple of 4; x1, y1, x2, y2, repeat");

        for (int i = 0; i < framePixelValues.length; i += 4)
        {
            frames.add(new FrameMetaData((double) framePixelValues[i] / textureTotalWidth, (double) framePixelValues[i + 1] / textureTotalHeight, (double) framePixelValues[i + 2] / textureTotalWidth, (double) framePixelValues[i + 3] / textureTotalHeight));
        }
    }

    public SpriteMetaData(double... frameUVValues)
    {
        if (frameUVValues.length == 0) throw new IllegalArgumentException("You must specify values for at least one frame");
        if (frameUVValues.length % 4 != 0) throw new IllegalArgumentException("The number of values passed in for UV coordinates must be a multiple of 4; u1, v1, u2, v2, repeat");

        for (int i = 0; i < frameUVValues.length; i += 4)
        {
            frames.add(new FrameMetaData(frameUVValues[i], frameUVValues[i + 1], frameUVValues[i + 2], frameUVValues[i + 3]));
        }
    }


    public SpriteMetaData setNormalizedOriginStatic(double relativeOriginX, double relativeOriginY)
    {
        for (FrameMetaData frame : frames) frame.setNormalizedOrigin(relativeOriginX, relativeOriginY);
        return this;
    }


    public SpriteMetaData setNormalizedOriginDynamic(double... relativeOriginCoordsPerFrame)
    {
        if (relativeOriginCoordsPerFrame.length != frames.size() * 2) throw new IllegalArgumentException("Relative X and Y for each frame must be passed in");

        for (int i = 0; i < frames.size(); i++)
        {
            frames.get(i).setNormalizedOrigin(relativeOriginCoordsPerFrame[i * 2], relativeOriginCoordsPerFrame[i * 2 + 1]);
        }
        return this;
    }


    public static class FrameMetaData
    {
        public double u1, v1, u2, v2, relativeOriginX = 0.5, relativeOriginY = 0.5;

        public FrameMetaData(double u1, double v1, double u2, double v2)
        {
            this.u1 = u1;
            this.v1 = v1;
            this.u2 = u2;
            this.v2 = v2;
        }

        public void setOrigin(int frameWidth, int frameHeight, int relativePixelOriginX, int relativePixelOriginY)
        {
            setNormalizedOrigin((double) relativePixelOriginX / frameWidth, (double) relativePixelOriginY / frameHeight);
        }

        public void setNormalizedOrigin(double relativeOriginX, double relativeOriginY)
        {
            this.relativeOriginX = relativeOriginX;
            this.relativeOriginY = relativeOriginY;
        }
    }
}
