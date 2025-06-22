package com.company.exceptions;

public class SomethingIsWrongWithMyCodeException extends RuntimeException {
    public SomethingIsWrongWithMyCodeException(String errorMessage) {
        super(errorMessage);
    }
}
