package com.massita;

import java.util.Collection;

public class ObjectFactory<E, C extends Collection> {

    private Class<E> elementClazz;
    private Class<C> collectionClazz;


    public ObjectFactory(Class<E> elementClazz, Class<C> collectionClazz) {
        this.elementClazz = elementClazz;
        this.collectionClazz = collectionClazz;
    }

    public E createSimpleObject() {
        try {
            return elementClazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    @SuppressWarnings("unchecked")
    public C createSimpleCollectionWithElements(int numberOfElements) {
        try {
            C collection = collectionClazz.newInstance();
            for (int i = 0; i < numberOfElements; i++) {
                collection.add(elementClazz.newInstance());
            }
            return collection;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
