package ru.massita.framework.helpers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class ReflectionHelper {

    public static List<Method> getMethodsByAnnotation(Class testClass, Class annotation) {
        List<Method> result = new LinkedList<>();
        List<Method> allMethods = Arrays.asList(testClass.getDeclaredMethods());
        for (Method method : allMethods) {
            Annotation requestedAnnotation = method.getAnnotation(annotation);
            if (Objects.nonNull(requestedAnnotation)) {
                result.add(method);
            }
        }
        return result;
    }
}
