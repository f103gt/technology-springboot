package com.technology.exception.handler;

import com.technology.category.exceptions.ParentCategoryNotFoundException;
import com.technology.exception.error.ErrorObject;
import com.technology.exception.error.ErrorObjectCreator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ParentCategoryNotFoundExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {ParentCategoryNotFoundException.class})
    public ResponseEntity<ErrorObject> handleParentCategoryNotFoundExceptions(ParentCategoryNotFoundException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorObject error = ErrorObjectCreator.createNewErrorObject(exception,status);
        return ResponseEntity.status(status).body(error);
    }
}
