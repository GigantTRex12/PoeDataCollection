package main.com.company.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import main.com.company.datasets.loot.LootType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
}
