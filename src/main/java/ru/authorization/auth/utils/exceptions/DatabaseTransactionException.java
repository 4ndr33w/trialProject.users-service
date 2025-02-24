package ru.authorization.auth.utils.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseTransactionException extends RuntimeException {
    public DatabaseTransactionException(String message) {

        super(message);
        log.error(message);
    }
}
