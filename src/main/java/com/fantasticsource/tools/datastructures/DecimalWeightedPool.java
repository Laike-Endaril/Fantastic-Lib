package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

import java.util.HashMap;
import java.util.Map;

public class DecimalWeightedPool<T>
{
    public HashMap<T, Double> pool = new HashMap<>();
    public double totalWeight = 0;

    public void addWeight(T object, double weight)
    {
        double old = pool.getOrDefault(object, 0d);
        weight += old;
        if (weight <= 0) totalWeight -= pool.remove(object);
        else
        {
            pool.put(object, weight);
            totalWeight += weight - old;
        }
    }

    public void setWeight(T object, double weight)
    {
        if (weight < 0) weight = 0;
        totalWeight -= pool.getOrDefault(object, 0d);
        if (weight == 0) pool.remove(object);
        else pool.put(object, weight);
        totalWeight += weight;
    }

    public double getWeight(T object)
    {
        return pool.getOrDefault(object, 0d);
    }

    public double getChance(T object)
    {
        if (totalWeight == 0) return 0;
        return pool.getOrDefault(object, 0d) / totalWeight;
    }

    public T getRandom()
    {
        if (totalWeight == 0) return null;

        double index = Tools.random(totalWeight);
        for (Map.Entry<T, Double> entry : pool.entrySet())
        {
            if (index < entry.getValue()) return entry.getKey();
            index -= entry.getValue();
        }

        throw new IllegalStateException();
    }
}
