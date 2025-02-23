package ru.authorization.auth.utils.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EmailAlreadyBusyException extends RuntimeException {
    public EmailAlreadyBusyException(String message) {

        super(message);
        log.error(message);
    }
}
