package de.alexanderhofmeister.rechnungen.model;

public class BusinessException extends Throwable {

    private final String message;

    public BusinessException(String message) {
        this.message = message;
    }
}
