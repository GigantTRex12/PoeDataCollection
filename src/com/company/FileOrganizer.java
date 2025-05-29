package com.company;

import com.company.Exceptions.FileAlreadyExistsException;
import com.company.Exceptions.FileCannotBeReadException;
import com.company.Exceptions.FileCannotBeWrittenException;
import com.company.Exceptions.FileInvalidModeException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.FileWriter;
import java.io.IOException;

import static com.company.Utilities.join;

public class FileOrganizer {
    String filename;
    private boolean readOnly;
    private boolean append;
    private boolean overwrite;
    private boolean createOnly;
    private File file;

    public FileOrganizer(String filename, char mode) throws IOException {
        this.filename = filename;
        this.readOnly = false;
        this.append = false;
        this.overwrite = false;
        this.createOnly = false;
        if (mode == 'x') {
            this.createOnly = true;
        }
        else if (mode == 'w') {
            this.overwrite = true;
        }
        else if (mode == 'r') {
            this.readOnly = true;
        }
        /*else if (mode == 'a') {
            this.append = true;
        }*/
        else {
            throw new FileInvalidModeException("Invalid mode given");
        }

        this.file = new File(filename);

        if (this.readOnly) {
            if (!this.file.canRead()) {
                throw new FileCannotBeReadException("File cannot be read");
            }
        }

        else if (this.append) {
            if (!this.file.canRead()) {
                throw new FileCannotBeReadException("File cannot be read");
            }
            if (!this.file.canWrite()) {
                throw new FileCannotBeWrittenException("File cannot be written");
            }
        }

        else if (this.overwrite) {
            if (this.file.exists()) {
                this.file.delete();
            }
            if (!this.file.createNewFile()) {
                throw new IOException("Something went wrong");
            }
            if (!this.file.canWrite()) {
                throw new FileCannotBeWrittenException("File cannot be written");
            }
        }

        else if (this.createOnly) {
            if (!this.file.createNewFile()) {
                throw new FileAlreadyExistsException("File already exists!");
            }
            if (!this.file.canWrite()) {
                throw new FileCannotBeWrittenException("File cannot be written");
            }
        }
    }

    public static FileOrganizer open(String filename) throws IOException {
        return open(filename, 'r');
    }

    public static FileOrganizer open(String filename, char mode) throws IOException {
        return new FileOrganizer(filename, mode);
    }

    public static void append(String filename, String string) throws IOException {
        FileOrganizer fileRead = open(filename);
        String rep = fileRead.read() + "\n" + string;
        fileRead.close();

        FileOrganizer fileWrite = open(filename, 'w');
        fileWrite.write(rep);
        fileWrite.close();
    }

    public String read() throws IOException {
        return join(readLines(), "\n");
    }

    public String readLine() throws IOException {
        if (!(this.readOnly || this.append)) {
            throw new FileInvalidModeException("File Mode invalid to read File");
        }
        if (!this.file.canRead()) {
            throw new FileCannotBeReadException("File cannot be read");
        }

        Scanner scanner = new Scanner(this.file);
        if (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            scanner.close();
            return line;
        }
        else {
            return null;
        }
    }

    public List<String> readLines() throws IOException {
        if (!(this.readOnly || this.append)) {
            throw new FileInvalidModeException("File Mode invalid to read File");
        }
        if (!this.file.canRead()) {
            throw new FileCannotBeReadException("File cannot be read");
        }

        Scanner scanner = new Scanner(this.file);
        List<String> lines = new ArrayList<String>();
        while (scanner.hasNextLine()) {
            lines.add(scanner.nextLine());
        }
        scanner.close();
        return lines;
    }

    public void write(String string) throws IOException {
        if (!(this.append || this.createOnly || this.overwrite)) {
            throw new FileInvalidModeException("File Mode invalid to write File");
        }
        if (!this.file.canWrite()) {
            throw new FileCannotBeWrittenException("File cannot be written");
        }

        if (this.append) {
            String rep = read() + "\n" + string;
            FileWriter writer = new FileWriter(this.filename);
            writer.write(rep);
            writer.close();
        }
        else if (this.createOnly || this.overwrite) {
            FileWriter writer = new FileWriter(this.filename);
            writer.write(string);
            writer.close();
        }
    }

    public void close() throws IOException {
        this.readOnly = false;
        this.append = false;
        this.overwrite = false;
        this.createOnly = false;
    }
}
