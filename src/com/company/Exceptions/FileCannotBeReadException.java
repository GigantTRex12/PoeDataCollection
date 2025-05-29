package com.company.Exceptions;

import java.io.IOException;

public class FileCannotBeReadException extends IOException {
    public FileCannotBeReadException(String errorMessage) {
        super(errorMessage);
    }
}
