package com.technology.registration.registration.requests.general;

import com.technology.registration.registration.requests.address.AddressRegistrationRequest;
import com.technology.registration.registration.requests.user.UserRegistrationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private UserRegistrationRequest userRegistrationRequest;
    private AddressRegistrationRequest addressRegistrationRequest;
}
