package com.company.utils;

import java.io.ByteArrayInputStream;
import java.util.*;
import java.util.regex.Pattern;

import static com.company.utils.Utils.*;

public class IOUtils {
    private static Scanner scanner = new Scanner(System.in);

    public static void print(String string) {
        System.out.println(string);
    }

    public static void print(String[] strings) {
        for (String string : strings) {
            print(string);
        }
    }

    public static void print(String[] strings, String sep) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                result.append(sep);
            }
            result.append(strings[i]);
        }
        print(result.toString());
    }

    public static void print(Object object) {
        print(object.toString());
    }

    public static void print(Object[] objects) {
        for (Object object : objects) {
            print(object);
        }
    }

    public static void print(Object[] objects, String sep) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < objects.length; i++) {
            if (i != 0) {
                result.append(sep);
            }
            result.append(objects[i].toString());
        }
        print(result.toString());
    }

    public static void printList(List<?> objects) {
        printList(objects, "\n");
    }

    public static void printList(List<?> objects, String sep) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < objects.size(); i++) {
            if (i != 0) {
                result.append(sep);
            }
            result.append(objects.get(i).toString());
        }
        print(result.toString());
    }

    public static String input() {
        return scanner.nextLine();
    }

    public static String input(String message) {
        print(message);
        return input();
    }

    public static String input(String message, String[] options) {
        String result;
        String rep = message + "\nOptions: " + join(options, "; ");
        while (true) {
            result = input(rep);
            if (contains(options, result)) {
                break;
            }
            print("Invalid input, try again");
        }
        return result;
    }

    public static String input(String message, String[] options, boolean forceOptions) {
        if (forceOptions) {
            return input(message, options);
        }
        String rep = message + "\nOptions: " + join(options, "; ");
        return input(rep);
    }

    public static String input(String message, Collection<String> options) {
        return input(message, options.toArray(new String[0]));
    }

    public static String input(String message, Collection<String> options, boolean forceOptions) {
        return input(message, options.toArray(new String[0]), forceOptions);
    }

    public static String input(String[] options) {
        String result;
        while (true) {
            result = input();
            if (contains(options, result)) {
                break;
            }
            print("Invalid input, try again");
        }
        return result;
    }

    public static String input(String message, Map<String, String> options) {
        List<String> optionsReps = new ArrayList<>();
        options.forEach((k, v) -> optionsReps.add(k + "(" + v + ")"));
        String rep = message + "\nOptions: " + join(optionsReps, "; ");
        String result;
        while (true) {
            result = input(rep);
            if (contains(options, result)) {
                break;
            }
            print("Invalid input, try again");
        }
        return result;
    }

    public static String input(String message, Map<String, String> options, boolean forceOptions) {
        if (forceOptions) {
            return input(message, options);
        }
        List<String> optionsReps = new ArrayList<>();
        options.forEach((k, v) -> optionsReps.add(v + "(" + k + ")"));
        String rep = message + "\nOptions: " + join(optionsReps, "; ");
        return input(rep);
    }

    public static String input(String message, String regex) {
        return input(message, Pattern.compile(regex, Pattern.CASE_INSENSITIVE));
    }

    public static String input(String message, Pattern pattern) {
        String result;
        String rep = message + "\nFormat: " + pattern.pattern();
        while (true) {
            result = input(rep);
            if (pattern.matcher(result).find()) {
                break;
            }
            print("Invalid input, try again");
        }
        return result;
    }

    public static String multilineInput() {
        StringBuilder sb = new StringBuilder();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.isEmpty()) {
                break;
            }
            sb.append(line).append("\n");
        }
        if (sb.length() == 0) {
            return "";
        }
        sb.deleteCharAt(sb.length() - 1); // delete the last \n character
        return sb.toString();
    }

    public static String multilineInput(String message) {
        print(message);
        return multilineInput();
    }

    public static int inputInteger(String message) {
        while (true) {
            String inp = input(message);
            if (isWholeNumber(inp)) {
                return Integer.parseInt(inp);
            }
            print("Invalid Format, an integer is expected");
        }
    }

    public static void setInputStream(String input) { // Is needed for tests
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        scanner = new Scanner(System.in); // otherwise setting a new InputStream breaks things
    }
}
