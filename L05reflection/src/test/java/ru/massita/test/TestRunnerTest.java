package ru.massita.test;

import org.junit.Test;
import ru.massita.framework.runner.TestResult;
import ru.massita.framework.runner.TestRunner;
import ru.massita.framework.runner.TestStatus;
import ru.massita.mock.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertEquals;

public class TestRunnerTest {

    @Test
    public void simpleTest() {
        TestResult result = TestRunner.run(SimpleTest.class);
        assertEquals(result.getTestCount(), 2);
        assertEquals(result.getStatus(), TestStatus.PASSED);
    }

    @Test
    public void withErrorTest() {
        TestResult result = TestRunner.run(TestWithError.class);
        assertEquals(result.getTestCount(), 2);
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getStatus(), TestStatus.FAILED);

    }

    @Test
    public void withAssertionTest() {
        TestResult result = TestRunner.run(TestWithError.class);
        assertEquals(result.getErrors().size(), 1);
        assertEquals(result.getTestCount(), 2);
        assertEquals(result.getStatus(), TestStatus.FAILED);

    }

    @Test
    public void beforeEachTest() {
        TestResult result = TestRunner.run(BeforeEachTest.class);
        assertEquals(result.getErrors().size(), 0);
        assertEquals(result.getTestCount(), 2);
        assertEquals(result.getStatus(), TestStatus.PASSED);

    }

    @Test
    public void afterEachTest() {
        TestResult result = TestRunner.run(AfterEachTest.class);
        assertEquals(result.getErrors().size(), 0);
        assertEquals(result.getTestCount(), 3);
        assertEquals(result.getStatus(), TestStatus.PASSED);

    }

    @Test
    public void beforeEachErrorTest() {
        TestResult result = TestRunner.run(BeforeEachErrorTest.class);
        assertEquals(result.getStatus(), TestStatus.ERROR);
    }

    @Test
    public void parallelTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i<100; i++) {
            executorService.execute(() -> {
                TestResult result = TestRunner.run(SimpleTest.class);
                assertEquals(result.getStatus(), TestStatus.PASSED);
                assertEquals(result.getTestCount(), 2);
                TestResult result2 = TestRunner.run(TestWithError.class);
                assertEquals(result2.getErrors().size(), 1);
                assertEquals(result2.getTestCount(), 2);
                assertEquals(result2.getStatus(), TestStatus.FAILED);

            });
        }

    }
}