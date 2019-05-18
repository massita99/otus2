package com.massita;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static com.massita.MultyThreadSort.*;

public class MultyThreadSortTest {

    @org.junit.Test
    public void simpleOneThreadTest() {
        List<Integer> input = List.of(3, 1, 2);
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
        List<Integer> input = List.of(3, 1, 2, 6, 7);
        List<Integer> expectedResult = List.of(1, 2, 3, 6, 7);

        List<Integer> sorted = multiThreadSort(input);

        Assert.assertEquals(expectedResult, sorted);
    }

    @org.junit.Test
    public void bigMultiThreadTest() throws ExecutionException, InterruptedException {
        Random random = new Random();

        List<Integer> input = new ArrayList<>();
        IntStream.rangeClosed(1, 5000000).map(el -> random.nextInt()).distinct().forEach(input::add);

        //Just Get rough time of multisort
        long start = System.currentTimeMillis();
        List<Integer> sorted = multiThreadSort(input);
        long finish = System.currentTimeMillis();
        System.out.println(finish-start);

        //Just Get rough time of ordinary method
        start = System.currentTimeMillis();
        input.sort(Comparator.naturalOrder());
        finish = System.currentTimeMillis();
        System.out.println(finish-start);

        Assert.assertEquals(input, sorted);
    }
}