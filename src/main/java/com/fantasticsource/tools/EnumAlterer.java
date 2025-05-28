package com.fantasticsource.tools;

import sun.reflect.ConstructorAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumAlterer
{
    //Thanks to https://www.niceideas.ch/roller2/badtrash/entry/java-create-enum-instances-dynamically
    //I've edited it a lot though ~Kyle Koder aka Laike Endaril

    private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    private static void cleanEnumCache(Class<?> enumClass)
    {
        ReflectionTool.set(Class.class, "enumConstantDirectory", enumClass, null); // Sun (Oracle?!?) JDK 1.5/6
        ReflectionTool.set(Class.class, "enumConstants", enumClass, null); // IBM JDK
    }

    private static ConstructorAccessor getConstructorAccessor(Class<?> enumClass, Class<?>[] additionalParameterTypes) throws NoSuchMethodException
    {
        Class<?>[] parameterTypes = new Class[additionalParameterTypes.length + 2];
        parameterTypes[0] = String.class;
        parameterTypes[1] = int.class;
        System.arraycopy(additionalParameterTypes, 0, parameterTypes, 2, additionalParameterTypes.length);
        return reflectionFactory.newConstructorAccessor(enumClass.getDeclaredConstructor(parameterTypes));
    }

    private static Object makeEnum(Class<?> enumClass, String value, int ordinal, Class<?>[] additionalTypes, Object[] additionalValues) throws Exception
    {
        Object[] params = new Object[additionalValues.length + 2];
        params[0] = value;
        params[1] = ordinal;
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
        return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(params));
    }

    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName)
    {
        // 0. Sanity checks
        if (!Enum.class.isAssignableFrom(enumType))
        {
            throw new RuntimeException("class " + enumType + " is not an instance of Enum");
        }

        // 1. Lookup "$VALUES" holder in enum class and get previous enum instances
        Field valuesField = ReflectionTool.getField(enumType, "$VALUES");

        try
        {
            T[] previousValues = (T[]) ReflectionTool.get(valuesField, enumType);
            List<T> values = new ArrayList<>(Arrays.asList(previousValues));

            T newValue = (T) makeEnum(enumType, enumName, values.size(), new Class<?>[]{}, new Object[]{});

            // 4. add new value
            values.add(newValue);

            // 5. Set new values field
            ReflectionTool.set(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));

            // 6. Clean enum cache
            cleanEnumCache(enumType);

            return newValue;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static void setEnumOrdinal(Enum object, int ordinal)
    {
        ReflectionTool.set(object.getClass().getSuperclass(), "ordinal", object, ordinal);
    }
}
