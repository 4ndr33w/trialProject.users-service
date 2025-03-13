package ru.authorization.auth.models.Dtos;

import java.util.Date;

public class ErrorDto {

    private Date timestamp;
    private int statusCode;
    private String path;
    private String message;

    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
    public int getStatusCode() {
        return statusCode;
    }
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ErrorDto{" +
                "timestamp=" + timestamp +
                ", statusCode=" + statusCode +
                ", path='" + path + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
