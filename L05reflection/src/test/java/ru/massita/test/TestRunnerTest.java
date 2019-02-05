package ru.massita.test;

import org.junit.Test;
import ru.massita.framework.runner.TestRunner;
import ru.massita.framework.runner.TestStatus;
import ru.massita.mock.*;

import static org.junit.Assert.assertEquals;

public class TestRunnerTest {

    @Test
    public void simpleTest() {
        TestRunner.run(SimpleTest.class);
        assertEquals(TestRunner.getTestCount(), 2);
        assertEquals(TestRunner.getStatus(), TestStatus.PASSED);
    }

    @Test
    public void withErrorTest() {
        TestRunner.run(TestWithError.class);
        assertEquals(TestRunner.getTestCount(), 2);
        assertEquals(TestRunner.getErrors().size(), 1);
        assertEquals(TestRunner.getStatus(), TestStatus.FAILED);

    }

    @Test
    public void withAssertionTest() {
        TestRunner.run(TestWithError.class);
        assertEquals(TestRunner.getErrors().size(), 1);
        assertEquals(TestRunner.getTestCount(), 2);
        assertEquals(TestRunner.getStatus(), TestStatus.FAILED);

    }

    @Test
    public void beforeEachTest() {
        TestRunner.run(BeforeEachTest.class);
        assertEquals(TestRunner.getErrors().size(), 0);
        assertEquals(TestRunner.getTestCount(), 2);
        assertEquals(TestRunner.getStatus(), TestStatus.PASSED);

    }

    @Test
    public void afterEachTest() {
        TestRunner.run(AfterEachTest.class);
        assertEquals(TestRunner.getErrors().size(), 0);
        assertEquals(TestRunner.getTestCount(), 3);
        assertEquals(TestRunner.getStatus(), TestStatus.PASSED);

    }

    @Test
    public void beforeEachErrorTest() {
        TestRunner.run(BeforeEachErrorTest.class);
        assertEquals(TestRunner.getStatus(), TestStatus.ERROR);
    }
}