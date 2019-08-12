package com.fantasticsource.tools;

import sun.reflect.ConstructorAccessor;
import sun.reflect.FieldAccessor;
import sun.reflect.ReflectionFactory;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EnumAlterer
{
    //Thanks to https://www.niceideas.ch/roller2/badtrash/entry/java-create-enum-instances-dynamically

    private static ReflectionFactory reflectionFactory = ReflectionFactory.getReflectionFactory();

    private static void setFailsafeFieldValue(Field field, Object target, Object value) throws NoSuchFieldException, IllegalAccessException
    {
        // let's make the field accessible
        field.setAccessible(true);

        // next we change the modifier in the Field instance to
        // not be final anymore, thus tricking reflection into
        // letting us modify the static final field
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        int modifiers = modifiersField.getInt(field);

        // blank out the final bit in the modifiers int
        modifiers &= ~Modifier.FINAL;
        modifiersField.setInt(field, modifiers);

        FieldAccessor fa = reflectionFactory.newFieldAccessor(field, false);
        fa.set(target, value);
    }

    private static void blankField(Class<?> enumClass, String fieldName) throws NoSuchFieldException, IllegalAccessException
    {
        for (Field field : Class.class.getDeclaredFields())
        {
            if (field.getName().contains(fieldName))
            {
                AccessibleObject.setAccessible(new Field[]{field}, true);
                setFailsafeFieldValue(field, enumClass, null);
                break;
            }
        }
    }

    private static void cleanEnumCache(Class<?> enumClass) throws NoSuchFieldException, IllegalAccessException
    {
        blankField(enumClass, "enumConstantDirectory"); // Sun (Oracle?!?) JDK 1.5/6
        blankField(enumClass, "enumConstants"); // IBM JDK
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
        params[1] = Integer.valueOf(ordinal);
        System.arraycopy(additionalValues, 0, params, 2, additionalValues.length);
        return enumClass.cast(getConstructorAccessor(enumClass, additionalTypes).newInstance(params));
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<?>> T addEnum(Class<T> enumType, String enumName)
    {
        // 0. Sanity checks
        if (!Enum.class.isAssignableFrom(enumType))
        {
            throw new RuntimeException("class " + enumType + " is not an instance of Enum");
        }

        // 1. Lookup "$VALUES" holder in enum class and get previous enum instances
        Field valuesField = null;
        Field[] fields = enumType.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.getName().contains("$VALUES"))
            {
                valuesField = field;
                break;
            }
        }
        AccessibleObject.setAccessible(new Field[]{valuesField}, true);

        try
        {
            T[] previousValues = (T[]) valuesField.get(enumType);
            List<T> values = new ArrayList<>(Arrays.asList(previousValues));

            T newValue = (T) makeEnum(enumType, enumName, values.size(), new Class<?>[]{}, new Object[]{});

            // 4. add new value
            values.add(newValue);

            // 5. Set new values field
            setFailsafeFieldValue(valuesField, null, values.toArray((T[]) Array.newInstance(enumType, 0)));

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
        try
        {
            Field field = object.getClass().getSuperclass().getDeclaredField("ordinal");
            field.setAccessible(true);
            field.set(object, ordinal);
        }
        catch (Exception ex)
        {
            throw new RuntimeException("Can't update enum ordinal: " + ex);
        }
    }
}
