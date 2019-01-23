package com.massita;

import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class MyArrayListTest {

    private List<Integer> intTestList;
    private List<Object> objTestList;

    @Before
    public void init() {
        intTestList = new MyArrayList<>();
        objTestList = new MyArrayList<>();
    }

    @Test
    public void canCreateList() {
        //Given


        //Then
        assertThat("List empty",intTestList.isEmpty());
    }

    @Test
    public void canAddElement() {
        //Given
        intTestList.add(5);
        //Then
        assertThat(1, is(intTestList.size()));
    }

    @Test
    public void canRemoveElement() {
        Integer element = Integer.valueOf(5);
        intTestList.add(element);
        intTestList.remove(element);

        assertThat(0, is(intTestList.size()));

    }

    @Test
    public void canGetAddedElement() {
        intTestList.add(7);
        intTestList.add(6);
        assertThat(7,is(intTestList.get(0)));
        assertThat(6,is(not(intTestList.get(0))));
        assertThat(6,is(intTestList.get(1)));

    }

    @Rule
    public ExpectedException exceptionGrabber = ExpectedException.none();

    @Test
    public void getOutOutOfBoundsException (){
        intTestList.add(1);
        exceptionGrabber.expect(IndexOutOfBoundsException.class);
        intTestList.get(2);
    }

    @Test
    public void removeObjectTest() {
        Object firstElement = new Object();
        Object secondElement = new Object();

        objTestList.add(firstElement);

        assertThat("Can remove added object", objTestList.remove(firstElement));
        assertThat(Boolean.FALSE, is(objTestList.remove(secondElement)));

    }
    @Test
    public void containtTest() {
        Object firstElement = new Object();
        Object secondElement = new Object();
        objTestList.add(firstElement);

        assertThat("Contain added object", objTestList.contains(firstElement));
        assertThat(Boolean.FALSE, is(objTestList.contains(secondElement)));

    }

    @Test
    public void testIterator() {
        intTestList.add(1);
        intTestList.add(2);
        intTestList.add(3);

        Iterator<Integer> testIterator = intTestList.iterator();

        assertThat("Has first element", testIterator.hasNext());
        assertThat(1, is(testIterator.next()));
        assertThat(2, is(testIterator.next()));
        assertThat(3, is(testIterator.next()));
        assertThat(Boolean.FALSE, is(testIterator.hasNext()));
    }

    @Test
    public void testToArray() {
        intTestList.add(1);
        intTestList.add(2);

        Object[] expectedResultObj = {1, 2};
        Integer[] expectedResultInt = {1 ,2};
        assertThat(expectedResultObj, is(intTestList.toArray()));
        assertThat(expectedResultInt, is(intTestList.toArray(new Integer[0])));
    }

    @Test
    public void addAllTest() {
        List<Integer> testList = ImmutableList.of(1,2);
        //intTestList.addAll(testList);
        Collections.addAll(intTestList, 1, 2);

        assertThat(testList.toArray(), is(intTestList.toArray()));
    }

    @Test
    public void sortTest() {
        List<Integer> testUnsortedList = ImmutableList.of(3,5,1);
        List<Integer> testSortedList = ImmutableList.of(1,3,5);

        intTestList.addAll(testSortedList);

        Collections.sort(intTestList, Comparator.naturalOrder());
        assertThat(testSortedList.toArray(), is(intTestList.toArray()));


    }

    @Test
    public void copyTest() {
        intTestList.add(1);
        intTestList.add(2);

        List<Integer> copyOfIntTestList = new MyArrayList<>();
        copyOfIntTestList.add(5);
        copyOfIntTestList.add(6);

        Collections.copy(copyOfIntTestList, intTestList);

        assertThat(1, is(copyOfIntTestList.get(0)));
    }

    @Test
    public void expandTest() {
        Collections.addAll(intTestList, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        assertThat(12, is(intTestList.size()));
    }

}
