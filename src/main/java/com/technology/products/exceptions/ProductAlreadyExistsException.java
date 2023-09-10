package com.technology.products.exceptions;

import com.technology.exception.general.exceptions.AlreadyExistsException;

public class ProductAlreadyExistsException extends AlreadyExistsException {
    public ProductAlreadyExistsException(String message) {
        super(message);
    }
}
