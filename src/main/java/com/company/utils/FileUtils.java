package com.company.utils;

import com.company.exceptions.FileAlreadyExistsException;
import com.company.exceptions.FileCannotBeWrittenException;
import lombok.SneakyThrows;
import net.bytebuddy.implementation.bytecode.Throw;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.company.utils.Utils.toJson;

public class FileUtils {
    public static void create(String filename) throws IOException {
        File file = new File(filename);
        if (!file.createNewFile()) {
            throw new FileAlreadyExistsException("File already exists!");
        }
    }

    private static void create(File file) throws IOException {
        if (!file.createNewFile()) {
            throw new FileAlreadyExistsException("File already exists!");
        }
    }

    public static void createNew(String filename) throws IOException {
        File file = new File(filename);
        file.createNewFile();
    }

    public static void create(String filename, String content) throws IOException {
        File file = new File(filename);
        create(file);
        overwrite(file, content);
    }

    private static void create(File file, String content) throws IOException {
        create(file);
        overwrite(file, content);
    }

    public static String read(String filename) throws FileNotFoundException {
        File file = new File(filename);
        return read(file);
    }

    private static String read(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getPath() + " doesn't exist");
        }
        Scanner scanner = new Scanner(file);
        StringBuilder result = new StringBuilder();
        while (scanner.hasNextLine()) {
            result.append(scanner.nextLine()).append("\n");
        }
        scanner.close();
        return result.toString().strip();
    }

    public static List<String> readLines(String filename) throws FileNotFoundException {
        File file = new File(filename);
        return readLines(file);
    }

    private static List<String> readLines(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new FileNotFoundException("File " + file.getPath() + " doesn't exist");
        }
        Scanner scanner = new Scanner(file);
        List<String> result = new ArrayList<>();
        while (scanner.hasNextLine()) {
            result.add(scanner.nextLine());
        }
        return result;
    }

    public static void overwrite(String filename, String content) throws IOException {
        File file = new File(filename);
        overwrite(file, content);
    }

    private static void overwrite(File file, String content) throws IOException {
        if (file.exists()) {
            if (!file.canWrite()) {
                throw new FileCannotBeWrittenException("File " + file.getPath() + " cannot be written");
            }
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            writer.close();
        }
        else {
            create(file, content);
        }
    }

    public static void append(String filename, String content) throws IOException {
        File file = new File(filename);
        if (file.exists()) {
            String fullContent = read(file) + "\n" + content;
            fullContent = fullContent.strip();
            overwrite(file, fullContent);
        }
        else {
            create(file, content);
        }
    }

    public static void append(String filename, List<?> data) {
        StringBuilder dataRep = new StringBuilder();
        for (Object dataSet : data) {
            dataRep.append(toJson(dataSet)).append("\n");
        }
        dataRep = new StringBuilder(dataRep.toString().trim());
        try {
            append(filename, dataRep.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logException(Throwable e) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        logException(e.getClass().getName() + "\n" + e.getMessage() + "\n" + sw);
    }

    private static void logException(String message) throws IOException {
        append("Log/exceptions.txt", message);
    }

    public static void initLogs() throws IOException {
        logException("\n------------------------------\n" + LocalDateTime.now());
    }

}
