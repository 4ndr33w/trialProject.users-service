package ru.authorization.auth.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailAlreadyBusyException extends RuntimeException {
    public EmailAlreadyBusyException(String message) {

        super(message);
        //log.error(message);
    }
}
