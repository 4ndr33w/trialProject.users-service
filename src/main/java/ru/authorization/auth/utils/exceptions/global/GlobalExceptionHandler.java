package ru.authorization.auth.utils.exceptions.global;

import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.authorization.auth.models.Dtos.ErrorDto;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorDto handleException(HttpServletRequest request, Exception exception) {

        ErrorDto error = new ErrorDto();

        error.setTimestamp(new Date());
        error.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setPath(request.getServletPath());
        error.setMessage(exception.getMessage());

        LOGGER.error(exception.getMessage(), exception);

        return error;
    }
}
