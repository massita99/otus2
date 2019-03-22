package com.massita.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

public class ReflectionUtil {

    public static List<Field> getAllSerializableFields(Class<?> type) {
        List<Field> resultFields = new LinkedList<>();
        Field[] currentClassFields = type.getDeclaredFields();

        for (Field field : currentClassFields) {
            //Don't need static fields
            if (java.lang.reflect.Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //Don't need transient fields
            if (java.lang.reflect.Modifier.isTransient(field.getModifiers())) {
                continue;
            }
            resultFields.add(field);
        }

        if (type.getSuperclass() != null) {
            resultFields.addAll(getAllSerializableFields(type.getSuperclass()));
        }
        return resultFields;
    }
}
