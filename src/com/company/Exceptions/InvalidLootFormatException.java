package com.company.Exceptions;

public class InvalidLootFormatException extends InvalidInputFormatException {
    public InvalidLootFormatException(String errorMessage) {
        super(errorMessage);
    }
}
