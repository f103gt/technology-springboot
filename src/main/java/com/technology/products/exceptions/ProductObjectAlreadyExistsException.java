package com.technology.products.exceptions;

import com.technology.exception.general.exceptions.ObjectAlreadyExistsException;

public class ProductObjectAlreadyExistsException extends ObjectAlreadyExistsException {
    public ProductObjectAlreadyExistsException(String message) {
        super(message);
    }
}
