package com.company.utils;

import java.util.HashMap;
import java.util.function.BiConsumer;

public class Counter<T> extends HashMap<T, Integer> {

    public Counter() {
        super();
    }

    public Counter(T[] array) {
        super(array.length);
        for (T t : array) {
            this.add(t);
        }
    }

    public Counter(Iterable<T> iterable) {
        super();
        iterable.forEach(this::add);
    }

    @Override
    public Integer get(Object key) {
        return getOrDefault(key, 0);
    }

    public void add(T t) {
        this.put(t, this.getOrDefault(t, 0) + 1);
    }

    public void add(T[] array) {
        for (T t : array) {
            add(t);
        }
    }

    public void add(Iterable<T> iterable) {
        iterable.forEach(this::add);
    }

    public void add(T t, int amount) {
        this.put(t, this.getOrDefault(t, 0) + amount);
    }

    public void substract(T t) {
        this.put(t, this.getOrDefault(t, 0) - 1);
    }

    public void substract(T[] array) {
        for (T t : array) {
            substract(t);
        }
    }

    public void substract(Iterable<T> iterable) {
        iterable.forEach(this::substract);
    }

    public boolean allZero() {
        for (T t : this.keySet()) {
            if (this.get(t) != 0) {
                return false;
            }
        }
        return true;
    }

    public int sum() {
        int sum = 0;
        for (T t : this.keySet()) {
            sum += this.get(t);
        }
        return sum;
    }

    public void forEachNonZero(BiConsumer<T, Integer> consumer) {
        forEach((t, i) -> {
            if (i != 0) consumer.accept(t, i);
        });
    }

}
