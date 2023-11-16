package com.technology.validation.email.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EmailExistenceChecker.class)
public @interface EmailExists {
    String message() default "User with this email already exists!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
