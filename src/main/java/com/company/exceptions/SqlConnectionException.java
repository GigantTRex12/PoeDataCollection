package com.company.exceptions;

public class SqlConnectionException extends RuntimeException {
    public SqlConnectionException(String message) {
        super(message);
    }
    public SqlConnectionException(Exception e) {
        super(e);
    }
}
