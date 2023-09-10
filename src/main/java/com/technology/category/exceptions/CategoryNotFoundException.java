package com.technology.category.exceptions;

import com.technology.exception.general.exceptions.ObjectNotFoundException;

public class CategoryNotFoundException extends ObjectNotFoundException {
    public CategoryNotFoundException(String message){
        super(message);
    }
}
