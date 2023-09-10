package com.technology.category.exceptions;

public class ParentCategoryNotFoundException extends RuntimeException{
    public ParentCategoryNotFoundException(String message){
        super(message);
    }
}
