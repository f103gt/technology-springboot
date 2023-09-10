package com.technology.exception.general.exceptions;

public class ObjectAlreadyExistsException extends RuntimeException{
    public ObjectAlreadyExistsException(String message) {
        super(message);
    }
}
