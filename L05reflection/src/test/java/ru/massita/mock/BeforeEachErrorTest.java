package ru.massita.mock;

import ru.massita.framework.annotaions.BeforeEach;
import ru.massita.framework.annotaions.Test;

public class BeforeEachErrorTest {

    @BeforeEach
    public void prepare() {
        throw new RuntimeException();
    }

    @Test
    public void test1() {
    }
}
