package com.fantasticsource.tools.datastructures;

public class VectorN
{
    public final double[] values;

    public VectorN(double... values)
    {
        if (values == null) throw new NullPointerException();
        this.values = values;
    }


    public double getMagnitude()
    {
        return Math.sqrt(getMagnitudeSquared());
    }

    public double getMagnitudeSquared()
    {
        double squareMagnitude = 0;
        for (double d : values) squareMagnitude += d * d;
        return squareMagnitude;
    }


    public void scale(double scalar)
    {
        for (int i = 0; i < values.length; i++)
        {
            values[i] = values[i] * scalar;
        }
    }


    public void setMagnitude(double magnitude)
    {
        double ratio = magnitude / getMagnitude();
        for (int i = 0; i < values.length; i++) values[i] = values[i] * ratio;
    }


    public void add(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] += vector.values[i];
            }
        }
    }

    public void subtract(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] -= vector.values[i];
            }
        }
    }
}
