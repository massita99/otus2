package com.massita;

import org.junit.Assert;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static com.massita.MultyThreadSort.*;

public class MultyThreadSortTest {

    @org.junit.Test
    public void simpleOneThreadTest() {
        List<Integer> input = Arrays.asList(3, 2, 1);
        List<Integer> expectedResult = List.of(1, 2, 3);

        List<Integer> sorted = oneThreadSort(input);

        Assert.assertEquals(expectedResult, sorted);
    }

    @org.junit.Test
    public void simpleOneThreadMergeTest() {
        List<Integer> first = List.of(1, 3, 10);
        List<Integer> second = List.of(0, 1, 2);
        List<Integer> expectedResult = List.of(0, 1, 1, 2, 3, 10);

        List<Integer> sorted = oneThreadMergeSort(first, second);

        Assert.assertEquals(expectedResult, sorted);
    }

    @org.junit.Test
    public void simpleMultiThreadTest() throws ExecutionException, InterruptedException {
        List<Integer> input = Arrays.asList(3, 1, 2, 6, 7);

        List<Integer> expectedResult = List.of(1, 2, 3, 6, 7);

        List<Integer> sorted = multiThreadSort(input);

        Assert.assertEquals(expectedResult, sorted);
    }

    @org.junit.Test
    public void bigMultiThreadTest() throws ExecutionException, InterruptedException {
        Random random = new Random();

        List<Integer> input = new ArrayList<>();
        IntStream.rangeClosed(1, 10000000).map(el -> random.nextInt()).distinct().forEach(input::add);

        List<Integer> inputForAssert = new ArrayList<>(input);

        //Just Get rough time of multisort
        long start = System.currentTimeMillis();
        List<Integer> sorted = multiThreadSort(input);
        long finish = System.currentTimeMillis();
        System.out.println("MultiThread time: " + (finish-start));

        //Just Get rough time of ordinary method
        start = System.currentTimeMillis();
        inputForAssert.sort(Comparator.naturalOrder());
        finish = System.currentTimeMillis();
        System.out.println("OneThread time: " + (finish-start));

        Assert.assertEquals(inputForAssert, sorted);
    }
}