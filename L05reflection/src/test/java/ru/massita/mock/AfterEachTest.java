package ru.massita.mock;


import ru.massita.framework.annotaions.BeforeEach;
import ru.massita.framework.annotaions.Test;

import static ru.massita.framework.assertions.Assertions.verify;

public class AfterEachTest {

    boolean afterEachExecuted = true;

    @BeforeEach
    public void prepare() {
        afterEachExecuted = true;
    }

    @Test
    public void test1() {
        verify(afterEachExecuted);
        afterEachExecuted = false;
    }

    @Test
    public void test2() {
        verify(afterEachExecuted);
        afterEachExecuted = false;
    }

    @Test
    public void test3() {
        verify(afterEachExecuted);
        afterEachExecuted = false;
    }
}
