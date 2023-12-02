package com.technology.exception.handler;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleSecurityException(Exception ex) {
        log.error(ex.getMessage(),ex.getCause());
        ex.printStackTrace();
        ProblemDetail errorDetail = ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                        "An error occurred while processing the request.");
        errorDetail.setProperty("reason", "Internal server error");
        return errorDetail;
    }

    @ExceptionHandler({AccessDeniedException.class, AuthenticationException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleSecurityExceptions(RuntimeException exception) {
        Map<String, String> errors = new HashMap<>();
        errors.put("error", exception.getMessage());
        return errors;
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors()
                .forEach(error -> {
                    String fieldName = ((FieldError) error).getField();
                    String errorMessage = error.getDefaultMessage();
                    errors.put(fieldName, errorMessage);
                });
        return errors;
    }
}

