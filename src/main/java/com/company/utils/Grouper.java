package com.company.utils;

import lombok.Getter;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class Grouper<T> extends LinkedHashMap<List<?>, List<T>> {

    private List<T> all;

    @Getter
    private final List<Function<T, ?>> groupings;

    public Grouper(List<T> list) {
        super(ofEntries(entry(List.of(), new ArrayList<>(list))));
        all = super.get(List.of());
        groupings = new LinkedList<>();
    }

    public <R> HashSet<R> getAllReturns(Function<T, R> method, boolean filterNulls) {
        return getReturns(method, all, filterNulls);
    }

    private static <T, R> HashSet<R> getReturns(Function<T, R> method, List<T> list, boolean filterNulls) {
        HashSet<R> set = list.stream().map(method).collect(Collectors.toCollection(HashSet::new));
        if (filterNulls) set.remove(null);
        return set;
    }

    // TODO: ensure R is Comparable or fallback if it's not
    public <R> void groupBy(Function<T, R> method, boolean filterNulls) {
        ArrayList<Map.Entry<List<?>, List<T>>> entries = new ArrayList<>(entrySet());
        entries.forEach(e -> {
            getReturns(method, e.getValue(), filterNulls).stream().sorted().forEach(r -> {
                List<Object> newKey = new ArrayList<>(e.getKey());
                newKey.add(r);
                newKey = Collections.unmodifiableList(newKey);
                List<T> newValue = e.getValue().stream()
                        .filter(t -> r.equals(method.apply(t)))
                        .collect(Collectors.toList());
                if (newValue.size() > 0) {
                    super.put(newKey, newValue);
                }
            });
            super.remove(e.getKey());
        });

        groupings.add(method);
        if (filterNulls) all = all.stream().filter(t -> method.apply(t) != null).collect(Collectors.toList());
    }

    public <R> void filterValue(Function<T, R> method, R value) {
        filter(t -> value.equals(method.apply(t)));
    }

    public void filterBool(Predicate<T> method) {
        filter(method);
    }

    private void filter(Predicate<T> method) {
        all = all.stream().filter(method).collect(Collectors.toCollection(LinkedList::new));
        new ArrayList<>(keySet()).forEach(l -> {
            List<T> lt = get(l);
            lt.retainAll(all);
            if (lt.size() == 0) {
                super.remove(l);
            }
        });
    }

    //------------------------------------------------------------------------------------------------------------------
    // Unsupported operations:

    /*
    @Override
    public List<T> putFirst(List<?> objects, List<T> ts) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> putLast(List<?> objects, List<T> ts) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<List<?>, List<T>> eldest) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public void replaceAll(BiFunction<? super List<?>, ? super List<T>, ? extends List<T>> function) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public SequencedMap<List<?>, List<T>> reversed() {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> put(List<?> key, List<T> value) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public void putAll(Map<? extends List<?>, ? extends List<T>> m) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> remove(Object key) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> putIfAbsent(List<?> key, List<T> value) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public boolean remove(Object key, Object value) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public boolean replace(List<?> key, List<T> oldValue, List<T> newValue) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> replace(List<?> key, List<T> value) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> computeIfAbsent(List<?> key, Function<? super List<?>, ? extends List<T>> mappingFunction) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> computeIfPresent(List<?> key, BiFunction<? super List<?>, ? super List<T>, ? extends List<T>> remappingFunction) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> compute(List<?> key, BiFunction<? super List<?>, ? super List<T>, ? extends List<T>> remappingFunction) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }

    @Override
    public List<T> merge(List<?> key, List<T> value, BiFunction<? super List<T>, ? super List<T>, ? extends List<T>> remappingFunction) {
        throw new UnsupportedOperationException("A grouper should not be modified using this method");
    }
    */
}
