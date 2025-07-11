package com.company.exceptions;

import java.io.IOException;

import static com.company.utils.FileUtils.logException;

public class InvalidInputFormatException extends IOException {
    public InvalidInputFormatException(String errorMessage) {
        super(errorMessage);
        try {
            logException(this);
        } catch (IOException ignored) {}
    }
}
