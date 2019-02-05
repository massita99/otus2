package ru.massita.mock;


import ru.massita.framework.annotaions.BeforeEach;
import ru.massita.framework.annotaions.Test;

import static ru.massita.framework.assertions.Assertions.verify;

public class BeforeEachTest {

    boolean beforeEachExecuted = false;

    @BeforeEach
    public void prepare() {
        beforeEachExecuted = true;
    }

    @Test
    public void test1() {
        verify(beforeEachExecuted);
        beforeEachExecuted = false;
    }

    @Test
    public void test2() {
        verify(beforeEachExecuted);
        beforeEachExecuted = false;
    }
}
