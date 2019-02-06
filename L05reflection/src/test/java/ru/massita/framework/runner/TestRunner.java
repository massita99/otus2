package ru.massita.framework.runner;

import ru.massita.framework.annotaions.AfterEach;
import ru.massita.framework.annotaions.BeforeEach;
import ru.massita.framework.annotaions.Test;
import ru.massita.framework.exception.TestExecutionException;
import ru.massita.framework.helpers.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class TestRunner {

    public static <T> TestResult run(Class<T> clazz) {
        TestResult result = init();
        try {
            List<Method> testMethods = ReflectionHelper.getMethodsByAnnotation(clazz, Test.class);
            List<Method> beforeEachMethods = ReflectionHelper.getMethodsByAnnotation(clazz, BeforeEach.class);
            List<Method> afterEachMethods = ReflectionHelper.getMethodsByAnnotation(clazz, AfterEach.class);
            for (Method method : testMethods) {
                result.setTestCount(result.getTestCount() + 1);
                T object = clazz.newInstance();
                executeEach(beforeEachMethods, object, result);
                executeTest(method, object, result);
                executeEach(afterEachMethods, object, result);
            }
        } catch (TestExecutionException | InstantiationException | IllegalAccessException e) {
            result.setStatus(TestStatus.ERROR);
            e.printStackTrace();
        }
        return result;
    }

    private static void executeEach(List<Method> beforeEachMethods, Object object, TestResult result) throws TestExecutionException {
        for (Method method : beforeEachMethods) {
            try {
                method.invoke(object);
            } catch (InvocationTargetException | IllegalAccessException e ) {
                throw new TestExecutionException(String.format("Method %s in class %s failed. Execution Stopped.",
                        method.getName(),
                        object.getClass().getName()),e);
            }
        }
    }



    private static TestResult init() {
        return new TestResult();
    }

    private static void executeTest(Method method, Object object, TestResult result) {
        try {
            method.invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e ) {
            result.setStatus(TestStatus.FAILED);
            result.getErrors().add(e.toString());
        }
    }


}
