package com.technology.exception.handler.authentication;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ResponseExceptionHandler {
    @ExceptionHandler({ResponseStatusException.class})
    public ProblemDetail handleSecurityException(ResponseStatusException ex) {
        ProblemDetail errorDetail =ProblemDetail
                .forStatusAndDetail(HttpStatusCode.valueOf(ex.getStatusCode().value()),
                        ex.getMessage());
        errorDetail.setProperty("reason", ex.getReason());
        return errorDetail;
    }
}
