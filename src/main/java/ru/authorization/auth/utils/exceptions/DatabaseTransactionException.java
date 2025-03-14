package ru.authorization.auth.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DatabaseTransactionException extends RuntimeException {
    public DatabaseTransactionException(String message) {

        super(message);
    }
}
