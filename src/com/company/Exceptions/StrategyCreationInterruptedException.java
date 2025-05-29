package com.company.Exceptions;

import java.io.IOException;

public class StrategyCreationInterruptedException extends IOException {
    public StrategyCreationInterruptedException(String errorMessage) {
        super(errorMessage);
    }
}
