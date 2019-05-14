package com.massita;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

public class MultyThreadSort {

    /**
     * Make MultiThread Sort of unique element
     *
     * @param inputList list of unique element
     * @param <T> type of elements
     * @return sorted list in Natural order
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static <T extends Comparable<? super T>> List<T> multiThreadSort(List<T> inputList) throws ExecutionException, InterruptedException {

        //Divide input array on 4 parts and sort each in thread
        CompletableFuture<List<T>> threadOneSort = CompletableFuture.supplyAsync(() -> oneThreadSort(inputList.subList(0, inputList.size() / 4 + 1)));
        CompletableFuture<List<T>> threadSecondSort = CompletableFuture.supplyAsync(() -> oneThreadSort(inputList.subList(inputList.size() / 4 + 1, inputList.size() / 2 + 1)));
        CompletableFuture<List<T>> threadThirdSort = CompletableFuture.supplyAsync(() -> oneThreadSort(inputList.subList(inputList.size() / 2 + 1, 3 * inputList.size() / 4 + 1)));
        CompletableFuture<List<T>> threadFourthSort = CompletableFuture.supplyAsync(() -> oneThreadSort(inputList.subList(3 * inputList.size() / 4 + 1, inputList.size())));

        //Merge sorted arrays
        CompletableFuture<List<T>> firstHalfSort = threadOneSort.thenCombineAsync(threadSecondSort, MultyThreadSort::oneThreadMergeSort);
        CompletableFuture<List<T>> secondHalfSort = threadThirdSort.thenCombineAsync(threadFourthSort, MultyThreadSort::oneThreadMergeSort);

        CompletableFuture<List<T>> result = firstHalfSort.thenCombineAsync(secondHalfSort, MultyThreadSort::oneThreadMergeSort);

        return result.get();
    }

    /**
     * Make MultiThread Sort of unique element
     *
     * @param inputList list of unique element
     * @param <T> type of elements
     * @return sorted list in Natural order
     */

    public static <T extends Comparable<? super T>> List<T> multiThreadForkListSort(List<T> inputList) {
        ForkJoinSorter<T> task = new ForkJoinSorter(inputList);
        return new ForkJoinPool().invoke(task);
    }

    static <T extends Comparable<? super T>> List<T> oneThreadSort(List<T> inputList) {

        TreeSet<T> sortedSet = new TreeSet<>(inputList);

        return new ArrayList<>(sortedSet);
    }

    static <T extends Comparable<? super T>> List<T> oneThreadMergeSort(List<T> firstList, List<T> secondList) {
        List<T> result = new ArrayList<>();
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
