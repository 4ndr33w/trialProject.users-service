package ru.authorization.auth.utils.exceptions.global;

import java.util.Date;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.authorization.auth.models.Dtos.ErrorDto;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.utils.exceptions.DatabaseTransactionException;
import ru.authorization.auth.utils.exceptions.EmailAlreadyBusyException;
import ru.authorization.auth.utils.exceptions.UserNotFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    //private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleException(HttpServletRequest request, Exception exception) {

        ErrorDto error = new ErrorDto();

        error.setTimestamp(new Date());
        error.setPath(request.getServletPath());
        error.setMessage(exception.getMessage());

        if (exception.getMessage().contains("JDBC exception executing SQL"))
        {
            error.setMessage(StaticResources.DATABASE_ACCESS_EXCEPTION_MESSAGE);
        }

        if(exception instanceof EmailAlreadyBusyException ||
        exception instanceof DatabaseTransactionException ||
        exception instanceof UserNotFoundException)
        {
            error.setStatusCode(HttpStatus.CONFLICT.value());
        }

        else {
            error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        //log.error(error.toString());
        //LOGGER.error(exception.getMessage(), exception);

        return error;
    }

    @ExceptionHandler(EmailAlreadyBusyException.class)
    public ResponseEntity<String> handleEmailAlreadyBusyException(EmailAlreadyBusyException e) {
        //log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
    @ExceptionHandler(DatabaseTransactionException.class)
    public ResponseEntity<String> handleDatabaseTransactionException(DatabaseTransactionException e) {
        //log.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
