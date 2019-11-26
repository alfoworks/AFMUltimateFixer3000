package ru.allformine.afmuf;

import java.lang.reflect.Field;

public class Utils {
    public static <T, E> T getPrivateValue(Class<? super E> classToAccess, E instance, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = classToAccess.getDeclaredField(name);
        field.setAccessible(true);
        return (T) field.get(instance);
    }
}
