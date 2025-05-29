package com.company.Exceptions;

import java.io.IOException;

public class FileAlreadyExistsException extends IOException {
    public FileAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
