package com.fantasticsource.tools.datastructures;

@SuppressWarnings("unused")
public class WrappingQueue<T>
{
    private Object[] array;
    private int insertPos = 0, startPos = 0, used = 0;

    public WrappingQueue(int capacity)
    {
        array = new Object[capacity];
    }

    /**
     * Returns true if an entry was overwritten
     */
    public boolean add(T t)
    {
        array[insertPos] = t;

        if (++insertPos == array.length) insertPos = 0;

        if (++used > array.length)
        {
            used = array.length;
            if (++startPos == array.length) startPos = 0;
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public T getOldestToNewest(int index)
    {
        if (index < 0 || index >= used) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + used);
        return (T) array[(index + startPos) % array.length];
    }

    @SuppressWarnings("unchecked")
    public T getNewestToOldest(int index)
    {
        if (index <= -used || index > 0) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + used);
        return (T) array[(index + insertPos + array.length) % array.length];
    }

    public T pop()
    {
        return remove(0);
    }

    /**
     * Returns true if the object was removed, false if object was not found
     */
    @SuppressWarnings("unchecked")
    public T remove(int index)
    {
        if (index < 0 || index >= used) throw new ArrayIndexOutOfBoundsException("Index: " + index + ", Length: " + used);

        int i = (index + startPos) % array.length;
        T result = (T) array[i];
        for (; index >= 0; index--)
        {
            if (i == 0) array[0] = array[array.length - 1];
            else array[i] = array[i - 1];

            if (--i < 0) i = array.length - 1;
        }

        used--;
        startPos++;
        if (startPos == array.length) startPos = 0;

        return result;
    }

    /**
     * Returns true if the object was removed, false if object was not found
     */
    public boolean remove(T t)
    {
        int index = indexOf(t);
        if (index == -1) return false;
        remove(index);
        return true;
    }

    public Object[] toArray()
    {
        return toArray(0, used);
    }

    public Object[] toArray(int index, int length)
    {
        if (index < 0 || length < 0 || index + length > used) throw new ArrayIndexOutOfBoundsException("Index: " + index + length + ", Length: " + used);

        Object[] result = new Object[length];
        index = (index + startPos) % array.length;
        for (int i = 0; i < length; i++)
        {
            result[i] = array[index];
            if (++index == array.length) index = 0;
        }

        return result;
    }

    public int size()
    {
        return used;
    }

    public int indexOf(T t)
    {
        int index = startPos;
        for (int i = 0; i < used; i++)
        {
            if (array[index].equals(t)) return i;
            if (++index == array.length) index = 0;
        }
        return -1;
    }

    public boolean contains(T t)
    {
        return indexOf(t) != -1;
    }

    public void clear()
    {
        startPos = 0;
        used = 0;
        insertPos = 0;
    }
}
