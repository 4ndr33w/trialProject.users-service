package ru.authorization.auth.utils.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseTransactionException extends RuntimeException {
    public DatabaseTransactionException(String message) {

        super(message);
        log.error(message);
    }
}
