package com.company.exceptions;

import java.io.IOException;

import static com.company.utils.FileUtils.logException;

public class BoardStateDoesntMatchException extends RuntimeException {
    public BoardStateDoesntMatchException(String message) {
        super(message);
        try {
            logException(this);
        } catch (IOException ignored) {}
    }
}
