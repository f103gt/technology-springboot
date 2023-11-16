package com.technology.exception.general.exceptions;

public class ObjectAlreadyRevoked extends RuntimeException{
    public ObjectAlreadyRevoked(String message) {
        super(message);
    }
}
