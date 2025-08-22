package com.company.utils;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GrouperTest {

    private Grouper<String> grouper;

    @Test
    void test_getAllReturns_doNothing() {
        // given
        grouper = new Grouper<>(List.of("abc", "cde", "ah", "zyx"));

        // when
        HashSet<Character> actual = grouper.getAllReturns(s -> s.charAt(0), false);

        // then
        assertEquals(3, actual.size());
        assertTrue(actual.contains('a'));
        assertTrue(actual.contains('c'));
        assertTrue(actual.contains('z'));
    }

    @Test
    void test_getAllReturns_groupBy() {
        // given
        grouper = new Grouper<>(List.of("abc", "cde", "ah", "zbx"));

        // when
        grouper.groupBy(s -> s.charAt(1), false);
        HashSet<Character> actual = grouper.getAllReturns(s -> s.charAt(0), false);

        // then
        assertEquals(3, actual.size());
        assertTrue(actual.contains('a'));
        assertTrue(actual.contains('c'));
        assertTrue(actual.contains('z'));
    }

    @Test
    void test_getAllReturns_filter() {
        // given
        grouper = new Grouper<>(List.of("abc", "cbe", "ah", "zyx"));

        // when
        grouper.filterValue(s -> s.charAt(1), 'b');
        HashSet<Character> actual = grouper.getAllReturns(s -> s.charAt(0), false);

        // then
        assertEquals(2, actual.size());
        assertTrue(actual.contains('a'));
        assertTrue(actual.contains('c'));
    }

    @Test
    void test_groupBy_once() {
        // given
        grouper = new Grouper<>(List.of("abc", "cde", "ah", "zbx"));

        // when
        grouper.groupBy(s -> s.charAt(0), false);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(3, actual.size());
        // keys
        assertEquals(List.of('a'), actual.get(0).getKey());
        assertEquals(List.of('c'), actual.get(1).getKey());
        assertEquals(List.of('z'), actual.get(2).getKey());
        // values
        assertEquals(2, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abc", "ah")));
        assertEquals(1, actual.get(1).getValue().size());
        assertTrue(actual.get(1).getValue().containsAll(List.of("cde")));
        assertEquals(1, actual.get(2).getValue().size());
        assertTrue(actual.get(2).getValue().containsAll(List.of("zbx")));
    }

    @Test
    void test_groupBy_multiple() {
        // given
        grouper = new Grouper<>(List.of("abc", "bcd", "bbb", "ade", "xyz", "ada"));

        // when
        grouper.groupBy(s -> s.charAt(0), false);
        grouper.groupBy(s -> s.charAt(1), false);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(5, actual.size());
        // keys
        assertEquals(List.of('a', 'b'), actual.get(0).getKey());
        assertEquals(List.of('a', 'd'), actual.get(1).getKey());
        assertEquals(List.of('b', 'b'), actual.get(2).getKey());
        assertEquals(List.of('b', 'c'), actual.get(3).getKey());
        assertEquals(List.of('x', 'y'), actual.get(4).getKey());
        // values
        assertEquals(1, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abc")));
        assertEquals(2, actual.get(1).getValue().size());
        assertTrue(actual.get(1).getValue().containsAll(List.of("ade", "ada")));
        assertEquals(1, actual.get(2).getValue().size());
        assertTrue(actual.get(2).getValue().containsAll(List.of("bbb")));
        assertEquals(1, actual.get(3).getValue().size());
        assertTrue(actual.get(3).getValue().containsAll(List.of("bcd")));
        assertEquals(1, actual.get(4).getValue().size());
        assertTrue(actual.get(4).getValue().containsAll(List.of("xyz")));
    }

    @Test
    void test_groupBy_ignoreNulls() {
        // given
        grouper = new Grouper<>(List.of("abc", "a", "ab", "xyz", "f", "ff"));

        // when
        grouper.groupBy(s -> s.length() > 1 ? s.charAt(1) : null, true);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(3, actual.size());
        // keys
        assertEquals(List.of('b'), actual.get(0).getKey());
        assertEquals(List.of('f'), actual.get(1).getKey());
        assertEquals(List.of('y'), actual.get(2).getKey());
        // values
        assertEquals(2, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abc", "ab")));
        assertEquals(1, actual.get(1).getValue().size());
        assertTrue(actual.get(1).getValue().containsAll(List.of("ff")));
        assertEquals(1, actual.get(2).getValue().size());
        assertTrue(actual.get(2).getValue().containsAll(List.of("xyz")));
    }

    @Test
    void test_filterValue_once() {
        // given
        grouper = new Grouper<>(List.of("abc", "a", "ab", "baa"));

        // when
        grouper.filterValue(s -> s.charAt(0), 'a');
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(1, actual.size());
        // keys
        assertEquals(List.of(), actual.get(0).getKey());
        // values
        assertEquals(3, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("a", "ab", "abc")));
    }

    @Test
    void test_filterValue_multiple() {
        // given
        grouper = new Grouper<>(List.of("abc", "aa", "ab", "baa", "bbb"));

        // when
        grouper.filterValue(s -> s.charAt(0), 'a');
        grouper.filterValue(s -> s.charAt(1), 'b');
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(1, actual.size());
        // keys
        assertEquals(List.of(), actual.get(0).getKey());
        // values
        assertEquals(2, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("ab", "abc")));
    }

    @Test
    void test_filterBool_once() {
        // given
        grouper = new Grouper<>(List.of("abcd", "", "aa", "x", "yxy", "hg"));

        // when
        grouper.filterBool(s -> s.length() > 2);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(1, actual.size());
        // keys
        assertEquals(List.of(), actual.get(0).getKey());
        // values
        assertEquals(2, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abcd", "yxy")));
    }

    @Test
    void test_filterBool_multiple() {
        // given
        grouper = new Grouper<>(List.of("abcd", "abcde", "aa", "baxaxaxa", "axaxaxa", "bbbxxxxxxxyyyxxxxxxxxxx"));

        // when
        grouper.filterBool(s -> s.length() < 8);
        grouper.filterBool(s -> s.length() > 4);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(1, actual.size());
        // keys
        assertEquals(List.of(), actual.get(0).getKey());
        // values
        assertEquals(2, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abcde", "axaxaxa")));
    }

    @Test
    void test_groupAndFilter() {
        // given
        grouper = new Grouper<>(List.of("abcd", "abcde", "aa", "ya", "yb", "ybbbbbbb", "bbb", "bbbbbbbb", "llll"));

        // when
        grouper.groupBy(s -> s.charAt(0), false);
        grouper.filterValue(s -> s.charAt(1), 'b');
        grouper.filterBool(s -> s.length() < 5);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(3, actual.size());
        // keys
        assertEquals(List.of('a'), actual.get(0).getKey());
        assertEquals(List.of('b'), actual.get(1).getKey());
        assertEquals(List.of('y'), actual.get(2).getKey());
        // values
        assertEquals(1, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abcd")));
        assertEquals(1, actual.get(1).getValue().size());
        assertTrue(actual.get(1).getValue().containsAll(List.of("bbb")));
        assertEquals(1, actual.get(2).getValue().size());
        assertTrue(actual.get(2).getValue().containsAll(List.of("yb")));
    }

    @Test
    void test_filterAndGroup() {
        // given
        grouper = new Grouper<>(List.of("abcd", "abcde", "aa", "ya", "yb", "ybbbbbbb", "bbb", "bbbbbbbb", "llll"));

        // when
        grouper.filterValue(s -> s.charAt(1), 'b');
        grouper.groupBy(s -> s.charAt(0), false);
        grouper.filterBool(s -> s.length() < 5);
        ArrayList<Map.Entry<List<?>, List<String>>> actual = new ArrayList<>(grouper.entrySet());

        // then
        assertEquals(3, actual.size());
        // keys
        assertEquals(List.of('a'), actual.get(0).getKey());
        assertEquals(List.of('b'), actual.get(1).getKey());
        assertEquals(List.of('y'), actual.get(2).getKey());
        // values
        assertEquals(1, actual.get(0).getValue().size());
        assertTrue(actual.get(0).getValue().containsAll(List.of("abcd")));
        assertEquals(1, actual.get(1).getValue().size());
        assertTrue(actual.get(1).getValue().containsAll(List.of("bbb")));
        assertEquals(1, actual.get(2).getValue().size());
        assertTrue(actual.get(2).getValue().containsAll(List.of("yb")));
    }

    @Test
    @Disabled
    void test_unsupportedOperations() {
        // given
        grouper = new Grouper<>(List.of("a"));

        // then
        assertThrows(UnsupportedOperationException.class, () -> grouper.put(List.of(10), List.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.putAll(new HashMap<>()));
        assertThrows(UnsupportedOperationException.class, () -> grouper.putIfAbsent(List.of(10), List.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.putFirst(List.of(10), List.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.putLast(List.of(10), List.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.clear());
        assertThrows(UnsupportedOperationException.class, () -> grouper.compute(List.of(), (k, v) -> v));
        assertThrows(UnsupportedOperationException.class, () -> grouper.computeIfAbsent(List.of(), (k) -> List.of(k.toString())));
        assertThrows(UnsupportedOperationException.class, () -> grouper.computeIfPresent(List.of(), (k, v) -> v));
        assertThrows(UnsupportedOperationException.class, () -> grouper.merge(List.of(), List.of("b"), (k, v) -> v));
        assertThrows(UnsupportedOperationException.class, () -> grouper.remove(List.of()));
        assertThrows(UnsupportedOperationException.class, () -> grouper.remove(List.of(), List.of("a")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.replace(List.of(), List.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.replace(List.of(), List.of("a"), List.of("b")));
        assertThrows(UnsupportedOperationException.class, () -> grouper.replaceAll((k, v) -> v));
        assertThrows(UnsupportedOperationException.class, () -> grouper.reversed());
    }

}
