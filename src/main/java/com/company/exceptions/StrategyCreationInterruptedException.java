package com.company.exceptions;

import java.io.IOException;

public class StrategyCreationInterruptedException extends IOException {
    public StrategyCreationInterruptedException(String errorMessage) {
        super(errorMessage);
    }
}
