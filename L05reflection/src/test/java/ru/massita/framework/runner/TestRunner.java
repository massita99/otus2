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

    private static ThreadLocal<Integer> testCount = new ThreadLocal<>();

    private static ThreadLocal<List<String>> errors = new ThreadLocal<>();

    private static ThreadLocal<TestStatus> status = new ThreadLocal<>();

    public static <T> void run(Class<T> clazz) {
        init();
        try {
            List<Method> testMethods = ReflectionHelper.getMethodsByAnnotation(clazz, Test.class);
            List<Method> beforeEachMethods = ReflectionHelper.getMethodsByAnnotation(clazz, BeforeEach.class);
            List<Method> afterEachMethods = ReflectionHelper.getMethodsByAnnotation(clazz, AfterEach.class);
            for (Method method : testMethods) {
                testCount.set(testCount.get() + 1);
                T object = clazz.newInstance();
                executeEach(beforeEachMethods, object);
                executeTest(method, object);
                executeEach(afterEachMethods, object);
            }
        } catch (TestExecutionException | InstantiationException | IllegalAccessException e) {
            status.set(TestStatus.ERROR);
            e.printStackTrace();
        }
    }

    private static void executeEach(List<Method> beforeEachMethods, Object object) throws TestExecutionException {
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
        testCount.set(0);
        errors.set(new LinkedList<>());
        status.set(TestStatus.PASSED);
    }

    private static void executeTest(Method method, Object object) {
        try {
            method.invoke(object);
        } catch (InvocationTargetException | IllegalAccessException e ) {
            status.set(TestStatus.FAILED);
            errors.get().add(e.toString());
        }
    }


    public static int getTestCount() {
        return testCount.get();
    }

    public static List<String> getErrors() {
        return List.copyOf(errors.get());
    }

    public static TestStatus getStatus() {
        return status.get();
    }
}
