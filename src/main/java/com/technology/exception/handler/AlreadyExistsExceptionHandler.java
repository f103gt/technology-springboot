package com.technology.exception.handler;

import com.technology.category.exceptions.CategoryAlreadyExistsException;
import com.technology.exception.error.ErrorObject;
import com.technology.exception.error.ErrorObjectCreator;
import com.technology.exception.general.exceptions.ObjectAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AlreadyExistsExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(CategoryAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleAlreadyExistsException(ObjectAlreadyExistsException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorObject error = ErrorObjectCreator.createNewErrorObject(exception,status);
        return ResponseEntity.status(status).body(error);
    }
}
