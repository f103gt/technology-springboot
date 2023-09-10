package com.technology.category.exceptions;

import com.technology.exception.general.exceptions.ObjectAlreadyExistsException;

public class CategoryAlreadyExistsException extends ObjectAlreadyExistsException {
    public CategoryAlreadyExistsException(String message) {
        super(message);
    }
}
