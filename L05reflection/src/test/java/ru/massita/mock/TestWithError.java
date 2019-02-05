package ru.massita.mock;

import ru.massita.framework.annotaions.Test;

public class TestWithError {

    @Test
    public void errorTest() {
        throw new RuntimeException();
    }

    @Test
    public void okTest() {

    }
}
