package com.technology.product.exceptions;

import com.technology.exception.general.exceptions.ObjectNotFoundException;

public class ProductNotFoundException extends ObjectNotFoundException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}
