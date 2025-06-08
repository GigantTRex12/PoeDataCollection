package main.com.company.exceptions;

public class InvalidLootFormatException extends InvalidInputFormatException {
    public InvalidLootFormatException(String errorMessage) {
        super(errorMessage);
    }
}
