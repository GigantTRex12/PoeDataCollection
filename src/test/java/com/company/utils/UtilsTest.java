package com.company.utils;

import com.company.datasets.datasets.BossDropDataSet;
import com.company.datasets.datasets.DataSet;
import com.company.datasets.other.loot.GemLoot;
import com.company.datasets.other.loot.Loot;
import com.company.datasets.other.loot.LootType;
import com.company.datasets.other.loot.StackableLoot;
import com.company.datasets.other.metadata.Strategy;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.company.datasets.other.loot.LootType.*;
import static java.util.Map.entry;
import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    // ----------------------------------------------------------------------------------------------------------------
    // JOIN
    // ----------------------------------------------------------------------------------------------------------------

    private static Stream<Arguments> joinListProviderNoSep() {
        return Stream.of(
                Arguments.of(List.of("aaa", "bb", "cccc"), "aaabbcccc"),
                Arguments.of(List.of(), ""),
                Arguments.of(List.of("42"), "42"),
                Arguments.of(List.of("1", "", "1"), "11")
        );
    }

    private static Stream<Arguments> joinListProviderSep() {
        return Stream.of(
                Arguments.of(List.of("aaa", "bb", "cccc"), "aaa+bb+cccc"),
                Arguments.of(List.of(), ""),
                Arguments.of(List.of("42"), "42"),
                Arguments.of(List.of("1", "", "1"), "1++1")
        );
    }

    private static Stream<Arguments> joinListProviderStart() {
        return Stream.of(
                Arguments.of(List.of("aaa", "bb", "cccc"), 0, "aaa+bb+cccc"),
                Arguments.of(List.of(), 10, ""),
                Arguments.of(List.of("42"), 3, ""),
                Arguments.of(List.of("1", "", "1"), 1, "+1"),
                Arguments.of(List.of("aaa", "bb", "cccc"), 2, "cccc"),
                Arguments.of(List.of("aaa", "bb", "cccc", "d"), 1, "bb+cccc+d"),
                Arguments.of(List.of("1", "", ""), 1, "+")
        );
    }

    @ParameterizedTest
    @MethodSource("joinListProviderNoSep")
    void test_joinArrayNoSep(List<String> strings, String expected) {
        // given
        String[] stringsArr = strings.toArray(new String[0]);

        // when
        String actual = Utils.join(stringsArr);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("joinListProviderSep")
    void test_joinArraySep(List<String> strings, String expected) {
        // given
        String[] stringsArr = strings.toArray(new String[0]);

        // when
        String actual = Utils.join(stringsArr, "+");

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("joinListProviderNoSep")
    void test_joinListNoSep(List<String> strings, String expected) {
        // when
        String actual = Utils.join(strings);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("joinListProviderSep")
    void test_joinListSep(List<String> strings, String expected) {
        // when
        String actual = Utils.join(strings, "+");

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("joinListProviderStart")
    void test_joinArraySepStart(List<String> strings, int start, String expected) {
        // given
        String[] stringsArr = strings.toArray(new String[0]);

        // when
        String actual = Utils.join(stringsArr, "+", start);

        // then
        assertEquals(expected, actual);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // CONTAINS
    // ----------------------------------------------------------------------------------------------------------------

    private static Stream<Arguments> containsListProvider() {
        return Stream.of(
                Arguments.of(List.of("ab", "bc", "gh"), "ab", true),
                Arguments.of(List.of("ab", "bc", "gh"), "AB", true),
                Arguments.of(List.of("AB", "bc", "gh"), "ab", true),
                Arguments.of(List.of("ab", "bc", "gh"), "ba", false),
                Arguments.of(List.of(), "ab", false),
                Arguments.of(List.of("ab", "bc", "gh"), "zz", false),
                Arguments.of(List.of("ab", "bc", "gh"), "g", false)
        );
    }

    private static Stream<Arguments> containsMapProvider() {
        return Stream.of(
                Arguments.of(Map.ofEntries(entry("ab", "cd"), entry("ef", "gh")), "ab", true),
                Arguments.of(Map.ofEntries(entry("ab", "cd"), entry("ef", "gh")), "cd", true),
                Arguments.of(Map.ofEntries(entry("ab", "cd"), entry("ef", "gh")), "Ab", true),
                Arguments.of(Map.ofEntries(entry("ab", "cd"), entry("ef", "gh")), "gH", true),
                Arguments.of(Map.ofEntries(entry("ab", "cd"), entry("ef", "gh")), "ij", false),
                Arguments.of(Map.ofEntries(entry("ab", "cd"), entry("ef", "gh")), "a", false),
                Arguments.of(Map.ofEntries(), "ab", false)
        );
    }

    private static Stream<Arguments> containsLootTypeListProvider() {
        return Stream.of(
                Arguments.of(List.of(MAP, T17_MAP, CURRENCY), CURRENCY, true),
                Arguments.of(List.of(MAP, T17_MAP, CURRENCY), MAP, true),
                Arguments.of(List.of(MAP, T17_MAP, CURRENCY), CONQUEROR_MAP, false),
                Arguments.of(List.of(MAP, T17_MAP, CURRENCY), CATALYSTS, false),
                Arguments.of(List.of(), CURRENCY, false),
                Arguments.of(List.of(CATALYSTS), CURRENCY, false),
                Arguments.of(List.of(CURRENCY), CURRENCY, true)
        );
    }

    @ParameterizedTest
    @MethodSource("containsListProvider")
    void test_containsArray(List<String> strings, String option, boolean expected) {
        // given
        String[] stringsArr = strings.toArray(new String[0]);

        // when
        boolean actual = Utils.contains(stringsArr, option);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("containsListProvider")
    void test_containsList(List<String> strings, String option, boolean expected) {
        // when
        boolean actual = Utils.contains(strings, option);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("containsLootTypeListProvider")
    void test_containsLootTypes(List<LootType> types, LootType option, boolean expected) {
        // given
        LootType[] typeArr = types.toArray(new LootType[0]);

        // when
        boolean actual = Utils.contains(typeArr, option);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @MethodSource("containsMapProvider")
    void test_containsMap(Map<String, String> map, String option, boolean expected) {
        // when
        boolean actual = Utils.contains(map, option);

        // then
        assertEquals(expected, actual);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // JSON
    // ----------------------------------------------------------------------------------------------------------------

    private static Stream<Arguments> toJsonProvider() {
        Strategy exampleStrat = new Strategy(null, "3.27", null, null, List.of("S1", "S2").toArray(new String[0]), "Mesa", "Alch&Go", "");
        String stratJson = "{\"league\":\"3.27\",\"scarabs\":[\"S1\",\"S2\"],\"map\":\"Mesa\",\"mapRolling\":\"Alch&Go\",\"mapCraft\":\"\"}";
        return Stream.of(
                Arguments.of(exampleStrat, stratJson),
                Arguments.of(
                        new BossDropDataSet(
                                exampleStrat, "Maven", false, false, new Loot("Arn's Anguish", BOSS_UNIQUE_ITEM),
                                List.of(new GemLoot("Awakened WET", GEM, 0, 3), new StackableLoot("Orb of Conflict", CURRENCY, 1)), null
                        ),
                        "{\"strategy\":" + stratJson + ",\"boss\":\"Maven\",\"uber\":false,\"witnessed\":false," +
                                "\"guaranteedDrop\":{\"name\":\"Arn's Anguish\",\"type\":\"BOSS_UNIQUE_ITEM\"}," +
                                "\"extraDrops\":[" +
                                "{\"name\":\"Awakened WET\",\"type\":\"GEM\",\"level\":0,\"quality\":3}," +
                                "{\"name\":\"Orb of Conflict\",\"type\":\"CURRENCY\",\"stacksize\":1}" +
                                "]}"
                ),
                Arguments.of(
                        new GemLoot("Snipe", GEM, 1, 10),
                        "{\"name\":\"Snipe\",\"type\":\"GEM\",\"level\":1,\"quality\":10}"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("toJsonProvider")
    void test_toJson(Object object, String expected) {
        // when
        String actual = Utils.toJson(object);

        // then
        assertEquals(expected, actual);
    }

    // This also technically tests LootDeserializer but only to some degree

    @ParameterizedTest
    @MethodSource("toJsonProvider")
    void test_parseJson(Object expected, String json) throws JsonProcessingException {
        // when
        Object actual = Utils.parseJson(json, expected.getClass());

        // then
        assertEquals(expected, actual);
    }

    // ----------------------------------------------------------------------------------------------------------------
    // OTHER
    // ----------------------------------------------------------------------------------------------------------------

    private static Stream<Arguments> splitToCharsProvider() {
        return Stream.of(
                Arguments.of("ab/c/de", '/', new char[]{'a', 'b', 'c', 'd', 'e'}),
                Arguments.of("ab//de", '/', new char[]{'a', 'b', 'd', 'e'}),
                Arguments.of("//", '/', new char[0])
        );
    }

    @ParameterizedTest
    @MethodSource("splitToCharsProvider")
    void test_splitToChars(String inp, char sep, char[] expected) {
        // when
        char[] actual = Utils.splitToChars(inp, sep);

        // then
        assertArrayEquals(expected, actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "4.2", "0.75", "55.0", ".89", "+44", "+33.22", "+.76", "-90", "-20.20", "-.666", "0",
            "00", "-0"})
    void test_isNumberTrue(String num) {
        // when
        boolean actual = Utils.isNumber(num);

        // then
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "2b", "cc3", "2,1", " ", " 22", "84 ", "-a", "+ccc", "1.", "5S5"})
    void test_isNumberFalse(String num) {
        // when
        boolean actual = Utils.isNumber(num);

        // then
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void test_isNumberNull(String num) {
        // when
        boolean actual = Utils.isNumber(num);

        // then
        assertFalse(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"12", "+44", "-90", "0", "00", "-0"})
    void test_isWholeNumberTrue(String num) {
        // when
        boolean actual = Utils.isWholeNumber(num);

        // then
        assertTrue(actual);
    }

    @ParameterizedTest
    @ValueSource(strings = {"aa", "2b", "cc3", "2,1", " ", " 22", "84 ", "-a", "+ccc", "1.", "5S5", "4.2", "0.75",
            "55.0", ".89", "+33.22", "+.76", "-20.20", "-.666"})
    void test_isWholeNumberFalse(String num) {
        // when
        boolean actual = Utils.isWholeNumber(num);

        // then
        assertFalse(actual);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void test_isWholeNumberNull(String num) {
        // when
        boolean actual = Utils.isWholeNumber(num);

        // then
        assertFalse(actual);
    }

    @Test
    void test_getGetter() throws NoSuchFieldException, NoSuchMethodException {
        // when
        Method actual = Utils.getGetter(DataSet.class.getDeclaredField("strategy"));

        // then
        assertEquals(DataSet.class.getMethod("getStrategy"), actual);
    }

    @ParameterizedTest
    @CsvSource({
            "1,2,1,50%",
            "1,3,1,33.3%",
            "1,5,0,20%",
            "5,27,2,18.52%",
            "3,200,0,2%",
            "1,10000,2,0.01%",
            "1,500,5,0.2%"
    })
    void test_toPercentage(int dividend, int divisor, int digits, String expected) {
        // when
        String actual = Utils.toPercentage(dividend, divisor, digits);

        // then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @CsvSource({
            "37,39,0.95,2,[83.11% - 98.58%]",
            "47306,100000,0.95,3,[46.997% - 47.616%]",
            "3,100,0.9,1,[1.2% - 7.3%]",
            "694656942,2056239764,0.99,4,[33.7802% - 33.7856%]"
    })
    void test_toBinomialConfidenceRange(int successes, int sampleSize, float confidence, int digits, String expected) {
        // when
        String actual = Utils.toBinomialConfidenceRange(successes, sampleSize, confidence, digits);

        // then
        assertEquals(expected, actual);
    }

}
