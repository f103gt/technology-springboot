package com.technology.validation.email.annotation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EmailExistenceChecker implements ConstraintValidator<EmailExists, Object> {
    /*private final UserDao userDAO;

    @Autowired
    public EmailExistenceChecker(UserDao userDAO) {
        this.userDAO = userDAO;
    }*/

    @Override
    public boolean isValid(Object object, ConstraintValidatorContext context) {
       /* String email = (String) object;
        User user = userDAO.getUserByParameter("email",email);
        return user == null;*/
        return false;
    }

    @Override
    public void initialize(EmailExists constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
