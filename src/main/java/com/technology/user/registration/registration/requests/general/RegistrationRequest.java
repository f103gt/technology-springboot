package com.technology.user.registration.registration.requests.general;

import com.technology.address.regitration.request.AddressRegistrationRequest;
import com.technology.user.registration.registration.requests.user.UserRegistrationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private UserRegistrationRequest userRegistrationRequest;
    private AddressRegistrationRequest addressRegistrationRequest;
}
