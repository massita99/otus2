package com.massita;

import java.lang.instrument.Instrumentation;

public class MemoryWeighter {

    private final int numOfWeights;
    private static volatile Instrumentation instrumentation;

    public MemoryWeighter(int numOfWeghts) {
        this.numOfWeights = numOfWeghts;
    }

    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }


    public long weightObject(ObjectFactory factory) throws InterruptedException {
        Object[] array = new Object[numOfWeights];
        long memoryAtStart = getMem();

        for (int i = 0; i < numOfWeights; i++) {
            array[i] = factory.createSimpleObject();
        }

        long memoryAtFinish = getMem();
        int fake = array.length;
        return (memoryAtFinish - memoryAtStart) / numOfWeights;
    }

    public long weightObjectByInstrumentation(ObjectFactory factory) throws InterruptedException {
        return instrumentation.getObjectSize(factory.createSimpleObject());
    }

    public long weightCollection(ObjectFactory factory, int numOfElements) throws InterruptedException {
        Object[] array = new Object[numOfWeights];
        long memoryAtStart = getMem();

        for (int i = 0; i < numOfWeights; i++) {
            array[i] = factory.createSimpleCollectionWithElements(numOfElements);
        }

        long memoryAtFinish = getMem();

        int fake = array.length;
        return (memoryAtFinish - memoryAtStart) / numOfWeights;
    }

    private static long getMem() throws InterruptedException {
        System.gc();
        Thread.sleep(10);
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }
}
