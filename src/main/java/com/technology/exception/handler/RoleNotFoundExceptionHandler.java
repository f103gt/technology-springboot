package com.technology.exception.handler;

import com.technology.exception.error.ErrorObject;
import com.technology.exception.error.ErrorObjectCreator;
import com.technology.user.registration.errors.RoleNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RoleNotFoundExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = {RoleNotFoundException.class})
    public ResponseEntity<ErrorObject> handleRoleNotFoundExceptions(RoleNotFoundException exception) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        ErrorObject error = ErrorObjectCreator.createNewErrorObject(exception,status);
        return ResponseEntity.status(status).body(error);
    }
}
