package com.company.Exceptions;

import java.io.IOException;

public class FileInvalidModeException extends IOException {
    public FileInvalidModeException(String errorMessage) {
        super(errorMessage);
    }
}
