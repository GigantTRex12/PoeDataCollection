package com.company.exceptions;

import java.io.IOException;

public class FileAlreadyExistsException extends IOException {
    public FileAlreadyExistsException(String errorMessage) {
        super(errorMessage);
    }
}
