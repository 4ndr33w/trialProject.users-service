package ru.authorization.auth.utils.exceptions;

public class EmailAlreadyBusyException extends RuntimeException {
    public EmailAlreadyBusyException(String message) {
        super(message);
    }
}
