package ru.massita.framework.assertions;

public class Assertions {

    public static void verify(boolean condition) throws AssertionError {
        if (condition) {
            return;
        } else {
            throw new AssertionError();
        }
    }
}
