package com.company.utils;

import com.company.datasets.other.loot.LootType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.stat.interval.ConfidenceInterval;
import org.apache.commons.math3.stat.interval.WilsonScoreInterval;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class Utils {
    public static String join(String[] strings) {
        return join(strings, "");
    }

    public static String join(String[] strings, String sep) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                result.append(sep);
            }
            result.append(strings[i]);
        }
        return result.toString();
    }

    public static String join(String[] strings1, String[] strings2, String sep) {
        if (strings1.length != strings2.length) {
            throw new IllegalArgumentException("Can't join two arrays of different length");
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings1.length; i++) {
            if (i != 0) {
                result.append(sep);
            }
            result.append(strings1[i]).append("(").append(strings2[i]).append(")");
        }
        return result.toString();
    }

    public static String join(List<String> strings) {
        return join(strings, "");
    }

    public static String join(List<String> strings, String sep) {
        return join(strings.toArray(new String[0]), sep);
    }

    // case insensitive
    public static boolean contains(String[] strings, String option) {
        for (String string : strings) {
            if (string.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(Collection<String> strings, String option) {
        for (String string : strings) {
            if (string.equalsIgnoreCase(option)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(LootType[] types, LootType option) {
        for (LootType type : types) {
            if (type.equals(option)) {
                return true;
            }
        }
        return false;
    }

    public static boolean contains(Map<String, String> strings, String option) {
        return contains(strings.keySet(), option) || contains(strings.values(), option);
    }

    public static String join(String[] strings, String sep, int start) {
        StringBuilder result = new StringBuilder();
        for (int i = start; i < strings.length; i++) {
            if (i != start) {
                result.append(sep);
            }
            result.append(strings[i]);
        }
        return result.toString();
    }

    public static String toJson(Object object) {
        ObjectMapper om = new ObjectMapper();
        try {
            return om.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T parseJson(String json, Class<T> clazz) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, clazz);
    }

    public static char[] splitToChars(String string, char sep) {
        List<Character> chars = new ArrayList<>();
        for (char c : string.toCharArray()) {
            if (c != sep) {
                chars.add(c);
            }
        }
        char[] result = new char[chars.size()];
        for (int i = 0; i < chars.size(); i++) {
            result[i] = chars.get(i);
        }
        return result;
    }

    public static boolean isNumber(String string) {
        return string != null && string.matches("[-+]?\\d*\\.?\\d+");
    }

    public static boolean isWholeNumber(String string) {
        return string != null && string.matches("[-+]?\\d+");
    }

    // returns substring with only removing first and last non-whitespace character
    public static String cutOne(String string) {
        String trimmed = string.trim();
        return trimmed.substring(1, trimmed.length() - 1).trim();
    }

    // returns the getter method of a field, assuming it's name is getFieldName or isFieldName for primitive booleans
    // returns null if no such getter exists
    public static Method getGetter(Field field) {
        String getterName = field.getName();
        if (field.getType() == Boolean.TYPE) {
            getterName = "is" + getterName.substring(0, 1).toUpperCase() + getterName.substring(1);
        } else {
            getterName = "get" + getterName.substring(0, 1).toUpperCase() + getterName.substring(1);
        }
        try {
            Method getter = field.getDeclaringClass().getMethod(getterName);
            if (getter.getReturnType() != field.getType()) {
                throw new NoSuchMethodException("Return type of getter doesn't match");
            }
            return getter;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static Collection<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>(Arrays.stream(clazz.getDeclaredFields()).toList());

        Class<?> currClass = clazz.getSuperclass();
        while (currClass != null) {
            fields.addAll(Arrays.stream(currClass.getDeclaredFields()).toList());
            currClass = currClass.getSuperclass();
        }

        return fields;
    }

    public static String toPercentage(int dividend, int divisor, int digits) {
        return numbertoStringWithComma(Math.round((double) (dividend * 100 * (long) Math.pow(10, digits)) / (double) divisor), digits) + "%";
    }

    public static String toBinomialConfidenceRange(int successes, int sampleSize, double confidence, int digits) {
        ConfidenceInterval interval = new WilsonScoreInterval().createInterval(sampleSize, successes, confidence);
        long tens = (long) Math.pow(10, digits);
        long lower = Math.round(100 * tens * interval.getLowerBound());
        long higher = Math.round(100 * tens * interval.getUpperBound());
        return "[" + numbertoStringWithComma(lower, digits) + "% - " + numbertoStringWithComma(higher, digits) + "%]";
    }

    private static String numbertoStringWithComma(long number, int digits) {
        if (digits <= 0) return String.valueOf(number);
        String rep = String.valueOf(number);
        int pos = rep.length() - digits;

        if (pos < 0) return "0." + new String(new char[-pos]).replace('\0', '0') + rep;
        if (pos == 0) {
            while (rep.endsWith("0")) rep = rep.substring(0, rep.length() - 1);
            return "0." + rep;
        }

        String before = rep.substring(0, pos);
        String after = rep.substring(pos);
        while (after.endsWith("0")) after = after.substring(0, after.length() - 1);

        if (after.isEmpty()) return before;
        return before + "." + after;
    }

    public static String joinRepeating(String string, String sep, int amount) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < amount; i++) {
            if (i != 0) result.append(sep);
            result.append(string);
        }
        return result.toString();
    }

    public static <T> boolean compareCollections(Collection<T> collection1, Collection<T> collection2) {
        if (collection1.size() != collection2.size()) return false;
        List<T> copy = new ArrayList<>(collection1);
        for (T t : collection2) if (!copy.remove(t)) return false;
        return true;
    }

}
