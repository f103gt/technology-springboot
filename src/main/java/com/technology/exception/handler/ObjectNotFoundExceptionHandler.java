package com.technology.exception.handler;

import com.technology.exception.error.ErrorObject;
import com.technology.exception.error.ErrorObjectCreator;
import com.technology.exception.general.exceptions.ObjectNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ObjectNotFoundExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ObjectNotFoundException.class})
    public ResponseEntity<ErrorObject> handleRoleNotFoundExceptions(ObjectNotFoundException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorObject error = ErrorObjectCreator.createNewErrorObject(exception,status);
        return ResponseEntity.status(status).body(error);
    }
}
