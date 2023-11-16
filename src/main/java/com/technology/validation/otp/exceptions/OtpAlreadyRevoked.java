package com.technology.validation.otp.exceptions;

import com.technology.exception.general.exceptions.ObjectAlreadyRevoked;

public class OtpAlreadyRevoked extends ObjectAlreadyRevoked {
    public OtpAlreadyRevoked(String message) {
        super(message);
    }
}
