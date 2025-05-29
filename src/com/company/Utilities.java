package com.company;

import com.company.DataTypes.LootType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

public class Utilities {
    public static String join(String[] strings) {
        return join(strings, "");
    }

    public static String join(String[] strings, String sep) {
        String result = "";
        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                result += sep;
            }
            result += strings[i];
        }
        return result;
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
            if (string.toLowerCase().equals(option.toLowerCase())) {
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

    public static String join(String[] strings, String sep, int start) {
        String result = "";
        for (int i = start; i < strings.length; i++) {
            if (i != start) {
                result += sep;
            }
            result += strings[i];
        }
        return result;
    }

    public static String toJson(Object object) {
        ObjectMapper om = new ObjectMapper();
        try {
            String json = om.writeValueAsString(object);
            return json;
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
        List<Character> chars = new ArrayList<Character>();
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
        return string.matches("[-+]?[0-9]*\\.?[0-9]+");
    }
}
