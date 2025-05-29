package com.company;

import java.util.Collection;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

import static com.company.Utilities.contains;
import static com.company.Utilities.join;

public class IOSystem {
    public static void print(String string) {
        System.out.println(string);
    }

    public static void print(String[] strings) {
        for (String string : strings) {
            print(string);
        }
    }

    public static void print(String[] strings, String sep) {
        String result = "";
        for (int i = 0; i < strings.length; i++) {
            if (i != 0) {
                result += sep;
            }
            result += strings[i];
        }
        print(result);
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
        String result = "";
        for (int i = 0; i < objects.length; i++) {
            if (i != 0) {
                result += sep;
            }
            result += objects[i].toString();
        }
        print(result);
    }

    public static void printList(List<? extends Object> objects) {
        printList(objects, "\n");
    }

    public static void printList(List<? extends Object> objects, String sep) {
        String result = "";
        for (int i = 0; i < objects.size(); i++) {
            if (i != 0) {
                result += sep;
            }
            result += objects.get(i).toString();
        }
        print(result);
    }

    public static String input() {
        Scanner scanner = new Scanner(System.in);
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

    public static String input(String message, String regex) {
        return input(message, Pattern.compile(regex));
    }

    public static String input(String message, Pattern pattern) {
        String result;
        while (true) {
            result = input(message);
            if (pattern.matcher(result).find()) {
                break;
            }
            print("Invalid input, try again");
        }
        return result;
    }

    public static String multilineInput() {
        Scanner scanner = new Scanner(System.in);
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

    public static String multiLineInput(String message) {
        print(message);
        return multilineInput();
    }

    public static int inputInteger(String message) {
        int value;
        while (true) {
            try {
                value = Integer.parseInt(input(message));
            }
            catch (NumberFormatException e) {
                print("Invalid Format, an integer is expected");
                continue;
            }
            break;
        }
        return value;
    }
}
