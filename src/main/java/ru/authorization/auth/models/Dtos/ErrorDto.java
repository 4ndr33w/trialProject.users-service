package ru.authorization.auth.models.Dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ErrorDto {

    private Date timestamp;
    private int statusCode;
    private String path;
    private String message;
}
