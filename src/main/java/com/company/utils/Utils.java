package com.company.utils;

import com.company.datasets.other.loot.LootType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

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
        if (digits <= 0) return Math.round(100 * (float) dividend / (float) divisor) + "%";
        float result = (float) dividend / (float) divisor;
        result *= 100;
        double tens = Math.pow(10, digits);
        result *= tens;
        String rep = String.valueOf(Math.round(result));
        return rep.substring(0, rep.length() - digits) + "." + rep.substring(rep.length() - digits) + "%";
    }

    // 95% confidence
    public static String toBinomialConfidenceRange(int successes, int sampleSize, int digits) {
        float p = (float) successes / (float) sampleSize;
        double lowEnd = p - 1.96 * Math.sqrt((p * (1 - p)) / (float) sampleSize);
        double highEnd = p + 1.96 * Math.sqrt((p * (1 - p)) / (float) sampleSize);
        if (highEnd > 1) highEnd = 1;

        // rounding and converting to percentage
        lowEnd *= 100;
        highEnd *= 100;
        double tens = Math.pow(10, digits);
        lowEnd *= tens;
        highEnd *= tens;
        String repLow = String.valueOf(Math.round(lowEnd));
        String repHigh = String.valueOf(Math.round(highEnd));
        repLow = repLow.substring(0, repLow.length() - digits) + "." + repLow.substring(repLow.length() - digits) + "%";
        repHigh = repHigh.substring(0, repHigh.length() - digits) + "." + repHigh.substring(repHigh.length() - digits) + "%";
        return "(" + repLow + " - " + repHigh + ")";
    }

}
