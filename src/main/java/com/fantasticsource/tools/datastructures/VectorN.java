package com.fantasticsource.tools.datastructures;

import com.fantasticsource.lwjgl.Quaternion;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.TrigLookupTable;

public class VectorN
{
    public static final VectorN
            X_AXIS = new VectorN(1, 0, 0),
            Y_AXIS = new VectorN(0, 1, 0),
            Z_AXIS = new VectorN(0, 0, 1);


    public double[] values;

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


    public boolean isPoint()
    {
        for (double value : values) if (value != 0) return false;
        return true;
    }

    public boolean isAllZeroes()
    {
        return isPoint();
    }

    public int zeros()
    {
        int count = 0;
        for (double value : values) if (value == 0) count++;
        return count;
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

    public VectorN reverse()
    {
        return scale(-1);
    }


    public VectorN setMagnitude(double magnitude)
    {
        double m = getMagnitude();
        if (m == 0) return this;

        double ratio = magnitude / m;
        for (int i = 0; i < values.length; i++) values[i] = values[i] * ratio;
        return this;
    }

    public VectorN normalize()
    {
        return setMagnitude(1);
    }


    public VectorN add(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] += values[i];
        }
        return this;
    }

    public VectorN add(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] += vector.values[i];
            }
        }
        return this;
    }


    public VectorN subtract(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] -= values[i];
        }
        return this;
    }

    public VectorN subtract(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] -= vector.values[i];
            }
        }
        return this;
    }


    public VectorN multiply(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] *= values[i];
        }
        return this;
    }

    public VectorN multiply(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] *= vector.values[i];
            }
        }
        return this;
    }


    public VectorN divide(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] /= values[i];
        }
        return this;
    }

    public VectorN divide(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] /= vector.values[i];
            }
        }
        return this;
    }


    public VectorN power(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] = Math.pow(this.values[i], values[i]);
        }
        return this;
    }

    public VectorN power(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] = Math.pow(values[i], vector.values[i]);
            }
        }
        return this;
    }


    public VectorN mod(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] = this.values[i] % values[i];
        }
        return this;
    }

    public VectorN mod(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] = values[i] % vector.values[i];
            }
        }
        return this;
    }


    public VectorN posMod(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] = Tools.posMod(this.values[i], values[i]);
        }
        return this;
    }

    public VectorN posMod(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] = Tools.posMod(values[i], vector.values[i]);
            }
        }
        return this;
    }


    public VectorN lowLimit(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] = Tools.max(this.values[i], values[i]);
        }
        return this;
    }

    public VectorN lowLimit(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] = Tools.max(values[i], vector.values[i]);
            }
        }
        return this;
    }


    public VectorN highLimit(double... values)
    {
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            this.values[i] = Tools.min(this.values[i], values[i]);
        }
        return this;
    }

    public VectorN highLimit(VectorN... vectors)
    {
        for (VectorN vector : vectors)
        {
            for (int i = 0; i < values.length && i < vector.values.length; i++)
            {
                values[i] = Tools.min(values[i], vector.values[i]);
            }
        }
        return this;
    }


    public double dotProduct(VectorN vector)
    {
        return dotProduct(vector.values);
    }

    public double dotProduct(double... values)
    {
        double result = 0;
        for (int i = 0; i < this.values.length && i < values.length; i++)
        {
            result += values[i] * this.values[i];
        }
        return result;
    }


    public VectorN round()
    {
        for (int i = 0; i < values.length; i++)
        {
            values[i] = Math.round(values[i]);
        }
        return this;
    }

    public VectorN floor()
    {
        for (int i = 0; i < values.length; i++)
        {
            values[i] = Math.floor(values[i]);
        }
        return this;
    }

    public VectorN ceil()
    {
        for (int i = 0; i < values.length; i++)
        {
            values[i] = Math.ceil(values[i]);
        }
        return this;
    }

    public VectorN abs()
    {
        for (int i = 0; i < values.length; i++)
        {
            values[i] = Math.abs(values[i]);
        }
        return this;
    }


    public double squareDistanceTo(VectorN other)
    {
        return squareDistanceTo(other.values);
    }

    public double squareDistanceTo(double... values)
    {
        if (this.values.length != values.length) return Double.NaN;
        double sumOfSquares = 0;
        for (int i = 0; i < values.length; i++)
        {
            sumOfSquares += (values[i] - this.values[i]) * (values[i] - this.values[i]);
        }
        return sumOfSquares;
    }

    public double distanceTo(VectorN other)
    {
        return distanceTo(other.values);
    }

    public double distanceTo(double... values)
    {
        return Math.sqrt(squareDistanceTo(values));
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


    public double angleBetween(double... values)
    {
        return angleBetween(new VectorN(values));
    }

    public double angleBetween(VectorN other)
    {
        if (isPoint() || other.isPoint()) return Double.NaN;

        VectorN v1, v2;
        if (values.length == other.values.length)
        {
            v1 = copy();
            v2 = other.copy();
        }
        else
        {
            int n = Tools.min(values.length, other.values.length);
            v1 = new VectorN();
            v1.values = new double[Tools.min(this.values.length, other.values.length)];
            System.arraycopy(this.values, 0, v1.values, 0, n);
            v2 = new VectorN();
            v2.values = new double[Tools.min(this.values.length, other.values.length)];
            System.arraycopy(other.values, 0, v2.values, 0, n);
        }
        return TrigLookupTable.TRIG_TABLE_1048576.arccos(v1.normalize().dotProduct(v2.normalize()));
    }


    public VectorN rotate(VectorN axis, double theta)
    {
        return rotate(axis, theta, TrigLookupTable.TRIG_TABLE_1048576);
    }

    /**
     * Only doing 3D rotations for now
     */
    public VectorN rotate(VectorN axis, double theta, TrigLookupTable trigTable)
    {
        theta = Tools.posMod(theta, Math.PI * 2);
        if (theta == 0) return this;
        if (axis.values.length != 3) throw new IllegalArgumentException("Can only use rotate() with a 3D axis; actual axis is " + axis.toString());


        int axisZeroes = axis.zeros();
        if (axisZeroes == 3) throw new IllegalArgumentException("Axis cannot be all zeroes!");


        Quaternion quaternion = Tools.rotatedQuaternion(new Quaternion((float) values[0], (float) values[1], (float) values[2], 0), new Quaternion((float) axis.values[0], (float) axis.values[1], (float) axis.values[2], 0), theta, trigTable);
        values[0] = quaternion.x;
        values[1] = quaternion.y;
        values[2] = quaternion.z;
        return this;
    }


    @Override
    public boolean equals(Object other)
    {
        if (this == other) return true;
        if (!(other instanceof VectorN && ((VectorN) other).values.length == values.length)) return false;
        for (int i = 0; i < values.length; i++) if (values[i] != ((VectorN) other).values[i]) return false;
        return true;
    }

    @Override
    public int hashCode()
    {
        if (values.length == 0) return 0;
        int result = Double.hashCode(values[0]);
        for (int i = 1; i < values.length; i++) result ^= Double.hashCode(values[i]);
        return result;
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
