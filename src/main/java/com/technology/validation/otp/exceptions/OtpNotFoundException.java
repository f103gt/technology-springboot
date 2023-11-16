package com.technology.validation.otp.exceptions;

import com.technology.exception.general.exceptions.ObjectNotFoundException;

public class OtpNotFoundException extends ObjectNotFoundException {
    public OtpNotFoundException(String message) {
        super(message);
    }
}
