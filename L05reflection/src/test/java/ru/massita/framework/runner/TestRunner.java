package ru.massita.framework.runner;

import ru.massita.framework.annotaions.AfterEach;
import ru.massita.framework.annotaions.BeforeEach;
import ru.massita.framework.annotaions.Test;
import ru.massita.framework.exception.TestExecutionException;
import ru.massita.framework.helpers.ReflectionHelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

public class TestRunner {

    private static int testCount = 0;

    private static List<String> errors = new LinkedList<>();

    private static TestStatus status = TestStatus.PASSED;

    public static <T> void run(Class<T> clazz) {
        init();
        try {
            T object = clazz.newInstance();

            List<Method> testMethods = ReflectionHelper.getMethodsByAnnotation(clazz, Test.class);
            List<Method> beforeEachMethods = ReflectionHelper.getMethodsByAnnotation(clazz, BeforeEach.class);
            List<Method> afterEachMethods = ReflectionHelper.getMethodsByAnnotation(clazz, AfterEach.class);
            for (Method method : testMethods) {
                testCount++;
                executeBefore(beforeEachMethods, object);
                executeTest(method, object);
                executeAfter(afterEachMethods, object);
            }
        } catch (TestExecutionException | InstantiationException | IllegalAccessException e) {
            status = TestStatus.ERROR;
            e.printStackTrace();
        }
    }

    private static void executeBefore(List<Method> beforeEachMethods, Object object) throws TestExecutionException {
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

    private static void executeAfter(List<Method> beforeEachMethods, Object object) throws TestExecutionException {
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

    private static void init() {
        testCount = 0;
        errors = new LinkedList<>();
        status = TestStatus.PASSED;
    }

    private static void executeTest(Method method, Object object) {
        try {
            method.invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e ) {
            status = TestStatus.FAILED;
            errors.add(e.toString());
        }
    }


    public static int getTestCount() {
        return testCount;
    }

    public static List<String> getErrors() {
        return errors;
    }

    public static TestStatus getStatus() {
        return status;
    }
}
