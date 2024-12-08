package ru.ac.uniyar.artgallery;

import java.util.ArrayList;
import java.util.List;

public class CopyOnWriteUtils {
    public static <T> List<T> addToList(List<T> list, List<T> addedObjects) {
        List<T> newList = new ArrayList<>(list);
        newList.addAll(addedObjects);
        return newList;
    }

    public static <T> List<T> removeFromList(List<T> list, List<T> removedObjects) {
        List<T> newList = new ArrayList<>(list);
        newList.removeAll(removedObjects);
        return newList;
    }
}
