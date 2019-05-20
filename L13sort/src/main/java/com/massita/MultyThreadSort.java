package com.massita;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.IntStream;

public class MultyThreadSort {



    /**
     * Make MultiThread Sort of element
     *
     * @param inputList list of element
     * @param <T> type of elements
     * @return sorted list in Natural order
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static <T extends Comparable<? super T>> List<T> multiThreadSort(List<T> inputList) throws ExecutionException, InterruptedException {

        //Divide input array on 4 parts and sort each in thread

        //Improvement 1: divide input list by create view(for ArrayList)
        List<List<T>> subArraysForSort = divideList(inputList, 4);

        //Improvement 2: Use ThreadPool with fixed necessary thread count
        Executor executor = Executors.newFixedThreadPool(4);

        CompletableFuture<List<T>> threadOneSort = CompletableFuture.supplyAsync(() -> oneThreadSort(subArraysForSort.get(0)), executor);
        CompletableFuture<List<T>> threadSecondSort = CompletableFuture.supplyAsync(() -> oneThreadSort(subArraysForSort.get(1)), executor);
        CompletableFuture<List<T>> threadThirdSort = CompletableFuture.supplyAsync(() -> oneThreadSort(subArraysForSort.get(2)), executor);
        CompletableFuture<List<T>> threadFourthSort = CompletableFuture.supplyAsync(() -> oneThreadSort(subArraysForSort.get(3)), executor);

        //Merge sorted arrays
        CompletableFuture<List<T>> firstHalfSort = threadOneSort.thenCombineAsync(threadSecondSort, MultyThreadSort::oneThreadMergeSort, executor);
        CompletableFuture<List<T>> secondHalfSort = threadThirdSort.thenCombineAsync(threadFourthSort, MultyThreadSort::oneThreadMergeSort, executor);

        CompletableFuture<List<T>> result = firstHalfSort.thenCombineAsync(secondHalfSort, MultyThreadSort::oneThreadMergeSort, executor);

        return result.get();
    }

    /**
     * Make MultiThread Sort of element
     *
     * @param inputList list of unique element
     * @param <T> type of elements
     * @return sorted list in Natural order
     */

    public static <T extends Comparable<? super T>> List<T> multiThreadForkListSort(List<T> inputList) {
        ForkJoinSorter<T> task = new ForkJoinSorter(inputList);
        return new ForkJoinPool().invoke(task);
    }

    private static <T extends Comparable<? super T>> List<List<T>> divideList(List<T> inputList, int numOfPart) {

        List<List<T>> result = new ArrayList<>(numOfPart);

        //Fill each part with elements: for ArrayList it will be view, for LinkedList copy
        IntStream.range(0, numOfPart)
                .forEach((el) -> result.add(inputList.subList(el * inputList.size()/numOfPart, (el + 1)  * inputList.size() / numOfPart)));

        return result;
    }

    static <T extends Comparable<? super T>> List<T> oneThreadSort(List<T> inputList) {

        //Improvement 4: use standard sort algoritm
        inputList.sort(Comparator.naturalOrder());

        return inputList;
    }

    static <T extends Comparable<? super T>> List<T> oneThreadMergeSort(List<T> firstList, List<T> secondList) {

        //Improvement 3: set required array size during creation to prevent copy
        List<T> result = new ArrayList<>(firstList.size() + secondList.size());
        for (int i = 0, j = 0; i < firstList.size() || j < secondList.size(); ) {
            if (i == firstList.size()) {
                result.add(secondList.get(j++));
            } else if (j == secondList.size()) {
                result.add(firstList.get(i++));
            } else if (firstList.get(i).compareTo(secondList.get(j)) < 0) {
                result.add(firstList.get(i++));
            } else {
                result.add(secondList.get(j++));
            }
        }
        return result;
    }

}
