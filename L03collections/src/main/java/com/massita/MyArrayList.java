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
                removeFromInternalByIndex(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        c.forEach(this::add);
        return true;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
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
        checkIndexInBound(index);
        addElementToInternalByIndex(index, element);

    }

    private void addElementToInternalByIndex(int index, E element) {
        if (isNeedToExpandStorage()) {
            expandArray();
        }
        if (index == currentSize - 1) {
            internalStorage[currentSize++] = element;
        } else {
            Object[] newInternalArray = new Object[internalStorage.length];
            for (int i = 0; i < currentSize; i++) {
                if (i < index) {
                    newInternalArray[i] = internalStorage[i];
                } else if ( i == index) {
                    newInternalArray[i] = element;
                } else {
                    newInternalArray[i] = internalStorage[i-1];
                }
            }
            currentSize++;
            internalStorage = newInternalArray;
        }
    }

    @Override
    public E remove(int index) {
        checkIndexInBound(index);
        E result = removeFromInternalByIndex(index);
        return result;
    }

    private E removeFromInternalByIndex(int index) {
        E result = (E) internalStorage[index];
        if (index == currentSize - 1) {
            currentSize--;
        } else {

            internalStorage[index] = null;
            Object[] newInternalArray = Arrays.stream(internalStorage).filter(el -> el != null).toArray();
            internalStorage = Arrays.copyOf(newInternalArray, internalStorage.length);
        }
        return result;
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();

    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();

    }

    @Override
    public ListIterator<E> listIterator() {



        return new ListIterator<E>() {

            int currentIteratorPosition = -1;
            @Override
            public boolean hasNext() {
                return currentSize > currentIteratorPosition;
            }

            @Override
            public E next() {
                return (E) internalStorage[++currentIteratorPosition];
            }

            @Override
            public boolean hasPrevious() {
                return currentIteratorPosition > 0;
            }

            @Override
            public E previous() {
                return (E) internalStorage[--currentIteratorPosition];
            }

            @Override
            public int nextIndex() {
                return currentIteratorPosition + 1;
            }

            @Override
            public int previousIndex() {
                return currentIteratorPosition - 1;
            }

            @Override
            public void remove() {
                removeFromInternalByIndex(currentIteratorPosition);
            }

            @Override
            public void set(E e) {
                internalStorage[currentIteratorPosition] = e;
            }

            @Override
            public void add(E e) {
                addElementToInternalByIndex(currentIteratorPosition, e);
            }
        };
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();

    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        throw new UnsupportedOperationException();

}
}
