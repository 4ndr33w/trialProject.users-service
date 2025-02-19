package ru.authorization.auth.utils.exceptions;

public class DatabaseTransactionException extends RuntimeException {
    public DatabaseTransactionException(String message) {
        super(message);
    }
}
