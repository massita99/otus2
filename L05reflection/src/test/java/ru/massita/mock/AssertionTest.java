package ru.massita.mock;

import ru.massita.framework.annotaions.Test;

import static ru.massita.framework.assertions.Assertions.verify;

public class AssertionTest {

    @Test
    public void test(){
        verify(true);
    }

    @Test
    public void falseTest() {
        verify(false);
    }
}
