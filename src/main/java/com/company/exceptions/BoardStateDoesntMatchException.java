package com.company.exceptions;

public class BoardStateDoesntMatchException extends RuntimeException {
    public BoardStateDoesntMatchException(String message) {
        super(message);
    }
}
