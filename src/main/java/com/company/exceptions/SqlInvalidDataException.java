package com.company.exceptions;

public class SqlInvalidDataException extends RuntimeException {
    public SqlInvalidDataException(String message) {
        super(message);
    }
}
