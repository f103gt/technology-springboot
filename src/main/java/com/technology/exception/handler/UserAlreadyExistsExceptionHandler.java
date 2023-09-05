package com.technology.exception.handler;

import com.technology.exception.error.ErrorObject;
import com.technology.exception.error.ErrorObjectCreator;
import com.technology.registration.errors.UserAlreadyExistsException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice
public class UserAlreadyExistsExceptionHandler {

    //@ResponseStatus(HttpStatus.BAD_REQUEST)
    //@ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorObject> handleUserAlreadyExistsException(UserAlreadyExistsException exception) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorObject error = ErrorObjectCreator.createNewErrorObject(exception,status);
        return ResponseEntity.status(status).body(error);
    }

}
