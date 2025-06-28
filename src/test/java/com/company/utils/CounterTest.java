package com.company.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

public class CounterTest {

    private Counter<String> counter;

    @BeforeEach
    void setup() {
        counter = new Counter<>();
    }

    @Test
    void add_singles() {
        // given
        String[] strings = {"1", "2", "3", "2", "3", "3"};

        // when
        for (String string : strings) {
            counter.add(string);
        }

        // then
        Assertions.assertEquals(1, counter.get("1"));
        Assertions.assertEquals(2, counter.get("2"));
        Assertions.assertEquals(3, counter.get("3"));
        Assertions.assertEquals(6, counter.sum());
        Assertions.assertFalse(counter.allZero());
    }

    @Test
    void add_array() {
        // given
        String[] strings = {"1", "2", "3", "2", "3", "3"};

        // when
        counter.add(strings);

        // then
        Assertions.assertEquals(1, counter.get("1"));
        Assertions.assertEquals(2, counter.get("2"));
        Assertions.assertEquals(3, counter.get("3"));
        Assertions.assertEquals(6, counter.sum());
        Assertions.assertFalse(counter.allZero());
    }

    @Test
    void add_list() {
        // given
        List<String> strings = List.of("1", "2", "3", "2", "3", "3");

        // when
        counter.add(strings);

        // then
        Assertions.assertEquals(1, counter.get("1"));
        Assertions.assertEquals(2, counter.get("2"));
        Assertions.assertEquals(3, counter.get("3"));
        Assertions.assertEquals(6, counter.sum());
        Assertions.assertFalse(counter.allZero());
    }

    @Test
    void create_array() {
        // given
        String[] strings = {"1", "2", "3", "2", "3", "3"};

        // when
        counter = new Counter<>(strings);

        // then
        Assertions.assertEquals(1, counter.get("1"));
        Assertions.assertEquals(2, counter.get("2"));
        Assertions.assertEquals(3, counter.get("3"));
        Assertions.assertEquals(6, counter.sum());
        Assertions.assertFalse(counter.allZero());
    }

    @Test
    void create_list() {
        // given
        List<String> strings = List.of("1", "2", "3", "2", "3", "3");

        // when
        counter = new Counter<>(strings);

        // then
        Assertions.assertEquals(1, counter.get("1"));
        Assertions.assertEquals(2, counter.get("2"));
        Assertions.assertEquals(3, counter.get("3"));
        Assertions.assertEquals(6, counter.sum());
        Assertions.assertFalse(counter.allZero());
    }

    private static Stream<Arguments> provideSubstract() {
        return Stream.of(
                Arguments.of(List.of(), List.of("1", "2", "1"), -2, -3, false),
                Arguments.of(List.of("1", "2", "3", "2", "3", "3"), List.of("1", "1"), -1, 4, false),
                Arguments.of(List.of("1", "1", "3", "2", "3", "3"), List.of("1", "3"), 1, 4, false),
                Arguments.of(List.of("1", "2", "3", "2", "3", "3"), List.of("1", "2", "3"), 0, 3, false),
                Arguments.of(List.of("1", "2", "3"), List.of("1", "2", "3"), 0, 0, true),
                Arguments.of(List.of("1", "2", "3"), List.of("1", "3", "3"), 0, 0, false),
                Arguments.of(List.of(), List.of(), 0, 0, true),
                Arguments.of(List.of("1"), List.of(), 1, 1, false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideSubstract")
    void substract_singles(List<String> initial, List<String> strings, int expected1, int expectedSum, boolean expectedZero) {
        // given
        counter = new Counter<>(initial);

        // when
        for (String string : strings) {
            counter.substract(string);
        }

        // then
        Assertions.assertEquals(expected1, counter.get("1"));
        Assertions.assertEquals(expectedSum, counter.sum());
        Assertions.assertEquals(expectedZero, counter.allZero());
    }

    @ParameterizedTest
    @MethodSource("provideSubstract")
    void substract_list(List<String> initial, List<String> strings, int expected1, int expectedSum, boolean expectedZero) {
        // given
        counter = new Counter<>(initial);

        // when
        counter.substract(strings);

        // then
        Assertions.assertEquals(expected1, counter.get("1"));
        Assertions.assertEquals(expectedSum, counter.sum());
        Assertions.assertEquals(expectedZero, counter.allZero());
    }
}
