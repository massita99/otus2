package ru.massita.framework.runner;

import lombok.Getter;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

@Setter
@Getter
public class TestResult {

    private int testCount;

    private List<String> errors;

    private TestStatus status;

    public TestResult() {
        this.testCount = 0;
        this.errors = new LinkedList<>();
        this.status = TestStatus.PASSED;
    }


}
