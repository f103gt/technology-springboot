package com.technology.category.exceptions;

import com.technology.exception.general.exceptions.ObjectNotFoundException;

public class ParentCategoryNotFoundException extends ObjectNotFoundException {
    public ParentCategoryNotFoundException(String message){
        super(message);
    }
}
