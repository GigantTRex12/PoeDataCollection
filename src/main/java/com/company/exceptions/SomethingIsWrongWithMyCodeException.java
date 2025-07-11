package com.company.exceptions;

import java.io.IOException;

import static com.company.utils.FileUtils.logException;

public class SomethingIsWrongWithMyCodeException extends RuntimeException {
    public SomethingIsWrongWithMyCodeException(String errorMessage) {
        super(errorMessage);
        try {
            logException(this);
        } catch (IOException ignored) {}
    }
}
