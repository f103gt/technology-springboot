package com.technology.exception.error;

import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ErrorObjectCreator {
    public static ErrorObject createNewErrorObject(Exception exception, HttpStatus status) {
        ErrorObject error = new ErrorObject();
        error.setMessage(exception.getMessage());
        error.setHttpStatus(status);
        error.setTimestamp(ZonedDateTime.now(ZoneId.of("Z")));
        return error;
    }
}
