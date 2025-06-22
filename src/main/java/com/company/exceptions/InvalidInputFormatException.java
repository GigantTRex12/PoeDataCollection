package com.company.exceptions;

import java.io.IOException;

public class InvalidInputFormatException extends IOException {
    public InvalidInputFormatException(String errorMessage) {
        super(errorMessage);
    }
}
