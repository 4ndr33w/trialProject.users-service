package ru.authorization.auth.utils.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailAlreadyBusyException extends RuntimeException {
    public EmailAlreadyBusyException(String message) {

        super(message);
        log.error(message);
    }
}
