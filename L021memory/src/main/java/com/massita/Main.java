package com.massita;

import java.util.ArrayList;
import java.util.LinkedList;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        MemoryWeighter weighter = new MemoryWeighter(10_000_000);
        ObjectFactory<String, ArrayList> stringFactory = new ObjectFactory<>(String.class, ArrayList.class);
        System.out.println("String size = " + weighter.weightObject(stringFactory));
        //System.out.println("String size by Instrumentation= " + weighter.weightObjectByInstrumentation(stringFactory));
        for (int i = 0; i < 5; i++) {
            System.out.println("ArrayList of " + i + " Strings size = " + weighter.weightCollection(stringFactory, i));
        }

        ObjectFactory<Object, LinkedList> objectFactory = new ObjectFactory<>(Object.class, LinkedList.class);
        System.out.println("Object size = " + weighter.weightObject(objectFactory));
        System.out.println("Object size by Instrumentation= " + weighter.weightObjectByInstrumentation(objectFactory));
        for (int i = 0; i < 5; i++) {
            System.out.println("LinkedList of " + i + " Objects size = " + weighter.weightCollection(objectFactory, i));

        }

    }
}
