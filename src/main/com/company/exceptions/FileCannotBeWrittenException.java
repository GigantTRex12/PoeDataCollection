package main.com.company.exceptions;

import java.io.IOException;

public class FileCannotBeWrittenException extends IOException {
    public FileCannotBeWrittenException(String errorMessage) {
        super(errorMessage);
    }
}
