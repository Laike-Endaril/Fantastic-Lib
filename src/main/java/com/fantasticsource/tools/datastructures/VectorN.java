package com.fantasticsource.tools.datastructures;

public class VectorN
{
    public final double[] values;

    public VectorN(double... values)
    {
        if (values == null) throw new NullPointerException();
        this.values = values;
    }


    public VectorN copy()
    {
        return new VectorN(values);
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


    public VectorN scale(double scalar)
    {
        for (int i = 0; i < values.length; i++)
        {
            values[i] = values[i] * scalar;
        }
        return this;
    }


    public VectorN setMagnitude(double magnitude)
    {
        double ratio = magnitude / getMagnitude();
        for (int i = 0; i < values.length; i++) values[i] = values[i] * ratio;
        return this;
    }


    public VectorN add(double... values)
    {
        for (int i = 0; i < this.values.length; i++)
        {
            this.values[i] += values[i];
        }
        return this;
    }

    public VectorN add(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] += vector.values[i];
            }
        }
        return this;
    }


    public VectorN subtract(double... values)
    {
        for (int i = 0; i < this.values.length; i++)
        {
            this.values[i] -= values[i];
        }
        return this;
    }

    public VectorN subtract(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] -= vector.values[i];
            }
        }
        return this;
    }


    public VectorN multiply(double... values)
    {
        for (int i = 0; i < this.values.length; i++)
        {
            this.values[i] *= values[i];
        }
        return this;
    }

    public VectorN multiply(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] *= vector.values[i];
            }
        }
        return this;
    }


    public VectorN divide(double... values)
    {
        for (int i = 0; i < this.values.length; i++)
        {
            this.values[i] /= values[i];
        }
        return this;
    }

    public VectorN divide(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] /= vector.values[i];
            }
        }
        return this;
    }

    /**
     * Only doing 3D cross product for now...I don't see myself using the alternatives
     */
    public VectorN crossProduct(VectorN other)
    {
        double[] values = new double[this.values.length];
        System.arraycopy(this.values, 0, values, 0, values.length);

        this.values[0] = values[1] * other.values[2] - values[2] * other.values[1];
        this.values[1] = values[2] * other.values[0] - values[0] * other.values[2];
        this.values[2] = values[0] * other.values[1] - values[1] * other.values[0];

        return this;
    }
}
