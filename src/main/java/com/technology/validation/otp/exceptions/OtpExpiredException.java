package com.technology.validation.otp.exceptions;

import com.technology.exception.general.exceptions.ObjectExpiredException;

public class OtpExpiredException extends ObjectExpiredException {
    public OtpExpiredException(String message) {
        super(message);
    }
}
