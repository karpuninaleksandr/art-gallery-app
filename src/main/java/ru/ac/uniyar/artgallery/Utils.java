package ru.ac.uniyar.artgallery;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

public class Utils {
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

    public static <T> List<T> filter(List<T> list, Predicate<T> condition) {
        List<T> result = new ArrayList<>();
        list.forEach(it -> {
            if (condition.test(it))
                result.add(it);
        });
        return result;
    }

    public static <T> T reduce(List<T> list, T init, BiFunction<T, T, T> function) {
        T result = init;
        for (T it : list) {
            result = function.apply(result, it);
        }
        return result;
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> function) {
        List<R> result = new ArrayList<>();
        list.forEach(it -> {
            result.add(function.apply(it));
        });
        return result;
    }
}
