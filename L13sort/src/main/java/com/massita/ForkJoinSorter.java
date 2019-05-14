package com.massita;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.RecursiveTask;

import static com.massita.MultyThreadSort.oneThreadMergeSort;
import static com.massita.MultyThreadSort.oneThreadSort;

@RequiredArgsConstructor
public class ForkJoinSorter<T extends Comparable<? super T>>  extends RecursiveTask<List<T>> {

    public static final long THRESHOLD = 1000;

    private final List<T> input;

    @Override
    protected List<T> compute() {
        if (input.size()<THRESHOLD) {
            return oneThreadSort(input);
        }
        int mediumElementIndex = input.size() / 2;
        ForkJoinSorter<T> leftTask = new ForkJoinSorter(input.subList(0 , mediumElementIndex));
        leftTask.fork();
        ForkJoinSorter<T> rightTask = new ForkJoinSorter<>(input.subList(mediumElementIndex, input.size()));

        List<T> rightSideArray = rightTask.compute();
        List<T> leftSideArray = leftTask.join();


        return oneThreadMergeSort(leftSideArray, rightSideArray);
    }
}