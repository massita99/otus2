package com.massita;

import java.util.*;
import java.util.stream.Collectors;

public class MyArrayList<E> implements List<E> {

    private Object[] internalStorage;
    private int currentSize;
    public static int DEFAULT_SIZE = 10;



    public MyArrayList() {
        this.internalStorage = new Object[DEFAULT_SIZE];
        this.currentSize = 0;
    }

    @Override
    public int size() {
        return currentSize;
    }

    @Override
    public boolean isEmpty() {
        return (currentSize == 0);
    }

    @Override
    public boolean contains(Object o) {
        for (int i = 0; i < currentSize; i++) {
            if (internalStorage[i] == o) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            int currentIteratorPosition = 0;

            @Override
            public boolean hasNext() {
                return currentSize > currentIteratorPosition;
            }

            @Override
            public E next() {
                return (E) internalStorage[currentIteratorPosition++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        return Arrays.stream(internalStorage)
                .limit(currentSize)
                .toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return Arrays.stream(internalStorage)
                .limit((currentSize))
                .collect(Collectors.toList())
                .toArray(a);
    }

    @Override
    public boolean add(E e) {
        if (isNeedToExpandStorage()) {
            expandArray();
        }
        internalStorage[currentSize++] = e;
        return true;
    }

    private boolean isNeedToExpandStorage() {
        return (currentSize+1 == internalStorage.length);
    }

    private void expandArray() {
        Object[] newInternalStorage = Arrays.copyOf(internalStorage, currentSize*2);
        internalStorage = newInternalStorage;
    }

    @Override
    public boolean remove(Object o) {
        for (int i = 0; i < currentSize; i++) {
            if (internalStorage[i] == o) {
                internalStorage[i] = null;
                currentSize--;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        //not implemented yet
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public void clear() {
        currentSize = 0;
    }

    @Override
    public E get(int index) {
        checkIndexInBound(index);
        return (E) internalStorage[index];
    }

    private void checkIndexInBound(int index) {
        if (index >= currentSize) {
            throw new IndexOutOfBoundsException();
        }
    }

    @Override
    public E set(int index, E element) {
        checkIndexInBound(index);
        E currentElement = (E) internalStorage[index];
        internalStorage[index] = element;
        return currentElement;
    }

    @Override
    public void add(int index, E element) {
        //not implemented yet
    }

    @Override
    public E remove(int index) {
        //not implemented yet
        return null;
    }

    @Override
    public int indexOf(Object o) {
        //not implemented yet
        return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
        //not implemented yet
        return 0;
    }

    @Override
    public ListIterator<E> listIterator() {

        return (ListIterator<E>) Arrays.asList(internalStorage).stream()
                .limit(currentSize)
                .collect(Collectors.toList())
                .listIterator();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        //not implemented yet
        return null;
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        //not implemented yet
        return null;
    }
}
