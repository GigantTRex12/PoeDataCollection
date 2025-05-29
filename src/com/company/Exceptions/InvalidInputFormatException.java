package com.company.Exceptions;

import java.io.IOException;

public class InvalidInputFormatException extends IOException {
    public InvalidInputFormatException(String errorMessage) {
        super(errorMessage);
    }
}
