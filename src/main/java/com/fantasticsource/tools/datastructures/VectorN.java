package com.fantasticsource.tools.datastructures;

import com.fantasticsource.lwjgl.Quaternion;
import com.fantasticsource.mctools.MCTools;

public class VectorN
{
    public final double[] values;

    public VectorN(double... values)
    {
        if (values == null) throw new NullPointerException();
        this.values = new double[values.length];
        System.arraycopy(values, 0, this.values, 0, values.length);
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


    public VectorN power(double... values)
    {
        for (int i = 0; i < this.values.length; i++)
        {
            this.values[i] = Math.pow(this.values[i], values[i]);
        }
        return this;
    }

    public VectorN power(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length; i++)
            {
                values[i] = Math.pow(values[i], vector.values[i]);
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


    /**
     * Only doing 3D rotations for now
     */
    public VectorN rotate(VectorN axis, double theta)
    {
        Quaternion quaternion = MCTools.rotatedQuaternion(new Quaternion((float) values[0], (float) values[1], (float) values[2], 0), new Quaternion((float) axis.values[0], (float) axis.values[1], (float) axis.values[2], 0), theta);
        values[0] = quaternion.x;
        values[1] = quaternion.y;
        values[2] = quaternion.z;
        return this;
    }


    @Override
    public String toString()
    {
        if (values.length == 0) return "()";

        StringBuilder result = new StringBuilder("(" + values[0]);
        for (int i = 1; i < values.length; i++) result.append(", ").append(values[i]);
        return result + ")";
    }
}
