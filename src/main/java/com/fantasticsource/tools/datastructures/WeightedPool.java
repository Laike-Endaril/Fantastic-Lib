package com.fantasticsource.tools.datastructures;

import com.fantasticsource.tools.Tools;

import java.util.HashMap;
import java.util.Map;

public class WeightedPool<T>
{
    public HashMap<T, Integer> pool = new HashMap<>();
    public int totalWeight = 0;

    public void addWeight(T object, int weight)
    {
        int old = pool.getOrDefault(object, 0);
        weight += old;
        if (weight <= 0) totalWeight -= pool.remove(object);
        else
        {
            pool.put(object, weight);
            totalWeight += weight - old;
        }
    }

    public void setWeight(T object, int weight)
    {
        if (weight < 0) weight = 0;
        totalWeight -= pool.getOrDefault(object, 0);
        if (weight == 0) pool.remove(object);
        else pool.put(object, weight);
        totalWeight += weight;
    }

    public int getWeight(T object)
    {
        return pool.getOrDefault(object, 0);
    }

    public double getChance(T object)
    {
        if (totalWeight == 0) return 0;
        return (double) pool.getOrDefault(object, 0) / totalWeight;
    }

    public T getRandom()
    {
        if (totalWeight == 0) return null;

        int index = Tools.random(totalWeight);
        for (Map.Entry<T, Integer> entry : pool.entrySet())
        {
            if (index < entry.getValue()) return entry.getKey();
            index -= entry.getValue();
        }

        throw new IllegalStateException();
    }
}
