package com.technology.user.requests.general;

import com.technology.address.regitration.request.AddressRegistrationRequest;
import com.technology.user.requests.user.UserRegistrationRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private UserRegistrationRequest userRegistrationRequest;
    private AddressRegistrationRequest addressRegistrationRequest;
}
