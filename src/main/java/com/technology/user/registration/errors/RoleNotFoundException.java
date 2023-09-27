package com.technology.user.registration.errors;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(String message){
        super(message);
    }
}
